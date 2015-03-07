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

import org.soulwing.snmp.Formatter;

import net.percederberg.mibble.MibValueSymbol;

class BitsFormatter implements Formatter {

  private final MibValueSymbol[] symbols;
  
  public BitsFormatter(MibValueSymbol[] symbols) {
    this.symbols = symbols;
  }
  
  public String format(Object obj) {
    byte[] value = (byte[]) obj;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < value.length; i++) {
      for (int j = 0; j < 8; j++) {
        int mask = 0x80 >>> j;
        if ((value[i] & mask) != 0) {
          sb.append(sb.length() == 0 ? '[' : ' ');
          sb.append(findName(8*i + j));
        }
      }
    }
    if (sb.length() == 0) {
      sb.append('[');
    }
    sb.append(']');
    return sb.toString();
  }

  private String findName(int value) {
    for (int i = 0; i < symbols.length; i++) {
      if (symbols[i].getValue().toObject().equals(value)) {
        return symbols[i].getName();
      }
    }
    return "UNDEFINED(" + value + ")";
  }
  
}
