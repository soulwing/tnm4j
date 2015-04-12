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
 * An API for asynchronous SNMP operations.
 *
 * @author Carl Harris
 */
public interface SnmpAsyncOperations {

  /**
   * Creates an SNMP GET operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> asyncGet(List<String> oids);

  /**
   * Creates an SNMP GET operation.
   * @param varbinds a collection identifying the objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> asyncGet(VarbindCollection varbinds);

  /**
   * Creates an SNMP GET operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> asyncGet(String... oids);

  /**
   * Creates an SNMP GETNEXT operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> asyncGetNext(List<String> oids);

  /**
   * Creates an SNMP GETNEXT operation.
   * @param varbinds a collection identifying the objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> asyncGetNext(VarbindCollection varbinds);

  /**
   * Creates an SNMP GETNEXT operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> asyncGetNext(String... oids);

  /**
   * Creates an SNMP GETBULK operation.
   * @param nonRepeaters number of non-repeating objects at the beginning of
   *    {@code oids}.
   * @param maxRepetitions maximum number of repetitions to retrieve for the
   *    repeating objects in {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch; the first {@code nonRepeaters} identifiers in the list
   *    are assumed to be non-repeating objects
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<List<VarbindCollection>> asyncGetBulk(int nonRepeaters,
      int maxRepetitions, List<String> oids);

  /**
   * Creates an SNMP GETBULK operation.
   * @param nonRepeaters number of non-repeating objects at the beginning of
   *    {@code oids}.
   * @param maxRepetitions maximum number of repetitions to retrieve for the
   *    repeating objects in {@code oids}
   * @param varbinds a collection identifying the objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<List<VarbindCollection>> asyncGetBulk(int nonRepeaters,
      int maxRepetitions, VarbindCollection varbinds);

  /**
   * Create an SNMP GETBULK operation.
   * @param nonRepeaters number of non-repeating objects at the beginning of
   *   {@code oids}.
   * @param maxRepetitions maximum number of repetitions to retrieve for the
   *    repeating objects in {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch; the first {@code nonRepeaters} identifiers in the 
   *    list are assumed to be for non-repeating objects
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<List<VarbindCollection>> asyncGetBulk(int nonRepeaters,
      int maxRepetitions, String... oids);

  /**
   * Creates a walker for a conceptual table.
   * <p>
   *   of {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers; the first 
   *   {@code nonRepeaters} identifiers in the list are assumed to be for 
   *   non-repeating objects
   * @return a walker that can be used to obtain rows from the table
   */
  SnmpAsyncWalker<VarbindCollection> asyncWalk(int nonRepeaters, 
      List<String> oids);

  /**
   * Creates a walker for a conceptual table.
   * <p>
   * @param nonRepeaters number of non-repeating objects at the beginning
   *   of {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers; the first 
   *   {@code nonRepeaters} identifiers in the list are assumed to be for 
   *   non-repeating objects
   * @return a future that can be used to obtain the list of table rows
   */
  SnmpAsyncWalker<VarbindCollection> asyncWalk(int nonRepeaters, String... oids);

  /**
   * Creates a walker for a conceptual table.
   * <p>
   * This is a high level operation that can be used to retrieve all 
   * rows of a conceptual table.
   * @param nonRepeaters MIB names or dotted-decimal object identifiers 
   *   for the non-repeating elements to retrieve
   * @param repeaters MIB names or dotted-decimal object identifiers 
   *   for the table column elements to retrieve
   * @return a future that can be used to obtain the list of table rows
   */
  SnmpAsyncWalker<VarbindCollection> asyncWalk(List<String> nonRepeaters, 
      List<String> repeaters);

  /**
   * Creates a walker for a conceptual table.
   * <p>
   * This is a high level operation that can be used to retrieve all 
   * rows of a conceptual table.
   * @param repeaters MIB names or dotted-decimal object identifiers 
   *   for the table column elements to retrieve
   * @return a walker that can be used to obtain rows from the table
   */
  SnmpAsyncWalker<VarbindCollection> asyncWalk(List<String> repeaters);

  /**
   * Creates a walker for a conceptual table.
   * <p>
   * This is a high level operation that can be used to retrieve all 
   * rows of a conceptual table.
   * @param repeaters MIB names or dotted-decimal object identifiers 
   *   for the table column elements to retrieve
   * @return a walker that can be used to obtain rows from the table
   */
  SnmpAsyncWalker<VarbindCollection> asyncWalk(String... repeaters);

}
