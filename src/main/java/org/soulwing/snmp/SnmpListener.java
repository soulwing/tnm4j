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
 * Conceptually, a listener represents a provider component that listens for
 * incoming SNMP notifications at the transport layer of the network.  Typically,
 * an implementation binds a socket to an IP address and port and receives all
 * incoming SNMP notifications received on that socket.
 * <p>
 * A listener maintains an ordered collection of handlers, based on a priority
 * specified when each handler is registered.  When an SNMP notification is
 * received, registered handlers are invoked in priority order.  When a handler
 * indicates that it handled the event (via the return value of
 * {@link SnmpNotificationHandler#handleNotification(SnmpNotificationEvent)}),
 * handlers of lower priority are not invoked.
 * <p>
 * The listener can be used to implement a strategy for handling different types
 * of notifications, where a given handler supports one notification type (or
 * perhaps a set of closely related types).  For each received notification,
 * the listener will ask each registered handler (in priority order) to handle
 * the notification, stopping with the first handler that claims to have done so.
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
