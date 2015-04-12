/*
 * File created on Apr 9, 2015
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
package org.soulwing.snmp.provider.snmp4j;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.soulwing.snmp.Formatter;
import org.soulwing.snmp.IndexExtractor;
import org.soulwing.snmp.Mib;
import org.soulwing.snmp.MutableVarbindCollection;
import org.soulwing.snmp.Varbind;
import org.soulwing.snmp.VarbindCollection;

/**
 * A simple {@link VarbindFactory}.
 *
 * @author Carl Harris
 */
class SimpleVarbindFactory implements VarbindFactory {

  private final Mib mib;

  public SimpleVarbindFactory(Mib mib) {
    this.mib = mib;
  }

  @Override
  public Mib getMib() {
    return mib;
  }

  @Override
  public Varbind newVarbind(VariableBinding vb) {
    String oid = vb.getOid().toString();
    String name = mib.oidToInstanceName(oid);
    Formatter formatter = mib.newFormatter(oid);
    IndexExtractor indexExtractor = createIndexExtractor(oid);
    return new Snmp4jVarbind(name, vb, formatter, indexExtractor, this);
  }

  @Override
  public VarbindCollection newVarbindCollection(PDU pdu) {
    MutableVarbindCollection results = new MutableVarbindCollection();
    Varbind[] indexes = new Varbind[0];
    for (int i = 0; i < pdu.size(); i++) {
      Varbind varbind = newVarbind(pdu.get(i));
      results.add(i, objectNameToKey(varbind), varbind);
      if (indexes.length == 0) {
        indexes = varbind.getIndexes();
      }
    }
    for (Varbind index : indexes) {
      results.addIndex(objectNameToKey(index), index);
    }
    return results.immutableCopy();
  }

  protected String objectNameToKey(Varbind v) {
    String oid = v.getOid();
    String name = v.getName();
    if (name.equals(oid)) return oid;
    int index = name.indexOf('.');
    return index != -1 ? name.substring(0, index) : name;
  }

  private IndexExtractor createIndexExtractor(String oid) {
    try {
      return mib.newIndexExtractor(oid);
    }
    catch (IllegalArgumentException ex) {
      return null;
    }
  }

}
