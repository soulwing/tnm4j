/*
 * tnm4j - Simplified SNMP API for Java
 * Copyright (C) 2012 Carl Harris, Jr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.soulwing.snmp;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SnmpV2cAsyncWalkDemo {

  
  public static void main(String[] args) throws Exception {    
    final String ipAddress = System.getProperty("agent.ip", "10.0.0.1");
    final String community = System.getProperty("agent.community", "public");

    SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
    target.setAddress(ipAddress);
    target.setCommunity(community);
    
    Mib mib = MibFactory.getInstance().newMib();
    mib.load("RFC1213-MIB");
    mib.load("IF-MIB");
        
    SnmpFactory factory = SnmpFactory.getInstance();
    SnmpContext snmp = factory.newContext(target, mib);

    WalkCallback callback = new WalkCallback();
    snmp.asyncWalk(callback,
        1, "sysUpTime", "ifIndex", "ifName", "ifOperStatus", "ifAdminStatus",
        "ifHCInOctets", "ifHCOutOctets");
    
    callback.awaitShutdown();
    System.out.println("done");
    factory.close();
  }

  static class WalkCallback implements
      SnmpCallback<SnmpAsyncWalker<VarbindCollection>> {

    private final Lock lock = new ReentrantLock();
    private final Condition shutdownCondition = lock.newCondition();
    
    private boolean shutdown;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onSnmpResponse(
        SnmpEvent<SnmpAsyncWalker<VarbindCollection>> event) {
      SnmpAsyncWalker<VarbindCollection> walker = event.getResponse().get();
      try {
        VarbindCollection row = walker.next().get();
        while (row != null) { 
          System.out.format("%s %d %s %s %s %s %s\n", 
              row.get("sysUpTime"), 
              row.get("ifIndex").asInt(),
              row.get("ifName"),
              row.get("ifAdminStatus"),
              row.get("ifOperStatus"),
              row.get("ifHCInOctets").asLong(),
              row.get("ifHCOutOctets").asLong());
          row = walker.next().get();
        }
        signalShutdown();
      }
      catch (WouldBlockException ex) {
        walker.invoke(this);
      }
    }

    private void signalShutdown() {
      lock.lock();
      try {
        System.out.println("shut down");
        shutdown = true;
        shutdownCondition.signalAll();
      }
      finally {
        lock.unlock();
      }
    }
    
    public void awaitShutdown() throws InterruptedException {
      lock.lock();
      try {
        while (!shutdown) {
          shutdownCondition.await();
        } 
        System.out.println("shutdown signaled");
      }
      finally {
        lock.unlock();
      }
    }
    
  }

}
