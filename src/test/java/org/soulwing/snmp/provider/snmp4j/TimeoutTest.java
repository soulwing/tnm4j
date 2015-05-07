/*
 * File created on Apr 23, 2015
 *
 * Copyright (c) 2015 Carl Harris, Jr
 * and others as noted
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.soulwing.snmp.provider.snmp4j;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramSocket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.soulwing.snmp.BlockingQueueSnmpCompletionService;
import org.soulwing.snmp.Mib;
import org.soulwing.snmp.MibFactory;
import org.soulwing.snmp.SimpleSnmpTargetConfig;
import org.soulwing.snmp.SimpleSnmpV2cTarget;
import org.soulwing.snmp.SnmpCompletionService;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpEvent;
import org.soulwing.snmp.TimeoutException;
import org.soulwing.snmp.VarbindCollection;

/**
 * Integration tests for SNMP4j request timeout functionality.
 *
 * @author Carl Harris
 */
public class TimeoutTest {

  private static final long TIMEOUT = 500;
  private static final String LOCALHOST = "127.0.0.1";
  private final Snmp4jProvider provider = new Snmp4jProvider();

  private Mib mib;

  @Before
  public void setUp() throws Exception {
    mib = MibFactory.getInstance().newMib();
    mib.load("SNMPv2-MIB");
  }

  @After
  public void tearDown() throws Exception {
    provider.close();
  }

  @Test
  public void testSynchronousTimeoutSingleAttempt() throws Exception {
    DatagramSocket socket = newTargetSocket();
    try {
      final int attempts = 1;
      SnmpContext context = provider.newContext(
          newTarget(socket), newConfig(attempts - 1, TIMEOUT), mib);
      final long start = System.currentTimeMillis();
      try {
        context.get("sysDescr.0").get();
        fail("expected TimeoutException");
      }
      catch (TimeoutException ex) {
        assertTrue(true);
        assertThat(System.currentTimeMillis() - start,
            is(greaterThanOrEqualTo(attempts * TIMEOUT)));
      }
    }
    finally {
      socket.close();
    }
  }

  @Test
  public void testSynchronousTimeoutMultipleAttempts()
      throws Exception {
    DatagramSocket socket = newTargetSocket();
    try {
      final int attempts = 3;
      SnmpContext context = provider.newContext(
          newTarget(socket), newConfig(attempts - 1, TIMEOUT), mib);
      final long start = System.currentTimeMillis();
      try {
        context.get("sysDescr.0").get();
        fail("expected TimeoutException");
      }
      catch (TimeoutException ex) {
        assertTrue(true);
        assertThat(System.currentTimeMillis() - start,
            is(greaterThanOrEqualTo(attempts * TIMEOUT)));
      }
    }
    finally {
      socket.close();
    }
  }

  @Test
  public void testAsynchronousTimeoutMultipleTargets()
      throws Exception {
    final int targetCount = 50;
    final DatagramSocket[] sockets = new DatagramSocket[targetCount];
    try {
      for (int i = 0; i < targetCount; i++) {
        sockets[i] = newTargetSocket();
      }

      final SnmpCompletionService<VarbindCollection> completionService =
          new BlockingQueueSnmpCompletionService<>();

      for (int i = 0; i < targetCount; i++) {
        SnmpContext context = provider.newContext(newTarget(sockets[i]),
            newConfig(0, TIMEOUT), mib);
        completionService.submit(context.newGet("sysDescr.0"));
      }

      while (!completionService.isIdle()) {
        final SnmpEvent<VarbindCollection> event = completionService.take();
        try {
          event.getResponse().get();
          fail("expected timeout");
        }
        catch (TimeoutException ex) {
          assertTrue(true);
        }
        finally {
          event.getContext().close();
        }
      }

    }
    finally {
      for (int i = 0; i < targetCount; i++) {
        if (sockets[i] == null) continue;
        sockets[i].close();
      }
    }
  }

  private SimpleSnmpTargetConfig newConfig(int retries, long timeout) {
    SimpleSnmpTargetConfig config = new SimpleSnmpTargetConfig();
    config.setRetries(retries);
    config.setTimeout(timeout);
    return config;
  }

  private SimpleSnmpV2cTarget newTarget(DatagramSocket socket) {
    SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
    target.setPort(socket.getLocalPort());
    target.setAddress(LOCALHOST);
    target.setCommunity("public");
    return target;
  }

  private static DatagramSocket newTargetSocket()
      throws IOException {
    DatagramSocket socket = null;
    while (socket == null) {
      int port = (int) (Math.random() * (65536 - 1024)) + 1024;
      try {
        socket = new DatagramSocket(port);
      }
      catch (BindException ex) {
        assert true;
      }
    }
    return socket;
  }

}
