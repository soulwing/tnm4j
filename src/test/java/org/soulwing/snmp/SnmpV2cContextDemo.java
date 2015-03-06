package org.soulwing.snmp;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.soulwing.snmp.MIB;
import org.soulwing.snmp.MIBFactory;
import org.soulwing.snmp.SNMPFactory;
import org.soulwing.snmp.SNMPv2cContext;
import org.soulwing.snmp.Varbind;

public class SnmpV2cContextDemo {

  private static final String NOT_AVAILABLE = "N/A";
  
  private static final Pattern CISCOIOS_PATTERN =
      Pattern.compile("Software \\(([^)]+)\\).*Version  *([^ ,]+) *");
  
  public static void main(String[] args) throws Exception {    
    final String deviceName = "Foo";
    final String ipAddress = "10.0.0.1";
    final String community = "public";
    
    MIB mib = MIBFactory.getInstance().newMIB();
    mib.load("RFC1213-MIB");
    
    SNMPv2cContext snmp = SNMPFactory.getInstance().newSnmpV2cContext(mib);
    snmp.setAddress(ipAddress);
    snmp.setCommunity(community);
    
    List<Varbind> varbinds = snmp.getNext("sysDescr", "sysUpTime");
    String sysDescr = varbinds.get(0).toString();
    String sysUpTime = varbinds.get(1).toString();

    Matcher matcher = CISCOIOS_PATTERN.matcher(sysDescr);
    boolean found = matcher.find();
    String software = found ? matcher.group(1) : NOT_AVAILABLE;
    String version = found ? matcher.group(2) : NOT_AVAILABLE;
    
    System.out.format("%-12s %-15s %-24s %-24s %s\n", 
        "Device Name", "IP Address", "Software", "Version" , "Up Time");
    
    System.out.format("%-12s %-15s %-24s %-24s %s\n", 
        deviceName, ipAddress, software, version, sysUpTime);
  }
  
}
