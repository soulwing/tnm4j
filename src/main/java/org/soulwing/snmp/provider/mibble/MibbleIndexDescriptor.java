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

import org.soulwing.snmp.IndexDescriptor;

import net.percederberg.mibble.MibType;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpObjectType;

class MibbleIndexDescriptor implements IndexDescriptor {

  private final MibValueSymbol indexSymbol;
  private final MibType indexType;
  private final int[] encoded;
  private final boolean implied;
  
  public MibbleIndexDescriptor(MibValueSymbol indexSymbol,
      int[] encoded, boolean implied) {
    this.indexSymbol = indexSymbol;
    this.indexType = ((SnmpObjectType) indexSymbol.getType()).getSyntax();
    this.encoded = encoded;
    this.implied = implied;
  }

  @Override
  public String getOid() {
    return indexSymbol.getValue().toString();
  }

  @Override
  public int getSyntax() {
    int category = indexType.getTag().getCategory();
    int value = indexType.getTag().getValue();
    return (category<<6) + value;
  }

  @Override
  public int[] getEncoded() {
    return encoded;
  }

  @Override
  public boolean isImplied() {
    return implied;
  }  

}
