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
package org.soulwing.snmp;

/**
 * An {@link SnmpNotification} that has the additional properties associated
 * with an SNMPv1 TRAP.
 *
 * @author Carl Harris
 */
public interface SnmpV1Trap extends SnmpNotification {

  /**
   * Gets the enterprise OID associated with the trap.
   * @return dotted-decimal OID string
   */
  String getEnterprise();

  /**
   * Gets the agent address reported in the trap.
   * <p>
   * While this is usually an IP address, the specification allows any network
   * address.
   * @return agent network address
   */
  String getAgentAddress();

  /**
   * Gets the generic trap type.
   * @return trap type
   */
  ObjectValue getGenericTrap();

  /**
   * Gets the specific trap type.
   * @return trap type
   */
  ObjectValue getSpecificTrap();

  /**
   * Gets the timestamp reported in the trap.
   * @return timestamp
   */
  ObjectValue getTimestamp();

}
