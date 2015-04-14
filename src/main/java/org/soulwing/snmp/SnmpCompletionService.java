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
 * A service that awaits the completion of {@link SnmpOperation} instances.
 *
 * @author Carl Harris
 */
public interface SnmpCompletionService<V> {

  /**
   * Tests whether this completion service has a remaining event that has 
   * not yet been retrieved.
   * @return {@code true} if an incomplete response event remains
   */
  boolean isIdle();
  
  /**
   * Gets an event representing a completed response if one is available.
   * @return completed response event or {@code null} if none is available
   */
  SnmpEvent<V> poll();
  
  /**
   * Gets a response event waiting as long as the specified duration for
   * one to become available.
   * @param timeout timeout in milliseconds
   * @return response event or {@code null} if one did not become available
   *    within the specified time interval
   * @throws InterruptedException if the calling thread is interrupted 
   *    while waiting
   */
  SnmpEvent<V> poll(long timeout) throws InterruptedException;
  
  /**
   * Gets a completed response waiting until one becomes available.
   * @return completed response
   * @throws InterruptedException if the calling thread is interrupted
   *    while waiting
   */
  SnmpEvent<V> take() throws InterruptedException;

  /**
   * Submits an operation to the service.
   * <p>
   * The operation is invoked asynchronously and its response is placed into
   * the queue managed by this service.
   * service.
   * @param operation the operation to invoke
   */
  void submit(SnmpOperation<V> operation);
  
}
