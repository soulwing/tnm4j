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
 * An SNMP notification (trap, inform).
 *
 * @author Carl Harris
 */
public interface SnmpNotification {

  enum Type {
    /** SNMPv1 trap */
    TRAPv1,
    /** SNMPv2 trap */
    TRAP,
    /** SNMPv2 inform */
    INFORM
  }

  /**
   * Gets the notification type.
   * <p>
   * When the type is {@link Type#TRAPv1} the notification may be cast to
   * {@link SnmpV1Trap} to access the details unique to SNMPv1 traps.
   * @return notification type
   */
  Type getType();

  /**
   * Gets a target that describes the sender of the notification.
   * <p>
   * This could be used to create a context that can be used to perform
   * operations against the sender after receiving a notification.
   * @return target describing the notification sender
   */
  SnmpTarget getPeer();

  /**
   * Gets the collection of variable bindings received in the notification.
   * @return variable bindings
   */
  VarbindCollection getVarbinds();

}
