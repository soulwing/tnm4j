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
import org.soulwing.snmp.IndexExtractor;

/**
 * A {@link IndexExtractorFactory} that caches and reuses index extractors
 * for a given object type.
 *
 * @author Carl Harris
 */
class CachingIndexExtractorFactory implements IndexExtractorFactory {

  private final ConcurrentMap<MibValueSymbol, IndexExtractor> cache =
      new ConcurrentHashMap<MibValueSymbol, IndexExtractor>();

  @Override
  public IndexExtractor getIndexExtractor(MibValueSymbol symbol) {
    if (symbol == null) {
      return null;
    }
    if (!symbol.isTableColumn()) {
      throw new IllegalArgumentException(symbol.getName()
          + ": not a table column");
    }

    IndexExtractor extractor = cache.get(symbol);
    if (extractor == null) {
      extractor = new MibbleIndexExtractor(symbol);
      cache.put(symbol, extractor);
    }
    return extractor;
  }

}
