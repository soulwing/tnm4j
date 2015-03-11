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

import java.io.IOException;
import java.util.List;

/**
 * An API for synchronous SNMP operations.
 *
 * @author Carl Harris
 */
public interface SnmpOperations {

  /**
   * Performs an SNMP GET operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return retrieved varbinds in the same order and with the same keys
   *   as the requested objects
   * @throws IOException
   */
  VarbindCollection get(List<String> oids) throws IOException;

  /**
   * Performs an SNMP GET operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return list of retrieved varbinds in the same order as the requested
   *    objects
   * @throws IOException
   */
  VarbindCollection get(String... oids) throws IOException;

  /**
   * Performs an SNMP GETNEXT operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return retrieved varbinds in the same order and with the same keys
   *   as the requested objects
   * @throws IOException
   */
  VarbindCollection getNext(List<String> oids) throws IOException;

  /**
   * Performs an SNMP GETNEXT operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return retrieved varbinds in the same order and with the same keys
   *   as the requested objects
   * @throws IOException
   */
  VarbindCollection getNext(String... oids) throws IOException;

  /**
   * Performs an SNMP GETBULK operation.
   * @param nonRepeaters number of non-repeating objects at the beginning of
   *    {@code oids}.
   * @param maxRepetitions maximum number of repetitions to retrieve for the
   *    repeating objects in {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch; the first {@code nonRepeaters} identifiers in the list
   *    are assumed to be non-repeating objects
   * @return retrieved varbinds in the same order and with the same keys
   *   as the requested objects
   * @throws IOException
   */
  VarbindCollection getBulk(int nonRepeaters, int maxRepetitions, 
      List<String> oids) throws IOException;

  /**
   * Performs an SNMP GETBULK operation.
   * @param nonRepeaters number of non-repeating objects at the beginning of
   *   {@code oids}.
   * @param maxRepetitions maximum number of repetitions to retrieve for the
   *    repeating objects in {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch; the first {@code nonRepeaters} identifiers in the 
   *    list are assumed to be for non-repeating objects
   * @return retrieved varbinds in the same order and with the same keys
   *   as the requested objects
   * @throws IOException
   */
  VarbindCollection getBulk(int nonRepeaters, int maxRepetitions, 
      String... oids) throws IOException;

  /**
   * Performs a walk of a MIB conceptual table.
   * <p>
   * This is a high level operation that can be used to retrieve all 
   * rows of a conceptual table.
   * @param nonRepeaters number of non-repeating objects at the beginning
   *   of {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers; the first 
   *   {@code nonRepeaters} identifiers in the list are assumed to be for 
   *   non-repeating objects
   * @return list of table rows (each row is a varbind collection in the
   *    same order and with the same keys as the requested objects)
   * @throws IOException
   */
  SnmpWalker<VarbindCollection> walk(int nonRepeaters, List<String> oids)
      throws IOException;

  /**
   * Performs a walk of a MIB conceptual table.
   * <p>
   * This is a high level operation that can be used to retrieve all 
   * rows of a conceptual table.
   * @param nonRepeaters number of non-repeating objects at the beginning
   *   of {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers; the first 
   *   {@code nonRepeaters} identifiers in the list are assumed to be for 
   *   non-repeating objects
   * @return list of table rows (each row is a varbind collection in the
   *    same order and with the same keys as the requested objects)
   * @throws IOException
   */
  SnmpWalker<VarbindCollection> walk(int nonRepeaters, String... oids)
      throws IOException;

  /**
   * Performs a walk of a MIB conceptual table.
   * <p>
   * This is a high level operation that can be used to retrieve all 
   * rows of a conceptual table.
   * @param nonRepeaters MIB names or dotted-decimal object identifiers 
   *   for the non-repeating elements to retrieve
   * @param repeaters MIB names or dotted-decimal object identifiers 
   *   for the table column elements to retrieve
   * @return list of table rows (each row is a varbind collection in the
   *    same order and with the same keys as the requested objects)
   * @throws IOException
   */
  SnmpWalker<VarbindCollection> walk(List<String> nonRepeaters, 
      List<String> repeaters) throws IOException;

  /**
   * Performs a walk of a MIB conceptual table.
   * <p>
   * This is a high level operation that can be used to retrieve all 
   * rows of a conceptual table.
   * @param repeaters MIB names or dotted-decimal object identifiers 
   *   for the table column elements to retrieve
   * @return list of table rows (each row is a varbind collection in the
   *    same order and with the same keys as the requested objects)
   * @throws IOException
   */
  SnmpWalker<VarbindCollection> walk(List<String> repeaters) 
      throws IOException;

  /**
   * Performs a walk of a MIB conceptual table.
   * <p>
   * This is a high level operation that can be used to retrieve all 
   * rows of a conceptual table.
   * @param repeaters MIB names or dotted-decimal object identifiers 
   *   for the table column elements to retrieve
   * @return list of table rows (each row is a varbind collection in the
   *    same order and with the same keys as the requested objects)
   * @throws IOException
   */
  SnmpWalker<VarbindCollection> walk(String... repeaters) throws IOException;

}
