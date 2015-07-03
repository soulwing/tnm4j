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

package org.soulwing.snmp.provider.snmp4j;

import java.io.IOException;

import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.soulwing.snmp.MutableVarbindCollection;
import org.soulwing.snmp.SnmpAsyncWalker;
import org.soulwing.snmp.Varbind;
import org.soulwing.snmp.VarbindCollection;

/**
 * An {@link SnmpAsyncWalker} implemented using an SNMP GETBULK operation.
 *
 * @author Carl Harris
 */
class GetBulkAsyncWalker
    extends AbstractAsyncWalker<VarbindCollection> {

  public GetBulkAsyncWalker(Snmp4jContext context, VariableBinding[] varbinds,
      int nonRepeaters, int maxRepetitions) {
    super(context, varbinds, nonRepeaters, maxRepetitions);
  }

  @Override
  protected ResponseEvent doInvoke(PDU request) throws IOException {
    configureRequest(request);
    return context.getSession().getBulk(request, context.getSnmp4jTarget());
  }

  @Override
  protected void doInvoke(PDU request, Object userObject) throws IOException {
    configureRequest(request);
    context.getSession().getBulk(request, context.getSnmp4jTarget(), userObject,
        this);
  }

  private void configureRequest(PDU request) {
    request.setNonRepeaters(nonRepeaters);
    request.setMaxRepetitions(maxRepetitions);
  }

  @Override
  protected VarbindCollection createRow(PDU response, int nonRepeaters,
      int repeaters, int offset) {
    final int responseSize = response.size();
    MutableVarbindCollection row = new MutableVarbindCollection();
    for (int i = 0; i < nonRepeaters; i++) {
      if (i < responseSize
          && response.get(i).getOid().startsWith(varbinds[i].getOid())) {
        Varbind v = context.getVarbindFactory().newVarbind(response.get(i));
        row.add(i, objectNameToKey(v), v);
      }
    }
    if (repeaters > 0) {
      Varbind[] indexes = new Varbind[0];
      int count = 0;
      for (int i = 0; i < repeaters; i++) {
        if (i + offset < response.size()) {
          OID oid = response.get(i + offset).getOid();
          if (oid.startsWith(requestedVarbinds[i + nonRepeaters].getOid())) {
            Varbind v = context.getVarbindFactory()
                .newVarbind(response.get(i + offset));
            row.add(i + nonRepeaters, objectNameToKey(v), v);
            count++;
            if (indexes.length == 0) {
              indexes = v.getIndexes();
            }
          }
        }
      }
      for (Varbind index : indexes) {
        row.addIndex(objectNameToKey(index), index);
      }
    }
    return row.immutableCopy();
  }

}
