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
package org.soulwing.snmp;

import java.util.Map;

/**
 * A object that represents an SNMP agent on a network.
 *
 * @author Carl Harris
 */
public interface SnmpTarget {

  /**
   * Gets the network address of the targeted agent. 
   * @return string representation of network address or hostname
   */
  String getAddress();
  
  /**
   * Gets the network port of the targeted agent.
   * @return network port number
   */
  int getPort();
  
  /**
   * Gets a map of provider-specific properties for the target.
   * @return target properties
   */
  Map<String, Object> getProperties();
  
}
