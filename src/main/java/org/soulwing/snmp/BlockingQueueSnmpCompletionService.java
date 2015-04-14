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

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * An {@link SnmpCompletionService} that is backed by a {@link BlockingQueue}.
 *
 * @author Carl Harris
 */
public class BlockingQueueSnmpCompletionService<V> 
    implements SnmpCompletionService<V> {

  private final Map<SnmpCallback<V>, SnmpCallback<V>> pending = 
      new ConcurrentHashMap<SnmpCallback<V>, SnmpCallback<V>>();
  
  private final BlockingQueue<SnmpEvent<V>> queue;

  /**
   * Creates a new completion service backed by an unbounded blocking queue.
   */
  public BlockingQueueSnmpCompletionService() {
    this(new LinkedBlockingQueue<SnmpEvent<V>>());
  }

  /**
   * Creates a new completion service backed by the given blocking queue.
   * @param queue queue that will be used to buffer received response data
   */
  public BlockingQueueSnmpCompletionService(BlockingQueue<SnmpEvent<V>> queue) {
    this.queue = queue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isIdle() {
    return pending.isEmpty() && queue.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpEvent<V> poll() {
    return queue.poll();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpEvent<V> poll(long timeout) throws InterruptedException {
    return queue.poll(timeout, TimeUnit.MILLISECONDS);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpEvent<V> take() throws InterruptedException {
    return queue.take();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void submit(SnmpOperation<V> operation) {
    SnmpCallback<V> callback = new SnmpCallback<V>() {
      @Override
      public void onSnmpResponse(SnmpEvent<V> event) {
        pending.remove(this);
        queue.offer(event);
      } 
    };
    
    pending.put(callback, callback);
    operation.invoke(callback);
  }

}
