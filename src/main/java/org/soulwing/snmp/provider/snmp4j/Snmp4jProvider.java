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

import org.soulwing.snmp.Mib;
import org.soulwing.snmp.SnmpTargetConfig;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.provider.SnmpProvider;

/**
 * An {@link SnmpProvider} based on SNMP4j.
 *
 * @author Carl Harris
 */
public class Snmp4jProvider implements SnmpProvider {

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
  public SnmpContext newContext(SnmpTarget target, SnmpTargetConfig config, 
      Mib mib) {
    return Snmp4jContextFactory.newContext(target, config, mib);
  }

  @Override
  public void close() {
    Snmp4jContextFactory.close();
  }

}
