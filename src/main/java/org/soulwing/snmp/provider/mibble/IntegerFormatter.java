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
          d = d.divide(BigDecimal.valueOf(10));
        }
        return d.toString();
      }
    }
    return Long.toString(i, RadixUtil.radixForFormat(format));    
  }
  
}
