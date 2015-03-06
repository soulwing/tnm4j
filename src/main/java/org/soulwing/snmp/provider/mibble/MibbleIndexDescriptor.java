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
    int syntax = (category<<6) + value;
    return syntax;
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
