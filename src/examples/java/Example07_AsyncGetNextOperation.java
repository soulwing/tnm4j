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

import org.soulwing.snmp.Mib;
import org.soulwing.snmp.MibFactory;
import org.soulwing.snmp.SnmpCallback;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpEvent;
import org.soulwing.snmp.SnmpException;
import org.soulwing.snmp.SnmpFactory;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.VarbindCollection;

/**
 * An example that shows how to perform a GETNEXT operation asynchronously.
 *
 * @author Carl Harris
 */
public class Example07_AsyncGetNextOperation {

  public static void main(String[] args) throws Exception {
    Mib mib = MibFactory.getInstance().newMib();
    mib.load("SNMPv2-MIB");

    SnmpTarget target = ExampleTargets.v2ReadOnly();
    SnmpContext context = SnmpFactory.getInstance().newContext(target, mib);

    // create the callback and send the request

    ExampleCallback callback = new ExampleCallback();
    context.asyncGetNext(callback, "sysName", "sysUpTime");

    // Since we have nothing else to do, we're just going to wait for the
    // signal that our request was completed. Presumably, in a real network
    // management application, we'll have plenty of other things to do while
    // we wait. :-)

    callback.awaitCompletion();

    SnmpFactory.getInstance().close();
  }

  static class ExampleCallback implements SnmpCallback<VarbindCollection> {

    private boolean completed;

    @Override
    public void onSnmpResponse(SnmpEvent<VarbindCollection> event) {
      try {
        VarbindCollection result = event.getResponse().get();
        System.out.format("Response Callback: %s uptime %s\n",
            result.get("sysName"),
            result.get("sysUpTime"));
      }
      catch (SnmpException ex) {
        ex.printStackTrace(System.err);
      }
      finally {
        // set the flag to indicate that our request was completed
        synchronized (this) {
          completed = true;
          this.notifyAll();
        }
        event.getContext().close();
      }
    }

    synchronized void awaitCompletion() throws InterruptedException {
      // wait for the completed flag to get set to true
      while (!completed) {
        this.wait();
      }
    }

  }

}
