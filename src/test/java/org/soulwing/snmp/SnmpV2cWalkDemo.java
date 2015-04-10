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


public class SnmpV2cWalkDemo {

  
  public static void main(String[] args) throws Exception {    
    final String ipAddress = System.getProperty("agent.ip", "10.0.0.1");
    final String community = System.getProperty("agent.community", "public");

    SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
    target.setAddress(ipAddress);
    target.setCommunity(community);
    
    Mib mib = MibFactory.getInstance().newMib();
    mib.load("RFC1213-MIB");
    mib.load("IF-MIB");
        
    SnmpContext snmp = SnmpFactory.getInstance().newContext(target, mib);

    SnmpWalker<VarbindCollection> walker = snmp.walk(
        1, "sysUpTime", "ifIndex", "ifName", "ifInOctets", "ifHCInOctets");
    
    VarbindCollection row = walker.next().get();
    while (row != null) { 
      System.out.format("%s %d %s %s %s\n", 
          row.get("sysUpTime"), 
          row.get("ifIndex").asInt(),
          row.get("ifName"),
          row.get("ifInOctets"), 
          row.get("ifHCInOctets"));
      row = walker.next().get();
    }

    snmp.close();
    SnmpFactory.getInstance().close();
  }

}
