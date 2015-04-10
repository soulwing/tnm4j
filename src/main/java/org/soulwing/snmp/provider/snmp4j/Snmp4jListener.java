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
package org.soulwing.snmp.provider.snmp4j;

import static org.soulwing.snmp.provider.snmp4j.Snmp4jLogger.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.Snmp;
import org.snmp4j.smi.Address;
import org.soulwing.snmp.SnmpException;
import org.soulwing.snmp.SnmpListener;
import org.soulwing.snmp.SnmpNotificationEvent;
import org.soulwing.snmp.SnmpNotificationHandler;

/**
 * An {@link SnmpListener} implementation based on SNMP4j.
 *
 * @author Carl Harris
 */
class Snmp4jListener implements SnmpListener, CommandResponder {

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  private final List<PrioritizedHandler> handlers =
      new ArrayList<PrioritizedHandler>();

  private final Snmp snmp;
  private final Address listenAddress;
  private final SnmpNotificationEventFactory eventFactory;
  private final DisposeListener disposeListener;

  private volatile boolean closed;

  public Snmp4jListener(Snmp snmp, Address listenAddress,
      SnmpNotificationEventFactory eventFactory,
      DisposeListener disposeListener) {
    this.snmp = snmp;
    this.listenAddress = listenAddress;
    this.eventFactory = eventFactory;
    this.disposeListener = disposeListener;
  }

  @Override
  public void addHandler(SnmpNotificationHandler handler) {
    addHandler(handler, Integer.MAX_VALUE);
  }

  @Override
  public void addHandler(SnmpNotificationHandler handler, int priority) {
    assertNotClosed();
    PrioritizedHandler wrapper = new PrioritizedHandler(priority, handler);
    if (handlers.contains(wrapper)) return;
    lock.writeLock().lock();
    try {
      int index = priority < Integer.MAX_VALUE ? 0 : handlers.size();
      while (index < handlers.size()
          && handlers.get(index).priority <= priority) {
        index++;
      }
      handlers.add(index, wrapper);
    }
    finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void removeHandler(SnmpNotificationHandler handler) {
    assertNotClosed();
    lock.writeLock().lock();
    try {
      handlers.remove(new PrioritizedHandler(0, handler));
    }
    finally {
      lock.writeLock().unlock();
    }
  }

  private void assertNotClosed() {
    if (!closed) return;
    throw new IllegalStateException("listener is closed");
  }

  public void open() throws SnmpException {
    if (!snmp.addNotificationListener(listenAddress, this)) {
      throw new SnmpException("failed to create listener");
    }
    logger.info("listening for notifications at {}", listenAddress);
  }

  @Override
  public void close() {
    if (closed) return;
    lock.writeLock().lock();
    try {
      snmp.removeNotificationListener(listenAddress);
      handlers.clear();
      closed = true;
      logger.info("closed listener at {}", listenAddress);
      disposeListener.onDispose(this);
    }
    finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void processPdu(CommandResponderEvent ev) {
    if (closed) return;
    if (logger.isTraceEnabled()) {
      logger.trace("listener {} received event: {}", this, ev);
    }
    try {
      notifyHandlers(eventFactory.newEvent(this, ev));
    }
    catch (RuntimeException ex) {
      logger.warn("listener exception: {}", ex.toString(), ex);
    }
  }

  void notifyHandlers(SnmpNotificationEvent event) {
    lock.readLock().lock();
    try {
      if (logger.isDebugEnabled()) {
        logger.debug("listener {} notifying handlers for event: {}",
            this, event);
      }
      for (PrioritizedHandler wrapper : handlers) {
        // we use a wrapper type here to allow for scripting languages that
        // don't require methods to return a value
        Boolean handled = wrapper.handler.handleNotification(event);
        if (handled != null && handled) {
          if (logger.isDebugEnabled()) {
            logger.debug("event was handled by {}", wrapper.handler);
          }
          break;
        }
      }
    }
    finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public String toString() {
    return listenAddress.toString();
  }

  static class PrioritizedHandler {
    final int priority;
    final SnmpNotificationHandler handler;

    PrioritizedHandler(int priority, SnmpNotificationHandler handler) {
      this.priority = priority;
      this.handler = handler;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) return true;
      if (!(obj instanceof PrioritizedHandler)) return false;
      return this.handler.equals(((PrioritizedHandler) obj).handler);
    }

    @Override
    public int hashCode() {
      return handler.hashCode();
    }
  }


}
