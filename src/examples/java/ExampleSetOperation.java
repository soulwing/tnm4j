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
import org.soulwing.snmp.Varbind;
import org.soulwing.snmp.VarbindCollection;

/**
 * An example that shows a simple SET operation using the numeric object
 * identifiers for the {@code sysContact} object in the standard MIB.
 *
 * @author Carl Harris
 */
public class ExampleSetOperation {

  public static void main(String[] args) throws Exception {
    Mib mib = MibFactory.getInstance().newMib();
    mib.load("SNMPv2-MIB");

    SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
    target.setAddress(System.getProperty("tnm4j.agent.address", "10.0.0.1"));
    target.setCommunity(System.getProperty("tnm4j.agent.community", "public"));

    SnmpContext context = SnmpFactory.getInstance().newContext(target, mib);
    try {
      Varbind vb = context.newVarbind("sysContact.0", "nobody@nowhere.net");
      VarbindCollection result = context.set(vb).get();
      System.out.println(result.get("sysContact.0"));
    }
    finally {
      context.close();
    }

  }

}
