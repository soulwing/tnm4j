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

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An ordered collection of {@link Varbind} objects resulting from
 * an SNMP operation.  The collection supports both list-like and map-like
 * access methods.
 *
 * @author Carl Harris
 */
public interface VarbindCollection extends Iterable<Varbind> {

  /**
   * Gets the number of varbinds in this collection.
   * @return number of varbinds
   */
  int size();

  /**
   * Gets the set of keys (MIB names and/or dotted-decimal object identifiers
   * for which a varbind exists in this collection).
   * @return key set 
   */
  Set<String> keySet();

  /**
   * Gets a varbind from the collection.
   * @param index zero-based index of the varbind to retrieve 
   * @return varbind
   * @throws IndexOutOfBoundsException if {@code index < 0} or 
   *  {@index >= size}.
   */
  Varbind get(int index);
  
  /**
   * Gets a varbind from the collection.
   * @param oid MIB name or dotted-decimal object identifier of the object
   *    to retrieve; the specified value must be equal to one of values
   *    passed to the SNMP operation that resulted in this varbind collection
   * @return varbind or {@code null} if no varbind exists with the given 
   *    name
   */
  Varbind get(String oid);
  
  /**
   * Gets the contents of this collection as a immutable {@link List} with
   * the same size and order as this collection.
   * @return list representation
   */
  List<Varbind> asList();

  /**
   * Gets the contents of this collection as a immutable {@link Map} indexed
   * using the same keys present in this collection.
   * @return map representation
   */
  Map<String, Varbind> asMap();

  /**
   * Creates a list of object identifiers for a GETNEXT or GETBULK operation.
   * <p>
   * The resulting list is a concatenation of the specified {@code oids} and
   * the object identifiers associated with the varbinds in this collection,
   * starting at {@code index=oids.size()}.  This method allows you to easily
   * construct a list of object identifiers where non-repeating objects
   * identifiers are specified in the given {@code oids} and repeating object
   * identifiers are extracted from the previously retrieved varbinds.
   *
   * @param oids array of object identifiers to prepend to the resulting list
   * @return list of object identifiers starting with {@code oids} followed by
   *    the OIDS associated with the varbinds in this collection, starting
   *    at {@code start}.
   */
  List<String> nextIdentifiers(List<String> oids);

  /**
   * Creates a list of object identifiers for a GETNEXT or GETBULK operation.
   * <p>
   * The resulting list is a concatenation of the specified {@code oids} and
   * the object identifiers associated with the varbinds in this collection,
   * starting at {@code index=oids.size()}.  This method allows you to easily
   * construct a list of object identifiers where non-repeating objects
   * identifiers are specified in the given {@code oids} and repeating object
   * identifiers are extracted from the previously retrieved varbinds.
   *
   * @param oids array of object identifiers to prepend to the resulting list
   * @return list of object identifiers starting with {@code oids} followed by
   *    the OIDS associated with the varbinds in this collection, starting
   *    at {@code start}.
   */
  List<String> nextIdentifiers(String... oids);

}
