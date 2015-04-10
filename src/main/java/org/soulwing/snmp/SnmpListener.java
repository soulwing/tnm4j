/*
 * File created on Apr 9, 2015
 *
 * Copyright (c) 2015 Carl Harris, Jr
 * and others as noted
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.soulwing.snmp;

/**
 * A component that listens for SNMP notifications (traps, informs) and
 * makes them available to registered handlers.
 * <p>
 * The listener maintains an ordered collection of handlers, based on a priority
 * specified when each handler is registered.  When an SNMP notification is
 * received, registered handlers are invoked in priority order.  When a handler
 * indicates that it handled the event (via the return value of
 * {@link SnmpNotificationHandler#handleNotification(SnmpNotificationEvent)}),
 * handlers of lower priority are not invoked.
 *
 * @author Carl Harris
 */
public interface SnmpListener {

  /**
   * Adds a notification handler.
   * <p>
   * The handler will be added to the collection such that it is notified
   * after all previously registered handlers.
   * <p>
   * This method has no effect if a handler identical to {@code handler} is
   * already registered.
   * @param handler the handler to add
   */
  void addHandler(SnmpNotificationHandler handler);

  /**
   * Adds a notification handler.
   * <p>
   * The handler will be added to the collection, such that it is notified
   * after all previously registered handlers whose priority is less than or
   * equal to the specified {@code priority}.
   * <p>
   * This method has no effect if a handler identical to {@code handler} is
   * already registered.
   * @param handler the handler to add
   * @param priority priority for the handler
   */
  void addHandler(SnmpNotificationHandler handler, int priority);

  /**
   * Removes a notification handler.
   * <p>
   * This method has no effect if {@code handler} is not registered
   * @param handler the handler to remove
   */
  void removeHandler(SnmpNotificationHandler handler);

  /**
   * Closes this listener.
   * <p>
   * After the listener has been closed, no further notifications will be
   * distributed to handlers.
   */
  void close();

}
