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
   * Invokes an asynchronous SNMP GET operation.
   * @param callback callback to invoke when a response is available
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   */
  void asyncGet(SnmpCallback<VarbindCollection> callback, List<String> oids);

  /**
   * Invokes an asynchronous SNMP GET operation.
   * @param callback callback to invoke when a response is available
   * @param varbinds a collection identifying the objects to fetch
   */
  void asyncGet(SnmpCallback<VarbindCollection> callback,
      VarbindCollection varbinds);

  /**
   * Invokes an asynchronous SNMP GET operation.
   * @param callback callback to invoke when a response is available
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   */
  void asyncGet(SnmpCallback<VarbindCollection> callback,
      String... oids);

  /**
   * Invokes an asynchronous SNMP SET operation.
   * @param callback callback to invoke when a response is available
   * @param varbinds variable bindings to set in the operation
   */
  void asyncSet(SnmpCallback<VarbindCollection> callback, List<Varbind> varbinds);

  /**
   * Invokes an asynchronous SNMP SET operation.
   * @param callback callback to invoke when a response is available
   * @param varbinds variable bindings to set in the operation
   */
  void asyncSet(SnmpCallback<VarbindCollection> callback,
      VarbindCollection varbinds);

  /**
   * Invokes an asynchronous SNMP SET operation.
   * @param callback callback to invoke when a response is available
   * @param varbinds variable bindings to set in the operation
   */
  void asyncSet(SnmpCallback<VarbindCollection> callback,
      Varbind... varbinds);

  /**
   * Invokes an asynchronous SNMP GETNEXT operation.
   * @param callback callback to invoke when a response is available
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   */
  void asyncGetNext(SnmpCallback<VarbindCollection> callback,
      List<String> oids);

  /**
   * Invokes an asynchronous SNMP GETNEXT operation.
   * @param callback callback to invoke when a response is available
   * @param varbinds a collection identifying the objects to fetch
   */
  void asyncGetNext(SnmpCallback<VarbindCollection> callback,
      VarbindCollection varbinds);

  /**
   * Invokes an asynchronous SNMP GETNEXT operation.
   * @param callback callback to invoke when a response is available
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   */
  void asyncGetNext(SnmpCallback<VarbindCollection> callback,
      String... oids);

  /**
   * Invokes an asynchronous SNMP GETBULK operation.
   * @param callback callback to invoke when a response is available
   * @param nonRepeaters number of non-repeating objects at the beginning of
   *    {@code oids}.
   * @param maxRepetitions maximum number of repetitions to retrieve for the
   *    repeating objects in {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch; the first {@code nonRepeaters} identifiers in the list
   *    are assumed to be non-repeating objects
   */
  void asyncGetBulk(SnmpCallback<List<VarbindCollection>> callback,
      int nonRepeaters,
      int maxRepetitions, List<String> oids);

  /**
   * Invokes an asynchronous SNMP GETBULK operation.
   * @param callback callback to invoke when a response is available
   * @param nonRepeaters number of non-repeating objects at the beginning of
   *    {@code oids}.
   * @param maxRepetitions maximum number of repetitions to retrieve for the
   *    repeating objects in {@code oids}
   * @param varbinds a collection identifying the objects to fetch
   */
  void asyncGetBulk(SnmpCallback<List<VarbindCollection>> callback,
      int nonRepeaters, int maxRepetitions, VarbindCollection varbinds);

  /**
   * Invokes an asynchronous SNMP GETBULK operation.
   * @param callback callback to invoke when a response is available
   * @param nonRepeaters number of non-repeating objects at the beginning of
   *   {@code oids}.
   * @param maxRepetitions maximum number of repetitions to retrieve for the
   *    repeating objects in {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch; the first {@code nonRepeaters} identifiers in the 
   *    list are assumed to be for non-repeating objects
   */
  void asyncGetBulk(SnmpCallback<List<VarbindCollection>> callback,
      int nonRepeaters, int maxRepetitions, String... oids);

  /**
   * Invokes an asynchronous walk on a conceptual table.
   * @param callback to invoke when a walker becomes available
   * @param oids MIB names or dotted-decimal object identifiers; the first
   *   {@code nonRepeaters} identifiers in the list are assumed to be for 
   *   non-repeating objects
   */
  void asyncWalk(SnmpCallback<SnmpAsyncWalker<VarbindCollection>> callback,
      int nonRepeaters, List<String> oids);

  /**
   * Invokes an asynchronous walk on a conceptual table.
   * @param callback to invoke when a walker becomes available
   * @param nonRepeaters number of non-repeating objects at the beginning
   *   of {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers; the first 
   *   {@code nonRepeaters} identifiers in the list are assumed to be for 
   *   non-repeating objects
   */
  void asyncWalk(SnmpCallback<SnmpAsyncWalker<VarbindCollection>> callback,
      int nonRepeaters, String... oids);

  /**
   * Invokes an asynchronous walk on a conceptual table.
   * @param callback to invoke when a walker becomes available
   * @param nonRepeaters MIB names or dotted-decimal object identifiers 
   *   for the non-repeating elements to retrieve
   * @param repeaters MIB names or dotted-decimal object identifiers 
   *   for the table column elements to retrieve
   */
  void asyncWalk(SnmpCallback<SnmpAsyncWalker<VarbindCollection>> callback,
      List<String> nonRepeaters, List<String> repeaters);

  /**
   * Invokes an asynchronous walk on a conceptual table.
   * @param callback to invoke when a walker becomes available
   * @param repeaters MIB names or dotted-decimal object identifiers
   *   for the table column elements to retrieve
   */
  void asyncWalk(SnmpCallback<SnmpAsyncWalker<VarbindCollection>> callback,
      List<String> repeaters);

  /**
   * Invokes an asynchronous walk on a conceptual table.
   * @param callback to invoke when a walker becomes available
   * @param repeaters MIB names or dotted-decimal object identifiers
   *   for the table column elements to retrieve
   */
  void asyncWalk(SnmpCallback<SnmpAsyncWalker<VarbindCollection>> callback,
      String... repeaters);

}
