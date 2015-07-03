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

import org.snmp4j.smi.VariableBinding;
import org.soulwing.snmp.SnmpResponse;
import org.soulwing.snmp.SnmpWalker;
import org.soulwing.snmp.VarbindCollection;
import org.soulwing.snmp.WouldBlockException;

/**
 * An {@link SnmpWalker} implemented using an SNMP GETBULK operation.
 *
 * @author Carl Harris
 */
class GetBulkSyncWalker extends GetBulkAsyncWalker {

  /**
   * Constructs a new instance.
   * @param context
   * @param varbinds
   * @param nonRepeaters
   * @param maxRepetitions
   */
  public GetBulkSyncWalker(Snmp4jContext context, VariableBinding[] varbinds,
      int nonRepeaters, int maxRepetitions) {
    super(context, varbinds, nonRepeaters, maxRepetitions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<VarbindCollection> next() {
    try {
      return super.next();
    }
    catch (WouldBlockException ex) {
      return super.invoke().get().next();
    }
  }

}
