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

import org.soulwing.snmp.Mib;
import org.soulwing.snmp.MibFactory;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpFactory;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.VarbindCollection;

/**
 * An example that shows a simple GET/GETNEXT operations using standard
 * MIB-2 objects.
 *
 * @author Carl Harris
 */
public class Example01_GetAndGetNext {

  public static void main(String[] args) throws Exception {
    Mib mib = MibFactory.getInstance().newMib();
    mib.load("SNMPv2-MIB");

    // Perform GET using each of the supported target types
    getUsingTarget(ExampleTargets.v1ReadOnly(), mib);
    getUsingTarget(ExampleTargets.v2ReadOnly(), mib);
    getUsingTarget(ExampleTargets.v3ReadOnly(), mib);

    // Perform GETNEXT using each of the supported target types
    getNextUsingTarget(ExampleTargets.v1ReadOnly(), mib);
    getNextUsingTarget(ExampleTargets.v2ReadOnly(), mib);
    getNextUsingTarget(ExampleTargets.v3ReadOnly(), mib);

    SnmpFactory.getInstance().close();
  }

  /**
   * Performs a GET request on a few MIB-2 singleton types using the
   * given target.
   */
  private static void getUsingTarget(SnmpTarget target, Mib mib) {
    try (SnmpContext context = SnmpFactory.getInstance().newContext(target, mib)) {

      // Use GET to fetch the specific object instances we want. Here we
      // specify instance IDs for the desired singleton objects by appending .0
      // to the name.

      VarbindCollection result = context.get(
          "sysName.0", "sysUpTime.0", "sysLocation.0", "sysContact.0").get();

      // Print the results.
      // In this example, we use asMap() on the result, which allows
      // name-based access to each value returned from the GET
      // request. Note that information in the MIB has been used to
      // transform the values from ASN.1 types to an appropriate Java
      // type and representation.

      System.out.format("GET: target type %s: %s\n",
          target.getClass().getSimpleName(), result.asMap());
    }
  }

  /**
   * Performs a GETNEXT request on a few MIB-2 singleton types using the
   * given target.
   */
  private static void getNextUsingTarget(SnmpTarget target, Mib mib) {
    try (SnmpContext context = SnmpFactory.getInstance().newContext(target, mib)) {

      // Use GETNEXT to fetch the specific object instances we want. Here we
      // simply specify the names of the object types; in the MIB, the
      // singleton instance of each of these types is the successor to the
      // object that defines the type.

      VarbindCollection result = context.getNext(
          "sysName", "sysUpTime", "sysLocation", "sysContact").get();

      // Print the results.
      // In this example, we use asList() on the result, which allows
      // indexed-based access to each value returned from the GETNEXT
      // request. Note that information in the MIB has been used to
      // transform the values from ASN.1 types to an appropriate Java
      // type and representation.

      System.out.format("GETNEXT: target type %s: %s\n",
          target.getClass().getSimpleName(), result.asList());
    }
  }

}
