/*
 * File created on Apr 14, 2015
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

import org.soulwing.snmp.Mib;
import org.soulwing.snmp.MibFactory;
import org.soulwing.snmp.SnmpFactory;
import org.soulwing.snmp.SnmpListener;
import org.soulwing.snmp.SnmpNotificationEvent;
import org.soulwing.snmp.SnmpNotificationHandler;

/**
 * An example that shows how to receive SNMP TRAP or INFORM notifications.
 *
 * @author Carl Harris
 */
public class Example09_NotificationHandling {

  static final int PORT = 11162;

  public static void main(String[] args) throws Exception {
    Mib mib = MibFactory.getInstance().newMib();
    mib.load("SNMPv2-MIB");

    SnmpListener listener = SnmpFactory.getInstance().newListener(PORT, mib);
    try {
      listener.addHandler(new SnmpNotificationHandler() {
        @Override
        public Boolean handleNotification(SnmpNotificationEvent event) {
          System.out.println("received a notification: " + event);
          return true;
        }
      });

      System.out.println("waiting for notifications");

      Thread.sleep(60000L);     // wait for some notifications to arrive

      System.out.println("shutting down");

    }
    finally {
      listener.close();     // listeners must be closed when no longer needed
    }

    SnmpFactory.getInstance().close();
  }

}