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
 * A response to an SNMP operation.
 *
 * @author Carl Harris
 */
public interface SnmpResponse<T> {

  /**
   * Gets the response value.
   * <p>
   * This method may block until a response is available.
   * @return value associated with the response
   * @throws SnmpException if an SNMP error occurs
   * @throws TimeoutException if a timeout occurs while awaiting a response
   *    from the remote agent
   */
  T get() throws SnmpException, TimeoutException;
  
}
