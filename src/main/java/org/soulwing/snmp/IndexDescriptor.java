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

/**
 * A descriptor for a conceptual table index object.
 *
 * @author Carl Harris
 */
public interface IndexDescriptor {

  /**
   * Gets the object identifier of the index value described by the receiver.
   * <p>
   * This method returns only that portion of the indexed object's OID that 
   * corresponds to the index value.
   * @return object identifier substring representing the index value
   */
  String getOid();
  
  /**
   * Gets the SMI syntax indicator for the index object described by the 
   * receiver.
   * @return syntax indicator
   */
  int getSyntax();
  
  /**
   * Gets the encoded form of the index value described by the receiver.
   * <p>
   * This method returns only that portion of the indexed object's OID that
   * corresponds to the index value. 
   * 
   * @return an integer array containing the OID-encoded index value
   */
  int[] getEncoded();
  
  /**
   * Gets the state of a flag indicating whether the length of the index
   *    value described by the receiver is implied (i.e. not included in
   *    the object identifier).
   * @return flag state
   */
  boolean isImplied();
  
}
