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
 * A value associated with an SNMP managed object.
 *
 * @author Carl Harris
 */
public interface ObjectValue {

  /**
   * Gets the MIB syntax type identifier of the object.
   * @return syntax identifier
   */
  int getSyntax();

  /**
   * Coerces the value of the bound object to a (signed) integer.
   * @return integer value of the bound object
   */
  int asInt();

  /**
   * Coerces the value of the bound object to a (signed) long.
   * @return long value of the bound object
   */
  long asLong();

  /**
   * Coerces the value of the bound object to a display string.
   * <p>
   * The implementation will incorporate the application textual convention
   * and display hint (if available) of the corresponding MIB object
   * definition.
   * @return string representation of the bound object
   */
  String asString();

  /**
   * Gets the value of the object as one of the fundamental types of SMI.
   * <p>
   * This method returns a value for each SMI type as follows:
   * <ul>
   * <li>OBJECT IDENTIFIER &mdash; {@code int} array of the OID components</li>
   * <li>OCTET STRING &mdash; {@code byte} array containing the object's octets</li>
   * <li>INTEGER &mdash; {@code Long} value of the object</li>
   * <li>NULL &mdash; {@code null}</li>
   * <li>IpAddress &mdash; {@code byte} array containing the address's octets</li>
   * <li>Counter &mdash; {@code Long} value of the object</li>
   * <li>Gauge &mdash; {@code Long} value of the object</li>
   * <li>TimeTicks &mdash; {@code Long} value of the object</li>
   * <li>Opaque &mdash; {@code byte} array containing the object's octets</li>
   * </ul>
   * @return value representation
   */
  Object toObject();

  /**
   * Sets the value of the object.
   * @param value
   */
  void set(Object value);

}
