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
 * An object that produces a description of each of the index objects 
 * contained in the identifier for an object that is defined in
 * a conceptual table.
 *
 * @author Carl Harris
 */
public interface IndexExtractor {

  /**
   * Produces a description of each of the index objects contained in 
   * an OID for an object that is defined in a conceptual table.
   * @param oid OID of a conceptual table column value
   * @return array of index descriptors, one per index component
   */
  IndexDescriptor[] extractIndexes(String oid);
  
}
