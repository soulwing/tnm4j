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
import org.snmp4j.smi.VariableBinding;

/**
 * An SNMP GET operation.
 *
 * @author Carl Harris
 */
class GetOperation extends VarbindCollectionOperation {

  /**
   * Constructs a new instance.
   * @param context
   * @param varbinds
   */
  public GetOperation(Snmp4jContext context, VariableBinding[] varbinds) {
    super(context, varbinds);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ResponseEvent doInvoke(PDU request) throws IOException {
    return context.getSession().get(request, context.getSnmp4jTarget());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doInvoke(PDU request, Object userObject)
      throws IOException {
    context.getSession().get(request, context.getSnmp4jTarget(), userObject, this);
  }

}
