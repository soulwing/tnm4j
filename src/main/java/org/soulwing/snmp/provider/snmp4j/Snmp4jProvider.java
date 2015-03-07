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

import org.soulwing.snmp.MIB;
import org.soulwing.snmp.SNMPv2cContext;
import org.soulwing.snmp.SNMPv3Context;
import org.soulwing.snmp.provider.SNMPProvider;

/**
 * An {@link SNMPProvider} based on SNMP4j.
 *
 * @author Carl Harris
 */
public class Snmp4jProvider implements SNMPProvider {

  private static final String PROVIDER_NAME = "snmp4j";
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return PROVIDER_NAME;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SNMPv2cContext newSnmpV2cContext(MIB mib) {
    return new Snmp4jV2cContext(mib);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SNMPv3Context newSnmpV3Context(MIB mib) {
    return new Snmp4jV3Context(mib);
  }

}
