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


public class SnmpV2cAsyncWalkDemo {

  
  public static void main(String[] args) throws Exception {    
    final String ipAddress = System.getProperty("agent.ip", "10.0.0.1");
    final String community = System.getProperty("agent.community", "public");

    SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
    target.setAddress(ipAddress);
    target.setCommunity(community);
    
    Mib mib = MibFactory.getInstance().newMIB();
    mib.load("RFC1213-MIB");
    mib.load("IF-MIB");
        
    SnmpContext snmp = SnmpFactory.getInstance().newContext(target, mib);

    SnmpAsyncWalker<VarbindCollection> walker = snmp.asyncWalk(
        1, "sysUpTime", "ifIndex", "ifName", "ifInOctets", "ifHCInOctets");
    
    WalkCallback callback = new WalkCallback();
    walker.invoke(callback);
    Thread.sleep(Long.MAX_VALUE);
  }

  static class WalkCallback implements
      SnmpCallback<SnmpAsyncWalker<VarbindCollection>> {

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
          System.out.format("%s %d %s %s %s\n", 
              row.get("sysUpTime"), 
              row.get("ifIndex").toInt(), 
              row.get("ifName"),
              row.get("ifInOctets"), 
              row.get("ifHCInOctets"));
          row = walker.next().get();
        }
        return;
      }
      catch (WouldBlockException ex) {
        walker.invoke(this);
      }
    }
    
  }

}
