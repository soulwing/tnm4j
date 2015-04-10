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
 * An object that is registered with an {@link SnmpListener} to receive
 * SNMP notifications (traps, informs).
 *
 * @author Carl Harris
 */
public interface SnmpNotificationHandler {

  /**
   * Notifies the recipient that a notification was received by the
   * associated listener.
   * @param event an event containing the subject notification
   * @return {@code true} to indicate that the recipient has handled the
   *    notification and that other handlers (of lower priority) not yet
   *    invoked should not be notified
   */
  Boolean handleNotification(SnmpNotificationEvent event);

}
