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

import java.math.BigDecimal;

import org.soulwing.snmp.Formatter;

class IntegerFormatter implements Formatter {

  private final String hint;
  
  public IntegerFormatter(String hint) {
    this.hint = hint;
  }
  
  public String format(Object value) {
    long i = ((Number) value).longValue();
    if (hint.length() == 0) {
      throw new IllegalArgumentException("invalid format specifier");
    }
    char format = hint.charAt(0);
    if (Character.toLowerCase(format) == 'd') {
      if (hint.length() > 1) {
        int decimalPosition = -Integer.parseInt(hint.substring(1));
        BigDecimal d = BigDecimal.valueOf(i);
        while (decimalPosition-- > 0) {
          d = d.divide(BigDecimal.valueOf(10), BigDecimal.ROUND_UNNECESSARY);
        }
        return d.toString();
      }
    }
    return Long.toString(i, RadixUtil.radixForFormat(format));    
  }
  
}
