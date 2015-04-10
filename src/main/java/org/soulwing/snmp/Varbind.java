/*
 * tnm4j - Simplified SNMP API for Java
 * Copyright (C) 2012 Carl Harris, Jr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.soulwing.snmp;

public interface Varbind extends ObjectValue {

  /**
   * Gets the MIB name of the bound object.
   * @return name
   */
  String getName();
  
  /**
   * Gets the object identifier of the named object as a dotted decimal string.
   * @return OID string
   */
  String getOid();
  
  /**
   * Gets the MIB syntax type identifier of the object.
   * @return
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
   * @return
   */
  Object toObject();
  
  /**
   * Gets the bindings that represent the index values the bound value.
   * @return if the bound value is a member of a conceptual table, the
   *    variable bindings representing the object's table index are returned;
   *    an empty array is returned for an object that is not a table member
   */
  Varbind[] getIndexes();
  
}
