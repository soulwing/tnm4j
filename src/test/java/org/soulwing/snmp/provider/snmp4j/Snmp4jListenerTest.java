/*
 * File created on Apr 9, 2015
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.smi.Address;
import org.soulwing.snmp.SnmpException;
import org.soulwing.snmp.SnmpNotificationEvent;
import org.soulwing.snmp.SnmpNotificationHandler;

/**
 * Unit tests for {@link Snmp4jListener}.
 *
 * @author Carl Harris
 */
public class Snmp4jListenerTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery() {
    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  @Mock
  private SnmpNotificationEvent event;

  @Mock
  private SnmpNotificationEventFactory eventFactory;

  @Mock
  private SnmpNotificationHandler handler;

  @Mock
  private SnmpNotificationHandler otherHandler;

  @Mock
  private DisposeListener disposeListener;

  @Mock
  private Snmp snmp;

  @Mock
  private Address listenAddress;

  @Mock
  private CommandResponderEvent snmp4jEvent;

  @Mock
  private PDU pdu;

  private Snmp4jListener listener;

  @Before
  public void setUp() throws Exception {
    listener = new Snmp4jListener(snmp, listenAddress, eventFactory,
        disposeListener);
  }

  @Test
  public void testOpen() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(snmp).addNotificationListener(listenAddress, listener);
        will(returnValue(true));
      }
    });
    listener.open();
  }

  @Test(expected = SnmpException.class)
  public void testOpenWhenAddNotificationListenerFails() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(snmp).addNotificationListener(listenAddress, listener);
        will(returnValue(false));
      }
    });
    listener.open();
  }

  @Test
  public void testAddAndRemoveListener() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(handler).handleNotification(event);
        will(returnValue(true));
      }
    });

    listener.addHandler(handler);
    listener.notifyHandlers(event);
    listener.removeHandler(handler);
    listener.notifyHandlers(event);
  }

  @Test
  public void testProcessPdu() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(eventFactory).newEvent(listener, snmp4jEvent);
        will(returnValue(event));
        oneOf(handler).handleNotification(event);
        will(returnValue(true));
      }
    });

    listener.addHandler(handler);
    listener.processPdu(snmp4jEvent);
  }

  @Test
  public void testNotifyPriorityHandlerOnly() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(handler).handleNotification(event);
        will(returnValue(true));
      }
    });

    listener.addHandler(otherHandler);
    listener.addHandler(handler, 0);
    listener.notifyHandlers(event);
  }

  @Test
  public void testNotifyAllWhenNoneHandles() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(handler).handleNotification(event);
        will(returnValue(false));
        oneOf(otherHandler).handleNotification(event);
        will(returnValue(false));
      }
    });

    listener.addHandler(handler);
    listener.addHandler(otherHandler);
    listener.notifyHandlers(event);
  }

  @Test
  public void testClose() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(snmp).removeNotificationListener(listenAddress);
        oneOf(disposeListener).onDispose(listener);
      }
    });

    listener.close();
    listener.close();                   // should be silently ignored
    listener.processPdu(snmp4jEvent);   // should be silently ignored

    try {
      listener.addHandler(handler);
      fail("expected IllegalStateException");
    }
    catch (IllegalStateException ex) {
      assertTrue(true);
    }
  }

}
