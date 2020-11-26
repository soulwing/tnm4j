/*
 * File created on Nov 25, 2020
 *
 * Copyright (c) 2020 Carl Harris, Jr
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
import org.soulwing.snmp.SnmpAsyncWalker;
import org.soulwing.snmp.SnmpCallback;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpEvent;
import org.soulwing.snmp.SnmpException;
import org.soulwing.snmp.SnmpFactory;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.VarbindCollection;

/**
 * An example that shows how to asynchronously retrieve the contents of a table.
 *
 * @author Carl Harris
 */
public class Example10_AsyncTableWalk {

  public static void main(String[] args) throws Exception {
    Mib mib = MibFactory.getInstance().newMib();
    mib.load("SNMPv2-MIB");
    mib.load("HOST-RESOURCES-MIB");

    SnmpTarget target = ExampleTargets.v2ReadOnly();
    try (SnmpContext context = SnmpFactory.getInstance().newContext(target, mib)) {
      ExampleCallback callback = new ExampleCallback();

      context.asyncWalk(callback, 1, "sysUpTime", "hrSWInstalledName",
          "hrSWInstalledType");

      callback.awaitCompletion();
    }

    SnmpFactory.getInstance().close();
  }

  static class ExampleCallback
      implements SnmpCallback<SnmpAsyncWalker<VarbindCollection>> {

    private boolean completed;
    private SnmpException exception;

    @Override
    public void onSnmpResponse(
        SnmpEvent<SnmpAsyncWalker<VarbindCollection>> event) {
      try {

        final SnmpAsyncWalker<VarbindCollection> walker = event.getResponse().get();

        VarbindCollection row = walker.next().get();
        while (row != null) {
          System.out.format("%s %d %s %s\n",
              row.get("sysUpTime").asString(),
              row.get("hrSWInstalledIndex").asInt(),
              row.get("hrSWInstalledName").asString(),
              row.get("hrSWInstalledType").asString());
          row = walker.next().get();
        }

        // When the next row is null, we've reached the end of the table.
        signalCompletion();
      }
      catch (SnmpException ex) {
        // An exception occurred, the event that was passed to us will throw
        // the exception when we call event.getResponse().get(). In this case,
        // we signal completion, but pass along the exception.
        signalCompletion(ex);
      }
    }

    void signalCompletion() {
      signalCompletion(null);
    }

    synchronized void signalCompletion(SnmpException ex) {
      this.completed = true;
      this.exception = ex;
      this.notifyAll();
    }

    synchronized void awaitCompletion()
        throws InterruptedException, SnmpException {
      while (!completed) {
        this.wait();
      }
      if (exception != null) {
        // if completion was signaled due to an exception that delivered to our
        // callback, rethrow it on the calling thread
        throw exception;
      }
    }
  }

}
