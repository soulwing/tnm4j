/*
 * File created on Apr 13, 2015
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

import org.soulwing.snmp.BlockingQueueSnmpCompletionService;
import org.soulwing.snmp.Mib;
import org.soulwing.snmp.MibFactory;
import org.soulwing.snmp.SnmpCompletionService;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpEvent;
import org.soulwing.snmp.SnmpFactory;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.VarbindCollection;

/**
 * An example that illustrates the use of {@link SnmpCompletionService}
 *
 * @author Carl Harris
 */
public class Example08_UsingCompletionService {

  public static void main(String[] args) throws Exception {
    Mib mib = MibFactory.getInstance().newMib();
    mib.load("SNMPv2-MIB");

    SnmpCompletionService<VarbindCollection> completionService =
        new BlockingQueueSnmpCompletionService<>();

    SnmpTarget target = ExampleTargets.v2ReadOnly();
    SnmpContext context = SnmpFactory.getInstance().newContext(target, mib);
    completionService.submit(context.newGetNext("sysName", "sysUpTime"));

    while (!completionService.isIdle()) {
      SnmpEvent<VarbindCollection> event = completionService.take();
      VarbindCollection result = event.getResponse().get();
      System.out.format("%s: sysName=%s sysUpTime=%s\n",
          event.getContext().getTarget().getAddress(),
          result.get("sysName"),
          result.get("sysUpTime"));

      event.getContext().close();
    }

    SnmpFactory.getInstance().close();
  }

}
