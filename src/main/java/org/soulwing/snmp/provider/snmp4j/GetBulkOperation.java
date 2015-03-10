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

/**
 * An SNMP GETBULK operation.
 *
 * @author Carl Harris
 */
class GetBulkOperation extends VarbindCollectionOperation {

  private final int nonRepeaters;
  private final int maxRepetitions;
  
  /**
   * Constructs a new instance.
   * @param context
   * @param oids
   */
  public GetBulkOperation(Snmp4jContext context, OID[] oids,
      int nonRepeaters, int maxRepetitions) {
    super(context, oids);
    this.nonRepeaters = nonRepeaters;
    this.maxRepetitions = maxRepetitions;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ResponseEvent doInvoke(PDU request) throws IOException {
    configureRequest(request);
    return context.getSnmp().getBulk(request, context.getSnmp4jTarget());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doInvoke(PDU request, Object userObject) throws IOException {
    configureRequest(request);
    context.getSnmp().getBulk(request, context.getSnmp4jTarget(), userObject, 
        this);
  }

  private void configureRequest(PDU request) {
    request.setNonRepeaters(nonRepeaters);
    request.setMaxRepetitions(maxRepetitions);
  }
  
}
