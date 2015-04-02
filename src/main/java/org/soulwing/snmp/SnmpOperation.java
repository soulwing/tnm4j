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
 * An SNMP operation.
 *
 * @author Carl Harris
 */
public interface SnmpOperation<V> {

  /**
   * Invoke the operation on the remote agent and wait for the response.
   * <p>
   * This method will block until a response becomes available.
   * @return response object
   */
  SnmpResponse<V> invoke();
  
  /**
   * Invoke this operation, notifying the specified callback when a 
   * response is available.
   * @param callback callback that will be notified when a response is
   *    available
   */
  void invoke(SnmpCallback<V> callback);
  
}
