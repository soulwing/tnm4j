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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.snmp4j.SNMP4JSettings;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.soulwing.snmp.Mib;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpFactory;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.SnmpTargetConfig;

/**
 * A factory that produces {@link Snmp4jContext} objects.
 *
 * @author Carl Harris
 */
class Snmp4jContextFactory {

  private static final TargetStrategy[] targetStrategies = {
      new CommunityTargetStrategy(),
      new UserTargetStrategy()
  };
  
  private static final PduFactoryStrategy[] pduFactoryStrategies = {
      new SnmpV2cPduFactoryStrategy(),
      new SnmpV3PduFactoryStrategy()
  };
  
  private static final Lock lock = new ReentrantLock();
  private static final Set<SnmpTarget> targets = new HashSet<SnmpTarget>(); 
  
  private static volatile Snmp snmp;
  private static volatile TransportMapping<?> transportMapping;
  
  static {
    SNMP4JSettings.setThreadFactory(
        new Snmp4jThreadFactory(
            SnmpFactory.getInstance().getThreadFactory()));
    SNMP4JSettings.setTimerFactory(
        new ScheduledExecutorServiceTimerFactory(
            SnmpFactory.getInstance().getScheduledExecutorService()));
  }
  
  /**
   * Creates a new context for the given target, configuration, and MIB.
   * @param target target agent
   * @param config SNMP configuration
   * @param mib MIB to associate with context
   * @return new context 
   */
  public static Snmp4jContext newContext(SnmpTarget target, 
      SnmpTargetConfig config, Mib mib) {
    try {
      Target snmp4jTarget = createTarget(target);
      PduFactory pduFactory = createPduFactory(target);
      snmp4jTarget.setAddress(createAddress(target));
      snmp4jTarget.setRetries(config.getRetries());
      snmp4jTarget.setTimeout(config.getTimeout());
      Snmp4jContext context = new Snmp4jContext(target, config, mib, 
          getSnmp(target), snmp4jTarget, pduFactory);
      return context;
    }
    catch (IOException ex) {
      throw new RuntimeException("failed to create context", ex);
    }
  }

  private static Snmp getSnmp(SnmpTarget target) throws IOException {
    if (snmp == null) {
      lock.lock();
      try {
        if (snmp == null) {
          logger.info("starting SNMP listener");
          snmp = new Snmp(getTransportMapping());        
        }
      }
      finally {
        lock.unlock();
      }
    }
    targets.add(target);
    return snmp;
  }
  
  private static TransportMapping<?> getTransportMapping() throws IOException {
    if (transportMapping == null) {
      lock.lock();
      try {
        if (transportMapping == null) {
          transportMapping = new DefaultUdpTransportMapping();
          transportMapping.listen();
        }
      }
      finally {
        lock.unlock();
      }
    }
    return transportMapping;
  }
  
  private static Target createTarget(SnmpTarget target) {
    for (TargetStrategy strategy : targetStrategies) {
      Target snmp4jTarget = strategy.newTarget(target);
      if (snmp4jTarget != null) return snmp4jTarget;
    }
    throw new RuntimeException("unsupported target type");
  }
  
  private static Address createAddress(SnmpTarget target) {
    Assert.notNull(target.getAddress(), "address is required");
    StringBuilder sb = new StringBuilder();
    sb.append("udp:");
    sb.append(target.getAddress());
    sb.append("/").append(target.getPort());
    
    return GenericAddress.parse(sb.toString());    
  }

  private static PduFactory createPduFactory(SnmpTarget target) {
    for (PduFactoryStrategy strategy : pduFactoryStrategies) {
      PduFactory factory = strategy.newPduFactory(target);
      if (factory != null) return factory;
    }
    throw new RuntimeException("unsupported target type");
  }

  public static void dispose(SnmpContext context) {
    lock.lock();
    try {
      targets.remove(context.getTarget());
      if (!targets.isEmpty()) return;
      logger.info("stopping SNMP listener");
      if (snmp != null) {
        try {
          snmp.close();
        }
        catch (IOException ex) {
          ex.printStackTrace(System.err);
        }
        finally {
          snmp = null;
        }
      }
      if (transportMapping != null) {
        try {
          transportMapping.close();
        }
        catch (IOException ex) {
          ex.printStackTrace(System.err);
        }
        finally {
          transportMapping = null;
        }
      }
    }
    finally {
      lock.unlock();
    }
  }
  
}
