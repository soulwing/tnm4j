/*
 * File created on Apr 12, 2015
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

import java.util.List;

/**
 * An API for creating SNMP operations.
 *
 * @author Carl Harris
 */
public interface SnmpOperationFactory {

  /**
   * Creates an SNMP GET operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> newGet(List<String> oids);

  /**
   * Creates an SNMP GET operation.
   * @param varbinds a collection identifying the objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> newGet(VarbindCollection varbinds);

  /**
   * Creates an SNMP GET operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> newGet(String... oids);

  /**
   * Creates an SNMP SET operation.
   * @param varbinds variable bindings to set
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> newSet(List<Varbind> varbinds);

  /**
   * Creates an SNMP SET operation.
   * @param varbinds a collection identifying the objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> newSet(VarbindCollection varbinds);

  /**
   * Creates an SNMP SET operation.
   * @param varbinds variable bindings to set
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> newSet(Varbind... varbinds);

  /**
   * Creates an SNMP GETNEXT operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> newGetNext(List<String> oids);

  /**
   * Creates an SNMP GETNEXT operation.
   * @param varbinds a collection identifying the objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> newGetNext(VarbindCollection varbinds);

  /**
   * Creates an SNMP GETNEXT operation.
   * @param oids MIB names or dotted-decimal object identifiers for the
   *    objects to fetch
   * @return an operation that can be invoked to obtain a response
   */
  SnmpOperation<VarbindCollection> newGetNext(String... oids);

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
  SnmpOperation<List<VarbindCollection>> newGetBulk(int nonRepeaters,
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
  SnmpOperation<List<VarbindCollection>> newGetBulk(int nonRepeaters,
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
  SnmpOperation<List<VarbindCollection>> newGetBulk(int nonRepeaters,
      int maxRepetitions, String... oids);

  /**
   * Creates a walk operation.
   * @param oids MIB names or dotted-decimal object identifiers; the first
   *   {@code nonRepeaters} identifiers in the list are assumed to be for
   *   non-repeating objects
   */
  SnmpAsyncWalker<VarbindCollection> newWalk(int nonRepeaters,
      List<String> oids);

  /**
   * Creates a walk operation.
   * @param nonRepeaters number of non-repeating objects at the beginning
   *   of {@code oids}
   * @param oids MIB names or dotted-decimal object identifiers; the first
   *   {@code nonRepeaters} identifiers in the list are assumed to be for
   *   non-repeating objects
   */
  SnmpAsyncWalker<VarbindCollection> newWalk(int nonRepeaters, String... oids);

  /**
   * Creates a walk operation.
   * @param nonRepeaters MIB names or dotted-decimal object identifiers
   *   for the non-repeating elements to retrieve
   * @param repeaters MIB names or dotted-decimal object identifiers
   *   for the table column elements to retrieve
   */
  SnmpAsyncWalker<VarbindCollection> newWalk(List<String> nonRepeaters,
      List<String> repeaters);

  /**
   * Creates a walk operation.
   * @param repeaters MIB names or dotted-decimal object identifiers
   *   for the table column elements to retrieve
   */
  SnmpAsyncWalker<VarbindCollection> newWalk(List<String> repeaters);

  /**
   * Creates a walk operation.
   * @param repeaters MIB names or dotted-decimal object identifiers
   *   for the table column elements to retrieve
   */
  SnmpAsyncWalker<VarbindCollection> newWalk(String... repeaters);

}
