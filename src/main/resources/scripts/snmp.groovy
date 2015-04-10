import demo.InterfaceInfo;

snmp.setAddress("172.16.18.15")
snmp.mib.load("RFC1213-MIB")
snmp.mib.load("IF-MIB")

def sysObjects = [ 
  "sysName", "sysUpTime" 
];
 
def ifObjects = [ 
  "ifName", "ifAlias", "ifType", 
  "ifAdminStatus", "ifOperStatus", "ifLastChange",
  "ifInUcastPkts", "ifInNUcastPkts", "ifInOctets", "ifHCInOctets", 
  "ifInErrors", "ifInDiscards",
  "ifOutUcastPkts","ifOutNUcastPkts", "ifOutOctets", "ifHCOutOctets",
  "ifOutErrors", "ifOutDiscards"
];


def start = System.currentTimeMillis();
def results = snmp.walk(sysObjects.size, sysObjects + ifObjects);
def stop = System.currentTimeMillis();
println "${stop - start} milliseconds";

def sysInfo = results.get(0);
results.remove(0);

device.name = sysInfo.sysName.toString();
device.upTime = sysInfo.sysUpTime.asLong();

results.each { ifEntry -> device.interfaces << createInterface(device, ifEntry) } 


/**
 * Transforms a map of IfEntry varbinds into an InterfaceInfo object
 * @param device the device object that will own the interface
 * @param ifEntry map of IfEntry varbinds
 * @return InterfaceInfo object
 */
def createInterface(def device, def ifEntry) {
  InterfaceInfo intf = new InterfaceInfo(device);
  intf.index = ifEntry.ifIndex.asInt();
  intf.name = ifEntry.ifName.toString();
  intf.description = ifEntry.ifAlias.toString();
  intf.adminStatus = ifEntry.ifAdminStatus.toString();
  intf.operStatus = ifEntry.ifOperStatus.toString();
  intf.lastChange = ifEntry.ifLastChange.asLong();
  intf.in.packets = ifEntry.ifInUcastPkts.asLong()
      + (ifEntry.ifInNUcastPkts ? ifEntry.ifInNUcastPkts.asLong() : 0);
  intf.in.octets = ifEntry.ifHCInOctets ?
      ifEntry.ifHCInOctets.asLong() : ifEntry.ifInOctets.asLong();
  intf.in.errors = ifEntry.ifInErrors.asLong();
  intf.in.discards = ifEntry.ifInDiscards.asLong();
  intf.out.packets = ifEntry.ifOutUcastPkts.asLong()
      + (ifEntry.ifOutNUcastPkts ? ifEntry.ifOutNUcastPkts.asLong() : 0);
  intf.out.octets = ifEntry.ifHCOutOctets ?
      ifEntry.ifHCOutOctets.asLong() : ifEntry.ifOutOctets.asLong();
  intf.out.errors = ifEntry.ifOutErrors.asLong();
  intf.out.discards = ifEntry.ifOutDiscards.asLong();
  return intf;
}
