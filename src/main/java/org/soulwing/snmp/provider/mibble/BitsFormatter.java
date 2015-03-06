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
