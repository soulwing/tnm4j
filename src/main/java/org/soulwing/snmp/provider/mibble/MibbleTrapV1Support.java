/*
 * File created on Apr 10, 2015
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibTypeSymbol;
import net.percederberg.mibble.type.ElementType;
import net.percederberg.mibble.type.IntegerType;
import net.percederberg.mibble.type.SequenceType;
import org.soulwing.snmp.Formatter;
import org.soulwing.snmp.MibTrapV1Support;

/**
 * A {@link MibbleTrapV1Support} implementation based on Mibble.
 *
 * @author Carl Harris
 */
class MibbleTrapV1Support implements MibTrapV1Support {

  private static final String TRAP_DEFINITIONS = "RFC1157-SNMP";
  private static final String SPECIFIC_TRAP_HINT = "d";
  private static final String TRAP_PDU_TYPE = "Trap-PDU";
  private static final String GENERIC_TRAP_MEMBER = "generic-trap";
  private static final String SMI_DEFINITIONS = "RFC1155-SMI";

  private static volatile MibbleTrapV1Support instance;

  private static final Lock lock = new ReentrantLock();

  private final Formatter genericTrapFormatter;
  private final Formatter specificTrapFormatter;
  private final Formatter timestampFormatter;

  private MibbleTrapV1Support(Formatter genericTrapFormatter) {
    this.genericTrapFormatter = genericTrapFormatter;
    this.specificTrapFormatter = new IntegerFormatter(SPECIFIC_TRAP_HINT);
    this.timestampFormatter = new TimeTicksFormatter(null);
  }

  @Override
  public Formatter getGenericTrapFormatter() {
    return genericTrapFormatter;
  }

  @Override
  public Formatter getSpecificTrapFormatter() {
    return specificTrapFormatter;
  }

  @Override
  public Formatter getTimestampFormatter() {
    return timestampFormatter;
  }

  public static MibbleTrapV1Support getInstance()
      throws MibLoaderException, IOException {
    if (instance == null) {
      lock.lock();
      try {
        if (instance == null) {
          instance = newInstance();
        }
      }
      finally {
        lock.unlock();
      }
    }
    return instance;
  }

  private static MibbleTrapV1Support newInstance()
      throws MibLoaderException, IOException {
    return new MibbleTrapV1Support(newGenericTypeFormatter());
  }

  private static EnumFormatter newGenericTypeFormatter()
      throws MibLoaderException, IOException {
    Mib mib = loadTrapMib();
    IntegerType genericTrapMember = findGenericTrapMember(findTrapType(mib));
    return new EnumFormatter(genericTrapMember.getAllSymbols());
  }

  private static Mib loadTrapMib() throws IOException, MibLoaderException {
    MibLoader loader = new MibLoader();
    loader.load(SMI_DEFINITIONS);
    URL url = MibbleTrapV1Support.class.getResource(TRAP_DEFINITIONS);
    if (url == null) {
      throw new FileNotFoundException("cannot find resource " + TRAP_DEFINITIONS);
    }
    return loader.load(url);
  }

  private static SequenceType findTrapType(Mib mib) {
    MibTypeSymbol trapSymbol = (MibTypeSymbol) mib.getSymbol(TRAP_PDU_TYPE);
    if (trapSymbol == null) {
      throw new AssertionError(TRAP_PDU_TYPE + " does not exist");
    }
    return (SequenceType) trapSymbol.getType();
  }

  private static IntegerType findGenericTrapMember(SequenceType trapType) {
    for (ElementType element : trapType.getAllElements()) {
      if (element.getName().equals(GENERIC_TRAP_MEMBER)) {
        return (IntegerType) element.getType();
      }
    }
    throw new AssertionError("can't find " + GENERIC_TRAP_MEMBER + " in "
        + TRAP_PDU_TYPE);
  }

}
