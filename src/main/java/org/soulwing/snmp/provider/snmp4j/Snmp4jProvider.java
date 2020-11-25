/*
 * tnm4j - Simplified SNMP API for Java
 * Copyright (C) 2012 Carl Harris, Jr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.soulwing.snmp.provider.snmp4j;

import static org.soulwing.snmp.provider.snmp4j.Snmp4jLogger.logger;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.snmp4j.SNMP4JSettings;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.soulwing.snmp.Mib;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpException;
import org.soulwing.snmp.SnmpFactory;
import org.soulwing.snmp.SnmpFactoryConfig;
import org.soulwing.snmp.SnmpListener;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.SnmpTargetConfig;
import org.soulwing.snmp.provider.SnmpProvider;

/**
 * An {@link SnmpProvider} based on SNMP4j.
 *
 * @author Carl Harris
 */
public class Snmp4jProvider implements SnmpProvider, DisposeListener {

  public static final String PROVIDER_NAME = "snmp4j";

  public static final String USE_SINGLE_SESSION =
      PROVIDER_NAME + ".use.single.session";

  private static final TargetStrategy[] targetStrategies = {
      new CommunityTargetStrategy(),
      new UserTargetStrategy()
  };

  private static final PduFactoryStrategy[] pduFactoryStrategies = {
      new SnmpV3PduFactoryStrategy(),
      new SnmpV2cPduFactoryStrategy(),
      new SnmpV1PduFactoryStrategy()
  };

  private final Lock lock = new ReentrantLock();

  private final IdentityHashMap<Object, Object> refs =
      new IdentityHashMap<Object, Object>();

  private boolean useSingleSession;

  private volatile Snmp snmp;

  static {
    SNMP4JSettings.setThreadFactory(
        new Snmp4jThreadFactory(SnmpFactory.getInstance().getThreadFactory()));
    SNMP4JSettings.setTimerFactory(
        new ScheduledExecutorServiceTimerFactory(
            SnmpFactory.getInstance().getScheduledExecutorService()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return PROVIDER_NAME;
  }

  @Override
  public void init(SnmpFactoryConfig config) {
    Object useSingleSession = config.getProperty(USE_SINGLE_SESSION);
    if (useSingleSession instanceof Boolean) {
      this.useSingleSession = (Boolean) useSingleSession;
    }
    else if (useSingleSession != null) {
      this.useSingleSession = Boolean.valueOf(useSingleSession.toString());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpContext newContext(SnmpTarget target, SnmpTargetConfig config,
      Mib mib) {
    lock.lock();
    try {
      Target snmp4jTarget = createTarget(target);
      PduFactory pduFactory = createPduFactory(target);
      snmp4jTarget.setAddress(createAddress(target));

      // timeout will be handled in SessionWrapper
      snmp4jTarget.setRetries(0);
      snmp4jTarget.setTimeout(Integer.MAX_VALUE);

      Snmp4jContext context = new Snmp4jContext(target, config, mib,
          getSnmp(), snmp4jTarget, pduFactory,
          new SimpleVarbindFactory(mib), this);
      refs.put(context, context);
      return context;
    }
    finally {
      lock.unlock();
    }
  }

  @Override
  public SnmpListener newListener(String address, int port, Mib mib) {
    lock.lock();
    try {
      Address listenAddress = address == null ?
          new UdpAddress(port) : createAddress(address, port);
      Snmp4jListener listener = new Snmp4jListener(getSnmp(),
          listenAddress, new Snmp4jNotificationEventFactory(
          new SimpleVarbindFactory(mib)), this);
      listener.open();
      refs.put(listener, listener);
      return listener;
    }
    finally {
      lock.unlock();
    }
  }

  private Snmp getSnmp() {
    if (snmp == null) {
      try {
        lock.lock();
        try {
          if (snmp == null) {
            snmp = new Snmp(new DefaultUdpTransportMapping());
            snmp.listen();
            logger.info("started SNMP listener");
          }
        }
        catch (IOException ex) {
          throw new SnmpException("error creating SNMP session", ex);
        }
      }
      finally {
        lock.unlock();
      }
    }
    return snmp;
  }

  @Override
  public void close() {
    lock.lock();
    try {
      refs.clear();
      shutdown();
    }
    finally {
      lock.unlock();
    }
  }

  @Override
  public void onDispose(Object ref) {
    lock.lock();
    try {
      refs.remove(ref);
      if (!refs.isEmpty() || useSingleSession) return;
      shutdown();
    }
    finally {
      lock.unlock();
    }
  }

  private void shutdown() {
    try {
      if (snmp != null) {
        snmp.close();
        Snmp4jLogger.logger.info("SNMP listener shutdown");
        snmp = null;
      }
    }
    catch (IOException ex) {
      Snmp4jLogger.logger.warn("while closing SNMP session: {}", ex.toString(), ex);
    }
  }

  private static Target createTarget(SnmpTarget target) {
    for (TargetStrategy strategy : targetStrategies) {
      Target snmp4jTarget = strategy.newTarget(target);
      if (snmp4jTarget != null) return snmp4jTarget;
    }
    throw new RuntimeException("unsupported target type");
  }

  private static Address createAddress(SnmpTarget target) {
    return createAddress(target.getAddress(), target.getPort());
  }

  private static Address createAddress(String address, int port) {
    Assert.notNull(address, "address is required");
    StringBuilder sb = new StringBuilder();
    sb.append("udp:");
    sb.append(address);
    sb.append("/").append(port);
    return GenericAddress.parse(sb.toString());
  }

  private static PduFactory createPduFactory(SnmpTarget target) {
    for (PduFactoryStrategy strategy : pduFactoryStrategies) {
      PduFactory factory = strategy.newPduFactory(target);
      if (factory != null) return factory;
    }
    throw new RuntimeException("unsupported target type");
  }

}
