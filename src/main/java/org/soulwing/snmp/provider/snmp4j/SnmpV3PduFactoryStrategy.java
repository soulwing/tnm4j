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
import org.snmp4j.ScopedPDU;
import org.snmp4j.smi.OctetString;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.SnmpV3Target;

/**
 * A {@link PduFactoryStrategy} for an SNMPv2c target.
 *
 * @author Carl Harris
 */
class SnmpV3PduFactoryStrategy implements PduFactoryStrategy {
  
  /**
   * {@inheritDoc}
   */
  @Override
  public PduFactory newPduFactory(SnmpTarget target) {
    if (!(target instanceof SnmpV3Target)) return null;
    return new SnmpV3PduFactory((SnmpV3Target) target);
  }
  
  static class SnmpV3PduFactory implements PduFactory {

    private final SnmpV3Target target;
    
    /**
     * Constructs a new instance.
     * @param target
     */
    SnmpV3PduFactory(SnmpV3Target target) {
      this.target = target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PDU newPDU() {
      ScopedPDU pdu = new ScopedPDU();
      if (target.getScope() != null) {
        pdu.setContextName(new OctetString(target.getScope()));
      }
      return pdu;
    }
    
  }

}
