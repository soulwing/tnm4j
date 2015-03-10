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

/**
 * An object that describes an asynchronous SNMP event.
 *
 * @author Carl Harris
 */
public class SnmpEvent<V> {

  private final SnmpContext context;
  private final SnmpResponse<V> response;
  
  /**
   * Constructs a new instance.
   * @param context
   * @param response
   */
  public SnmpEvent(SnmpContext context, SnmpResponse<V> response) {
    this.context = context;
    this.response = response;
  }

  /**
   * Gets the {@code context} property.
   * @return property value
   */
  public SnmpContext getContext() {
    return context;
  }

  /**
   * Gets the {@code response} property.
   * @return property value
   */
  public SnmpResponse<V> getResponse() {
    return response;
  }

}
