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
 * An example that shows how to access table index values when walking a table.
 *
 * @author Carl Harris
 */
public class Example06_AccessingTableIndexes {

  public static void main(String[] args) throws Exception {
    Mib mib = MibFactory.getInstance().newMib();
    mib.load("IP-MIB");

    SnmpTarget target = ExampleTargets.v2ReadOnly();

    try (SnmpContext context = SnmpFactory.getInstance().newContext(target, mib)) {

      SnmpWalker<VarbindCollection> walker = context.walk(
          "ipSystemStatsHCInReceives",
          "ipSystemStatsHCInOctets",
          "ipSystemStatsHCOutTransmits",
          "ipSystemStatsHCOutOctets");

      System.out.format("%-5s %10s %10s %10s %10s\n", "Proto",
          "Receives", "In Octets", "Transmits", "Out Octets");

      VarbindCollection row = walker.next().get();
      while (row != null) {
        System.out.format("%-5s %,10d %,10d %,10d %,10d\n",
            // ipSystemStatsIPVersion is marked as "no-access" in the MIB,
            // so we can't include it in the objects to fetch. However, we
            // can still access its value.
            row.get("ipSystemStatsIPVersion"),
            row.get("ipSystemStatsHCInReceives").asLong(),
            row.get("ipSystemStatsHCInOctets").asInt(),
            row.get("ipSystemStatsHCOutTransmits").asLong(),
            row.get("ipSystemStatsHCOutOctets").asLong());
        row = walker.next().get();
      }
    }

    SnmpFactory.getInstance().close();
  }

}
