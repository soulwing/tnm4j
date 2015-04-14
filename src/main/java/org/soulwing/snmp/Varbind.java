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
   * Gets the bindings that represent the index values the bound value.
   * @return if the bound value is a member of a conceptual table, the
   *    variable bindings representing the object's table index are returned;
   *    an empty array is returned for an object that is not a table member
   */
  Varbind[] getIndexes();
  
}
