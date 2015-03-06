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

import net.percederberg.mibble.MibType;
import net.percederberg.mibble.MibTypeSymbol;
import net.percederberg.mibble.MibTypeTag;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.percederberg.mibble.snmp.SnmpTextualConvention;
import net.percederberg.mibble.type.BitSetType;
import net.percederberg.mibble.type.ChoiceType;
import net.percederberg.mibble.type.IntegerType;

class ObjectFormatter implements Formatter {

  // Pre-SMIv2 textual conventions and types
  private static final String NETWORK_ADDRESS_TYPE = "NetworkAddress";  // RFC 1155 section 3.2.3.1
  private static final String DISPLAY_STRING_TYPE = "DisplayString";    // RFC 1213 section 3.2
  
  // default display hints
  private static final String INTEGER_HINT = "d";
  private static final String DISPLAY_STRING_HINT = "1a";
  private static final String OCTET_STRING_HINT = "1x:";
  private static final String IP_ADDRESS_HINT = "1d.";
  
  private final Formatter delegate;

  public ObjectFormatter(MibValueSymbol symbol) {
    if (!(symbol.getType() instanceof SnmpObjectType)) {
      this.delegate = new ToStringFormatter();
    }
    else {
      try {
        this.delegate = formatter(((SnmpObjectType) symbol.getType()).getSyntax());
      }
      catch (IllegalArgumentException ex) {
        throw new IllegalArgumentException("cannot format symbol: " + symbol, ex);
      }
    }
  }

  private static Formatter formatter(MibType syntax) {
    if (syntax.hasTag(MibTypeTag.INTEGER)) {
      if (((IntegerType) syntax).hasSymbols()) {
        return new EnumFormatter(((IntegerType) syntax).getAllSymbols());
      }
      return new IntegerFormatter(displayHint(syntax, INTEGER_HINT));
    }
    else if (syntax.hasTag(MibTypeTag.BIT_STRING)) {
      return new BitsFormatter(((BitSetType) syntax).getAllSymbols()); 
    }
    else if (syntax.hasTag(MibTypeTag.OCTET_STRING)) {
      return new OctetStringFormatter(displayHint(syntax, OCTET_STRING_HINT));
    }
    else if (syntax.hasTag(MibTypeTag.OBJECT_IDENTIFIER)) {
      return new ObjectIdentifierFormatter();
    }
    else if (syntax.hasTag(MibTypeTag.APPLICATION_CATEGORY, SnmpApplicationTypes.IP_ADDRESS)) {
      return new OctetStringFormatter(IP_ADDRESS_HINT);
    }
    else if (syntax.hasTag(MibTypeTag.APPLICATION_CATEGORY, SnmpApplicationTypes.GAUGE32)) {
      return new IntegerFormatter(displayHint(syntax, INTEGER_HINT));
    }
    else if (syntax.hasTag(MibTypeTag.APPLICATION_CATEGORY, SnmpApplicationTypes.TIME_TICKS)) {
      return new TimeTicksFormatter(displayHint(syntax, null));
    }
    else if (syntax.hasTag(MibTypeTag.APPLICATION_CATEGORY, SnmpApplicationTypes.OPAQUE)) {
      return new OctetStringFormatter(OCTET_STRING_HINT);
    }
    else if (syntax.hasTag(MibTypeTag.APPLICATION_CATEGORY, SnmpApplicationTypes.COUNTER32)
        || syntax.hasTag(MibTypeTag.APPLICATION_CATEGORY, SnmpApplicationTypes.COUNTER64)) {
      return new IntegerFormatter(INTEGER_HINT);
    }
    else if (syntax instanceof ChoiceType) {
      if (syntax.getReferenceSymbol() instanceof MibTypeSymbol) {
        if (syntax.getReferenceSymbol().getName().equals(NETWORK_ADDRESS_TYPE)) {
          return new OctetStringFormatter(IP_ADDRESS_HINT);
        }
      }
    }
    throw new IllegalArgumentException("unknown syntax: " + syntax);
  }
  
  private static String displayHint(MibType syntax, String defaultHint) {
    SnmpTextualConvention tc = textualConvention(syntax);
    if (tc != null) {
      if (tc.getDisplayHint() != null) {
        return tc.getDisplayHint();
      }
    }
    if (syntax.getReferenceSymbol() instanceof MibTypeSymbol) {
      if (syntax.getReferenceSymbol().getName().equals(DISPLAY_STRING_TYPE)) {
        return DISPLAY_STRING_HINT;
      }
    }
    return defaultHint;
  }
  
  private static SnmpTextualConvention textualConvention(MibType syntax) {
    SnmpTextualConvention tc = null;
    if (syntax.getReferenceSymbol() != null) {
      if (syntax.getReferenceSymbol().getType() instanceof SnmpTextualConvention) {
        tc = (SnmpTextualConvention) syntax.getReferenceSymbol().getType();
      }
    }
    return tc;
  }

  public String format(Object value) {
    return delegate.format(value);
  }

}
