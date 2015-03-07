tnm4j
=====

A simplified SNMP API for Java, inspired by Jürgen Schönwälder's Tnm
extension for Tcl.  

The original Tnm made it easy to write network management applications using 
simple Tcl scripts.  Tnm4j attempts to bring this same simplicity to the 
task of writing network management applications in Java or in Java-based
scripting languages such as Groovy.

Trivial Example
---------------

The following example illustrates just a few of the features of Tnm4j.  This 
snippet retrieves and displays the name, description, and up time of an 
SNMP-enabled network device.

```
MIB mib = MIBFactory.getInstance().newMib();
mib.load("RFC1213-MIB");

SnmpV2cContext snmp = SnmpFactory.getInstance().newSnmpV2cContext(mib);
snmp.setAddress("10.0.0.1");
snmp.setCommunity("public");

List<Varbind> varbinds = snmp.getNext("sysName", "sysDescr", "sysUpTime");
for (Varbind varbind : varbinds) {
  System.out.format("%s=%s\n", varbind.getName(), varbind.toString());
}
```

Notice how the MIB and SNMP context objects are designed to work together?
This is arguably the most salient concept of Tnm4j.  Tnm4j fully exploits
the MIB to make it easy for the developer to access management objects in
an SNMP agent.  The developer can use MIB object names (instead 
of SNMP object identifiers) when getting or setting object values, reducing
the time and effort required to get the desired management information. 

The syntax and textual convention details from the MIB are used when 
converting object values to strings.  This is illustrated in the preceding
example -- converting a retrieved value to a string in the appropriate 
format is as simple as calling `Varbind.toString()`.


Simplified Access to Conceptual Tables
--------------------------------------

The MIB integration also plays a pivotal role in interpreting the index
objects for conceptual tables in the MIB.  [SNMPv2] 
(https://www.ietf.org/rfc/rfc2578.txt) table row indexes are not accessible.  
An application that wishes to query table information from an SNMP agent must 
reconstruct the index object values from the object identifier of another 
(accessible) object in the table.  This restriction makes the agent 
implementation more efficient, but greatly complicates the work of the network 
management application.  Tnm4j completely encapsulates this unpleasant truth of 
SNMPv2 tables, as illustrated in the following example.  

Suppose that our management application wants to use the [IPV6-MIB] 
(http://www.ietf.org/rfc/rfc2465.txt) to obtain the IPv6 addresses 
that are configured on the interfaces of a network device.  The 
`ipv6AddrTable` contains the information that we'd like to retrieve.  
If you review the definition of this table, you'll note that the most important
pieces of information in this table -- the interface index and the 
address itself -- cannot be retrieved from the table; the ACCESS-TYPE of these
objects is `not-accessible`.  However, Tnm4j makes it easy to access this
information (which is encoded in the object identifiers of the other objects
in the table).

```
MIB mib = MIBFactory.getInstance().newMib();
mib.load("IPV6-MIB");

SnmpV2cContext snmp = SnmpFactory.getInstance().newSnmpV2cContext(mib);
snmp.setAddress("10.0.0.1");
snmp.setCommunity("public");

List<Map<String, Varbind>> varbinds = snmp.walk("ipv6AddrPfxLength", 
    "ipv6AddrType", "ipv6AddrAnycastFlag", "ipv6AddrStatus");
for (Map<String, Varbind> varbind : varbinds) {
  System.out.format("%d %s %d %s %s %s\n", 
    varbind.get("ipv6IfIndex").toInt(), varbind.get("ipv6AddrAddress"),
    varbind.get("ipv6AddrPfxLength").toInt(), varbind.get("ipv6AddrType"),
    varbind.get("ipv6AddrAnycastFlag"), varbind.get("ipv6AddrStatus"));
}

snmp.dispose();
```

When using the `walk` method to walk a table, the index objects are implicitly
available for each retrieved table row, even though they were not (and cannot)
be among the objects retrieved from the table.  Tnm4j interprets the object
identifiers returned from the first retrieved column value using the MIB to
provide the corresponding index object values.

The `Varbind.getIndexes` method also provides access to a table object's 
indexes.


Architecture
------------
Tnm4j provides a lightweight façade over an SNMP adapter and a MIB parser
adapter.  These adapters each implement a Tnm4j service provider interface to 
adapt a third-party library for use with Tnm4j.  The JDK's [ServiceLoader] 
(http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html)
mechanism is used to locate adapters for SNMP and MIB parsing support.

The default SNMP adapter uses Frank Fock's [Snmp4j] (http://www.snmp4j.org).
Snmp4j is an outstanding library providing comprehensive support for SNMP
communications in Java.

The default MIB adapter uses Per Cederberg's excellent [Mibble] 
(http://www.mibble.org) MIB parser.

Other SNMP providers or MIB parsers could be easily adapted for use with
Tnm4j by implementing the necessary SPI.


