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

import org.soulwing.snmp.SnmpListener;
import org.soulwing.snmp.SnmpNotification;
import org.soulwing.snmp.SnmpNotificationEvent;

/**
 * An immutable {@link SnmpNotification} implementation.
 *
 * @author Carl Harris
 */
class Snmp4jNotificationEvent implements SnmpNotificationEvent {

  private final SnmpListener source;
  private final SnmpNotification subject;

  public Snmp4jNotificationEvent(SnmpListener source, SnmpNotification subject) {
    this.source = source;
    this.subject = subject;
  }

  @Override
  public SnmpListener getSource() {
    return source;
  }

  @Override
  public SnmpNotification getSubject() {
    return subject;
  }

  @Override
  public String toString() {
    return String.format("{ source=%s, subject=%s }", source, subject);
  }

}
