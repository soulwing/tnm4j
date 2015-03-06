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
