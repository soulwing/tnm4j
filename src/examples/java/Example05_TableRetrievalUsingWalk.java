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
import org.soulwing.snmp.SnmpWalker;
import org.soulwing.snmp.VarbindCollection;

/**
 * An example that shows how to retrieve the contents of a table using the
 * high level walk operation.
 *
 * @author Carl Harris
 */
public class Example05_TableRetrievalUsingWalk {

  public static void main(String[] args) throws Exception {
    Mib mib = MibFactory.getInstance().newMib();
    mib.load("SNMPv2-MIB");
    mib.load("IF-MIB");

    SnmpTarget target = ExampleTargets.v2ReadOnly();

    try (SnmpContext context = SnmpFactory.getInstance().newContext(target, mib)) {

      final String[] columns = {"sysUpTime", "ifName",
          "ifDescr", "ifAdminStatus", "ifOperStatus", "ifInOctets", "ifOutOctets"};

      System.out.format("%-14s %-8s %-20s %-5s %-5s %15s %15s\n",
          "UpTime", "Name", "Description", "Admin", "Oper", "In Octets", "Out Octets");

      SnmpWalker<VarbindCollection> walker = context.walk(1, columns);

      VarbindCollection row = walker.next().get();
      while (row != null) {

        System.out.format("%14s %-8s %-20s %-5s %-5s %,15d %,15d\n",
            row.get("sysUpTime"),
            row.get("ifName"),
            row.get("ifDescr"),
            row.get("ifAdminStatus"),
            row.get("ifOperStatus"),
            row.get("ifInOctets").asLong(),
            row.get("ifOutOctets").asLong());

        row = walker.next().get();
      }
    }

    SnmpFactory.getInstance().close();
  }

}
