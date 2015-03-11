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
 * An object that walks a conceptual table asynchronously.
 * <p>
 * An asynchronous walker is an operation that when invoked, provides
 * a reference to a walker that can be used to retrieve rows from the
 * table.  It's {@link #next()} method will throw a {@link WouldBlockException}
 * if an additional operation is required to get the next row (or to
 * determine that the end of table has been reached).  On receipt of this
 * exception, the caller should then invoke the walker (as an operation) to
 * continue.
 *
 * @author Carl Harris
 */
public interface SnmpAsyncWalker<V> extends SnmpWalker<V>, 
    SnmpOperation<SnmpAsyncWalker<V>> {

  /**
   * Gets the next response from this walker.
   * @return next response or {@code null} to indicate that the end of the
   *    subject table has been reached. 
   * @throws WouldBlockException if another response is not available because
   *    it has not yet been retrieved from the remote agent
   */
  SnmpResponse<V> next() throws WouldBlockException;
  
}
