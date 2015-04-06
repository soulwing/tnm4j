/*
 * File created on Apr 3, 2015
 *
 * Copyright (c) 2015 Carl Harris, Jr
 * and others as noted
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.soulwing.snmp.provider.mibble;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.percederberg.mibble.MibValueSymbol;
import org.soulwing.snmp.Formatter;

/**
 * A {@link FormatterFactory} that caches and reuses formatters for a given
 * object type.
 *
 * @author Carl Harris
 */
class CachingFormatterFactory implements FormatterFactory {

  static final Formatter TO_STRING_FORMATTER = new ToStringFormatter();

  private final ConcurrentMap<MibValueSymbol, Formatter> formatterCache =
      new ConcurrentHashMap<MibValueSymbol, Formatter>();

  @Override
  public Formatter getFormatter(MibValueSymbol symbol) {
    if (symbol == null) {
      return TO_STRING_FORMATTER;
    }
    Formatter formatter = formatterCache.get(symbol);
    if (formatter == null) {
      formatter = newObjectFormatter(symbol);
      formatterCache.put(symbol, formatter);
    }
    return formatter;
  }

  Formatter newObjectFormatter(MibValueSymbol symbol) {
    return new ObjectFormatter(symbol);
  }

}

