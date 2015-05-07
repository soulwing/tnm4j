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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnmpV2cDemo {

  private static final String NOT_AVAILABLE = "N/A";
  
  private static final Pattern CISCOIOS_PATTERN =
      Pattern.compile("Software \\(([^)]+)\\).*Version  *([^ ,]+) *");
  
  public static void main(String[] args) throws Exception {
    final String deviceName = System.getProperty("agent.name", "foo");
    final String ipAddress = System.getProperty("agent.ip", "10.0.0.1");
    final String community = System.getProperty("agent.community", "public");

    SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
    target.setAddress(ipAddress);
    target.setCommunity(community);

    Mib mib = MibFactory.getInstance().newMib();
    mib.load("RFC1213-MIB");

    final SnmpFactory factory = SnmpFactory.getInstance();
    SnmpContext snmp = factory.newContext(target, mib);

    VarbindCollection varbinds = snmp.getNext("sysDescr", "sysUpTime").get();
    String sysDescr = varbinds.get("sysDescr").asString();
    String sysUpTime = varbinds.get("sysUpTime").asString();
    Matcher matcher = CISCOIOS_PATTERN.matcher(sysDescr);
    boolean found = matcher.find();
    String software = found ? matcher.group(1) : NOT_AVAILABLE;
    String version = found ? matcher.group(2) : NOT_AVAILABLE;

    System.out.format("%-12s %-15s %-24s %-24s %s\n",
        "Device Name", "IP Address", "Software", "Version", "Up Time");

    System.out.format("%-12s %-15s %-24s %-24s %s\n",
        deviceName, ipAddress, software, version, sysUpTime);

    snmp.close();
  }
  
}
