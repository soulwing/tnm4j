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

import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.soulwing.snmp.SnmpListener;
import org.soulwing.snmp.SnmpNotificationEvent;

/**
 * A factory that produces {@link SnmpNotificationEvent} objects that correspond
 * to the contents of a notification PDU.
 *
 * @author Carl Harris
 */
interface SnmpNotificationEventFactory {

  SnmpNotificationEvent newEvent(SnmpListener listener,
      CommandResponderEvent event);

}
