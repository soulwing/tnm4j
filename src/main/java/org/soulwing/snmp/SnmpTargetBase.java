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

import java.util.HashMap;
import java.util.Map;

/**
 * An abstract base for {@link SnmpTarget} implementations.
 *
 * @author Carl Harris
 */
public abstract class SnmpTargetBase implements SnmpTarget {

  private String address;
  private int port = SnmpDefaults.AGENT_PORT;
  private Map<String, Object> properties = new HashMap<String, Object>();

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAddress() {
    return address;
  }

  /**
   * Sets the {@code address} property.
   * @param address the value to set
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPort() {
    return port;
  }

  /**
   * Sets the {@code port} property.
   * @param port the value to set
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Object> getProperties() {
    return properties;
  }

  /**
   * Sets the {@code properties} property.
   * @param properties the value to set
   */
  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }

  @Override
  public String toString() {
    return String.format("%s/%d", address, port);
  }

}
