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
package org.soulwing.snmp;

/**
 * A provider of specialized MIB support for handling SNMPv1 TRAP PDUs.
 * <p>
 * SNMPv1 traps contain several fields that are represented in a manner similar
 * to variable bindings, but for which no managed object type is defined.  An
 * implementation of this class provides basic formatting support for these
 * fields.
 * <p>
 * This API is primarily intended to support
 * {@link org.soulwing.snmp.provider.SnmpProvider} implementations.
 *
 */
public interface MibTrapV1Support {

  /**
   * Gets a formatter for the {@code generic-trap} field of a V1 TRAP PDU.
   * @return formatter
   */
  Formatter getGenericTrapFormatter();

  /**
   * Gets a formatter for the {@code specific-trap} field of a V1 TRAP PDU.
   * @return formatter
   */
  Formatter getSpecificTrapFormatter();

  /**
   * Gets a formatter for the {@code timestamp} field of a V1 TRAP PDU.
   * @return formatter
   */
  Formatter getTimestampFormatter();

}
