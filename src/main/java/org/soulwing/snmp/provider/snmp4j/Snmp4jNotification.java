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

import org.soulwing.snmp.SnmpNotification;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.VarbindCollection;

/**
 * An {@link SnmpNotification} built from an SNMP4j PDU.
 *
 * @author Carl Harris
 */
class Snmp4jNotification implements SnmpNotification {

  private final SnmpNotification.Type type;
  private final SnmpTarget peer;
  private final VarbindCollection varbinds;

  public Snmp4jNotification(SnmpNotification.Type type,
      SnmpTarget target, VarbindCollection varbinds) {
    this.type = type;
    this.peer = target;
    this.varbinds = varbinds;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public SnmpTarget getPeer() {
    return peer;
  }

  @Override
  public VarbindCollection getVarbinds() {
    return varbinds;
  }

  @Override
  public String toString() {
    return String.format("{ type=%s peer=%s varbinds=%s }", type, peer,
        varbinds);
  }

}
