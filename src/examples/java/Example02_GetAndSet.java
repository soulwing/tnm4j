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
import org.soulwing.snmp.Varbind;
import org.soulwing.snmp.VarbindCollection;

/**
 * An example that shows a simple SET operation to change the sysName assigned
 * to an agent.
 *
 * @author Carl Harris
 */
public class Example02_GetAndSet {

  public static void main(String[] args) throws Exception {
    Mib mib = MibFactory.getInstance().newMib();
    mib.load("SNMPv2-MIB");
    SnmpTarget target = ExampleTargets.v3ReadWrite();
    try (SnmpContext context = SnmpFactory.getInstance().newContext(target, mib)) {
      System.out.format("Before: %s\n", context.get("sysName.0").get());

      Varbind sysName = context.newVarbind("sysName.0", "net-snmp");
      VarbindCollection result = context.set(sysName).get();

      System.out.format("After: %s\n", result);
    }

    SnmpFactory.getInstance().close();
  }

}
