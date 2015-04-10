/*
 * File created on Apr 10, 2015
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

import org.soulwing.snmp.Formatter;
import org.soulwing.snmp.ObjectValue;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.SnmpV1Trap;
import org.soulwing.snmp.VarbindCollection;

/**
 * An {@link SnmpV1Trap} implemented on top of SNMP4j.
 *
 * @author Carl Harris
 */
class Snmp4jV1Trap extends Snmp4jNotification implements SnmpV1Trap {

  private String enterprise;
  private String agentAddress;
  private ObjectValue genericType;
  private ObjectValue specificType;
  private ObjectValue timestamp;

  public Snmp4jV1Trap(SnmpTarget target, VarbindCollection varbinds) {
    super(Type.TRAPv1, target, varbinds);
  }

  @Override
  public String getEnterprise() {
    return enterprise;
  }

  public void setEnterprise(String enterprise) {
    this.enterprise = enterprise;
  }

  @Override
  public String getAgentAddress() {
    return agentAddress;
  }

  public void setAgentAddress(String agentAddress) {
    this.agentAddress = agentAddress;
  }

  @Override
  public ObjectValue getGenericTrap() {
    return genericType;
  }

  public void setGenericType(ObjectValue genericType) {
    this.genericType = genericType;
  }

  @Override
  public ObjectValue getSpecificTrap() {
    return specificType;
  }

  public void setSpecificType(ObjectValue specificType) {
    this.specificType = specificType;
  }

  @Override
  public ObjectValue getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(ObjectValue timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return String.format("{ type=%s peer=%s enterprise=%s agent=%s genericType=%s specificType=%s timestamp=%s varbinds=%s }",
        getType(), getPeer(), enterprise, agentAddress, genericType,
        specificType, timestamp, getVarbinds());
  }

}
