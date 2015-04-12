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
import org.soulwing.snmp.SimpleSnmpV2cTarget;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpFactory;
import org.soulwing.snmp.SnmpWalker;
import org.soulwing.snmp.VarbindCollection;

/**
 * An example that show how to retrieve the contents of a table using the
 * high level walk operation.
 *
 * @author Carl Harris
 */
class ExampleAccessingTableIndexes {

  public static void main(String[] args) throws Exception {
    Mib mib = MibFactory.getInstance().newMib();
    mib.load("IPV6-MIB");

    SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
    target.setAddress(System.getProperty("tnm4j.agent.address", "10.0.0.1"));
    target.setCommunity(System.getProperty("tnm4j.agent.community", "public"));

    SnmpContext context = SnmpFactory.getInstance().newContext(target, mib);
    try {
      SnmpWalker<VarbindCollection> walker = context.walk("ipv6AddrPfxLength",
          "ipv6AddrType", "ipv6AddrAnycastFlag", "ipv6AddrStatus");
      VarbindCollection row = walker.next().get();
      while (row != null) {
        System.out.format("%d %s %d %s %s %s\n",
            row.get("ipv6IfIndex").asInt(), row.get("ipv6AddrAddress"),
            row.get("ipv6AddrPfxLength").asInt(), row.get("ipv6AddrType"),
            row.get("ipv6AddrAnycastFlag"), row.get("ipv6AddrStatus"));
        row = walker.next().get();
      }
    }
    finally {
      context.close();
    }
  }

}
