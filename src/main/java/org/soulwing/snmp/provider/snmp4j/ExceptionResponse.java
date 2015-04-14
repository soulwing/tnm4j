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

import org.soulwing.snmp.SnmpException;
import org.soulwing.snmp.SnmpResponse;
import org.soulwing.snmp.TimeoutException;

/**
 * A response that represents an exception that occurred in communicating
 * with the remote agent.
 *
 * @author Carl Harris
 */
class ExceptionResponse<V> implements SnmpResponse<V> {

  private final Exception ex;
  
  /**
   * Constructs a new instance.
   * @param ex
   */
  public ExceptionResponse(Exception ex) {
    this.ex = ex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V get() throws SnmpException, TimeoutException {
    if (ex instanceof SnmpException) {
      throw (SnmpException) ex;
    }
    throw new SnmpException(ex);
  }

}
