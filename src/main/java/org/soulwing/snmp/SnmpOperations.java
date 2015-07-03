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
   * @return a response containing the retrieved varbinds in the same order 
   *    and with the same keys as the requested objects
   */
  SnmpResponse<VarbindCollection> get(List<String> oids);

  /**
   * Performs an SNMP GET operation.
   * @param varbinds a collection identifying the objects to fetch
   * @return a response containing the retrieved varbinds in the same order
   *    and with the same keys as the requested objects
   */
  SnmpResponse<VarbindCollection> get(VarbindCollection varbinds);

  /**
   * Performs an SNMP SET operation.
   * @param varbinds variable bindings to set
   * @return a response containing the resulting varbinds in the same order
   *    and with the same keys as the requested varbinds
   */
  SnmpResponse<VarbindCollection> set(Varbind... varbinds);

  /**
   * Performs an SNMP SET operation.
   * @param varbinds variable bindings to set
   * @return a response containing the resulting varbinds in the same order
   *    and with the same keys as the requested varbinds
   */
  SnmpResponse<VarbindCollection> set(List<Varbind> varbinds);

  /**
   * Performs an SNMP GET operation.
   * @param varbinds a collection identifying the objects to set
   * @return a response containing the resulting varbinds in the same order
   *    and with the same keys as the requested varbinds
   */
  SnmpResponse<VarbindCollection> set(VarbindCollection varbinds);

  /**
   * Performs an SNMP GET operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return a response containing the retrieved varbinds in the same order
   *    and with the same keys as the requested objects
   */
  SnmpResponse<VarbindCollection> get(String... oids);

  /**
   * Performs an SNMP GETNEXT operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return a response containing the retrieved varbinds in the same order 
   *    and with the same keys as the requested objects
   */
  SnmpResponse<VarbindCollection> getNext(List<String> oids);

  /**
   * Performs an SNMP GETNEXT operation.
   * @param varbinds a collection identifying the objects to fetch
   * @return a response containing the retrieved varbinds in the same order
   *    and with the same keys as the requested objects
   */
  SnmpResponse<VarbindCollection> getNext(VarbindCollection varbinds);

  /**
   * Performs an SNMP GETNEXT operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return a response containing the retrieved varbinds in the same order 
   *    and with the same keys as the requested objects
   */
  SnmpResponse<VarbindCollection> getNext(String... oids);

  /**
   * Performs an SNMP GETBULK operation.
   * @param nonRepeaters number of non-repeating objects at the beginning of
   *    {@code oids}.
   * @param maxRepetitions maximum number of repetitions to retrieve for the
   *    repeating objects in {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch; the first {@code nonRepeaters} identifiers in the list
   *    are assumed to be non-repeating objects
   * @return a response containing a list of varbind collections; the list size
   *    is {@code maxRepetitions}.
   */
  SnmpResponse<List<VarbindCollection>> getBulk(int nonRepeaters,
      int maxRepetitions, List<String> oids);

  /**
   * Performs an SNMP GETBULK operation.
   * @param nonRepeaters number of non-repeating objects at the beginning of
   *    {@code varbinds}.
   * @param maxRepetitions maximum number of repetitions to retrieve for the
   *    repeating objects in {@code varbinds}
   * @param varbinds a collection identifying the objects to fetch
   * @return a response containing a list of varbind collections; the list size
   *    is {@code maxRepetitions}.
   */
  SnmpResponse<List<VarbindCollection>> getBulk(int nonRepeaters,
      int maxRepetitions, VarbindCollection varbinds);
  /**
   * Performs an SNMP GETBULK operation.
   * @param nonRepeaters number of non-repeating objects at the beginning of
   *   {@code oids}.
   * @param maxRepetitions maximum number of repetitions to retrieve for the
   *    repeating objects in {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch; the first {@code nonRepeaters} identifiers in the 
   *    list are assumed to be for non-repeating objects
   * @return a response containing a list of varbind collections; the list size
   *    is {@code maxRepetitions}.
   */
  SnmpResponse<List<VarbindCollection>> getBulk(int nonRepeaters,
      int maxRepetitions, String... oids);

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
   * @return a response containing the retrieved varbinds in the same order 
   *    and with the same keys as the requested objects
   */
  SnmpWalker<VarbindCollection> walk(int nonRepeaters, List<String> oids);

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
   * @return table row walker
   */
  SnmpWalker<VarbindCollection> walk(int nonRepeaters, String... oids);

  /**
   * Performs a walk of a MIB conceptual table.
   * <p>
   * This is a high level operation that can be used to retrieve all 
   * rows of a conceptual table.
   * @param nonRepeaters MIB names or dotted-decimal object identifiers 
   *   for the non-repeating elements to retrieve
   * @param repeaters MIB names or dotted-decimal object identifiers 
   *   for the table column elements to retrieve
   * @return table row walker
   */
  SnmpWalker<VarbindCollection> walk(List<String> nonRepeaters, 
      List<String> repeaters);

  /**
   * Performs a walk of a MIB conceptual table.
   * <p>
   * This is a high level operation that can be used to retrieve all 
   * rows of a conceptual table.
   * @param repeaters MIB names or dotted-decimal object identifiers 
   *   for the table column elements to retrieve
   * @return table row walker
   */
  SnmpWalker<VarbindCollection> walk(List<String> repeaters);

  /**
   * Performs a walk of a MIB conceptual table.
   * <p>
   * This is a high level operation that can be used to retrieve all 
   * rows of a conceptual table.
   * @param repeaters MIB names or dotted-decimal object identifiers 
   *   for the table column elements to retrieve
   * @return table row walker
   */
  SnmpWalker<VarbindCollection> walk(String... repeaters);

}
