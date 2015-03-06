/*
 * File created on Jan 24, 2013 
 *
 * Copyright (c) 2013 Carl Harris, Jr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.soulwing.snmp.provider.mibble;

import java.util.Arrays;
import java.util.List;

import org.soulwing.snmp.IndexDescriptor;
import org.soulwing.snmp.IndexExtractor;
import org.soulwing.snmp.MIB;

import net.percederberg.mibble.MibType;
import net.percederberg.mibble.MibTypeTag;
import net.percederberg.mibble.MibValue;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpIndex;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.percederberg.mibble.type.SizeConstraint;
import net.percederberg.mibble.type.StringType;
import net.percederberg.mibble.type.ValueConstraint;

class MibbleIndexExtractor implements IndexExtractor {

  private final MibbleMIB mib;
  private final MibValueSymbol symbol;
  private final SnmpIndex[] indexes;

  public MibbleIndexExtractor(MibbleMIB mib, MibValueSymbol symbol) {
    if (!symbol.isTableColumn()) {
      throw new IllegalArgumentException("symbol is not a table column: " 
          + symbol.getName());
    }
    
    this.mib = mib;
    this.symbol = symbol;
    MibValueSymbol rowSymbol = symbol.getParent();
    SnmpObjectType rowType = (SnmpObjectType) rowSymbol.getType();
    if (rowType.getAugments() != null) {
      MibValueSymbol augmentsSymbol = symbol.getMib().getSymbolByValue(rowType.getAugments());
      rowType = (SnmpObjectType) augmentsSymbol.getType();
    }
    
    List indexes = rowType.getIndex();
    this.indexes = new SnmpIndex[indexes.size()];
    for (int i = 0; i < this.indexes.length; i++) {
      this.indexes[i] = (SnmpIndex) indexes.get(i);
    }
    
  }
  
  @Override
  public IndexDescriptor[] extractIndexes(String instanceOid) {
    String oid = symbol.getValue().toString();
    String suboid = instanceOid.substring(oid.length() + 1);
    int[] components = oidToArray(suboid);
    int offset = 0;
    IndexDescriptor[] descriptors = new IndexDescriptor[indexes.length];
    for (int i = 0; i < indexes.length; i++) {
      SnmpIndex index = indexes[i];
      MibValueSymbol indexSymbol = (MibValueSymbol) 
          mib.findSymbolByOid(index.getValue().toString());     
      MibType indexType = ((SnmpObjectType) indexSymbol.getType()).getSyntax();
      int length = fixedLength(indexType);
      boolean implied = length != -1 || index.isImplied();
      if (length == -1) {
        length = variableLength(indexType, components, offset, index.isImplied());
      }
      int[] encoded = new int[length];
      System.arraycopy(components, offset, encoded, 0, length);
      
      descriptors[i] = new MibbleIndexDescriptor(indexSymbol, encoded, implied);
      offset += length;
    }
    return descriptors;
  }

  private static int[] oidToArray(String oid) {
    String[] tokens = oid.split("\\.");
    int[] components = new int[tokens.length];
    for (int i = 0; i < components.length; i++) {
      components[i] = Integer.parseInt(tokens[i]);
    }
    return components;
  }

  private static int fixedLength(MibType type) {
    if (type.hasTag(MibTypeTag.INTEGER)
        || type.hasTag(MibTypeTag.APPLICATION_CATEGORY, SnmpApplicationTypes.COUNTER32)
        || type.hasTag(MibTypeTag.APPLICATION_CATEGORY, SnmpApplicationTypes.GAUGE32)
        || type.hasTag(MibTypeTag.APPLICATION_CATEGORY, SnmpApplicationTypes.TIME_TICKS)) {
      return 1;
    }
    else if (type.hasTag(MibTypeTag.APPLICATION_CATEGORY, SnmpApplicationTypes.IP_ADDRESS)) {
      return 4;
    }
    else if (type.hasTag(MibTypeTag.OCTET_STRING))  {
      List constraints = ((SizeConstraint) 
          ((StringType) type).getConstraint()).getValues();
      for (int i = 0; i < constraints.size(); i++) {
        if (constraints.get(i) instanceof ValueConstraint) {
          MibValue sizeValue = ((ValueConstraint) constraints.get(0)).getValue();
          return ((Number) sizeValue.toObject()).intValue();
        }
      }
    }
    return -1;
  }
  
  private static int variableLength(MibType type, int[] components, int offset,
      boolean impliedLength) {
    if (type.hasTag(MibTypeTag.OBJECT_IDENTIFIER)
        || type.hasTag(MibTypeTag.OCTET_STRING)) {
      return impliedLength ? offset - components.length : components[offset] + 1;
    }
    throw new IllegalStateException("cannot determine index length");
  }

  public static void main(String[] args) throws Exception {
    MIB mib = new MibbleMIB();
    mib.load("BRIDGE-MIB");
    mib.load("IPV6-MIB");

    String oid1 = mib.nameToOid("dot1dTpFdbPort") + ".0.0.12.26.43.60";
    //2001:468:C80:2100
    String oid2 = mib.nameToOid("ipv6AddrPrefixOnLinkFlag") + ".3.8.32.1.4.104.12.128.33.0.64";
    String[] oids = { oid1, oid2 };
    for (String oid : oids) {
      IndexExtractor extractor = mib.newIndexExtractor(oid);
      for (IndexDescriptor descriptor : extractor.extractIndexes(oid)) {
        System.out.format("%s=%s\n", descriptor.getOid(),
            Arrays.asList(descriptor.getEncoded()));
      }
    }
  }
}
