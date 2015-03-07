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
import net.percederberg.mibble.value.NumberValue;

class EnumFormatter implements Formatter {

  private final MibValueSymbol[] symbols;

  public EnumFormatter(MibValueSymbol[] symbols) {
    this.symbols = symbols;
  }
  
  public String format(Object value) {
    NumberValue numberValue = new NumberValue((Number) value);
    for (int i = 0; i < symbols.length; i++) {
      if (symbols[i].getValue().equals(numberValue)) {
        return symbols[i].getName();
      }
    }
    return value.toString();
  }
  
}
