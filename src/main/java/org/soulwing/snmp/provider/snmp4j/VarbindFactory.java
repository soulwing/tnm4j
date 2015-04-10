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

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.soulwing.snmp.Mib;
import org.soulwing.snmp.Varbind;
import org.soulwing.snmp.VarbindCollection;

/**
 * A factory that produces {@link Varbind} objects.
 */
interface VarbindFactory {

  /**
   * Gets the MIB associated with this factory.
   * @return MIB
   */
  Mib getMib();

  /**
   * Creates a new varbind from an SNMP4j {@link VariableBinding}.
   * @param vb the subject variable binding
   * @return new varbind instance
   */
  Varbind newVarbind(VariableBinding vb);

  /**
   * Creates a new varbind collection from all of the variable bindings
   * in the given SNMP4j {@link PDU}.
   * @param pdu the subject PDU
   * @return new populated varbind collection
   */
  VarbindCollection newVarbindCollection(PDU pdu);

}
