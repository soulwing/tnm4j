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
package org.soulwing.snmp.provider.mibble;

import java.util.List;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibType;
import net.percederberg.mibble.MibTypeTag;
import net.percederberg.mibble.MibValue;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpIndex;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.percederberg.mibble.type.SizeConstraint;
import net.percederberg.mibble.type.StringType;
import net.percederberg.mibble.type.ValueConstraint;
import org.soulwing.snmp.IndexDescriptor;
import org.soulwing.snmp.IndexExtractor;

class MibbleIndexExtractor implements IndexExtractor {

  private final MibValueSymbol symbol;
  private final SnmpIndex[] indexes;

  public MibbleIndexExtractor(MibValueSymbol symbol) {
    if (!symbol.isTableColumn()) {
      throw new IllegalArgumentException("symbol is not a table column: " 
          + symbol.getName());
    }
    
    this.symbol = symbol;
    MibValueSymbol rowSymbol = symbol.getParent();
    SnmpObjectType rowType = (SnmpObjectType) rowSymbol.getType();
    if (rowType.getAugments() != null) {
      MibValueSymbol augmentsSymbol = symbol.getMib().getSymbolByValue(rowType.getAugments());
      rowType = (SnmpObjectType) augmentsSymbol.getType();
    }
    
    List<?> indexes = rowType.getIndex();
    this.indexes = new SnmpIndex[indexes.size()];
    for (int i = 0; i < this.indexes.length; i++) {
      this.indexes[i] = (SnmpIndex) indexes.get(i);
    }
    
  }
  
  @Override
  public IndexDescriptor[] extractIndexes(String instanceOid) {
    String oid = symbol.getValue().toString();

    // GitHub issue 9 fix; we might be asked to extract indexes from an
    // OID that represents a type, not an instance.
    if (oid.equals(instanceOid)) return new IndexDescriptor[0];

    String suboid = instanceOid.substring(oid.length() + 1);
    int[] components = oidToArray(suboid);
    int offset = 0;
    IndexDescriptor[] descriptors = new IndexDescriptor[indexes.length];
    for (int i = 0; i < indexes.length; i++) {
      SnmpIndex index = indexes[i];
      MibValueSymbol indexSymbol =
          symbol.getMib().getSymbolByOid(index.getValue().toString());
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
      List<?> constraints = ((SizeConstraint) 
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

}
