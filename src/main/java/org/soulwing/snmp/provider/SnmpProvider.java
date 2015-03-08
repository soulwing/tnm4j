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
package org.soulwing.snmp.provider;

import org.soulwing.snmp.Mib;
import org.soulwing.snmp.SnmpConfiguration;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpTarget;

/**
 * A provider of {@link SnmpContext} objects.
 *
 * @author Carl Harris
 */
public interface SnmpProvider {

  /**
   * Gets the provider name.
   * @return provider name (never {@code null})
   */
  String getName();
  
  /**
   * Creates a new SNMPv2c context.
   * @param target target agent
   * @param config context configuration; the provider can safely 
   *    assume that the configuration is immutable for the lifetime of the
   *    returned context 
   * @param mib MIB provider
   * @return SNMP context
   */
  SnmpContext newContext(SnmpTarget target, SnmpConfiguration config, 
      Mib mib);

}
