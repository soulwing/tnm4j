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

import org.snmp4j.util.ThreadFactory;
import org.snmp4j.util.WorkerTask;

/**
 * An SNMP4j {@link ThreadFactory} layered over a Java SE 
 * {@link ThreadFactory}.
 *
 * @author Carl Harris
 */
class Snmp4jThreadFactory implements ThreadFactory {

  private final java.util.concurrent.ThreadFactory delegate;
    
  /**
   * Constructs a new instance.
   * @param delegate
   */
  public Snmp4jThreadFactory(java.util.concurrent.ThreadFactory delegate) {
    this.delegate = delegate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WorkerTask createWorkerThread(String name, WorkerTask task,
      boolean daemon) {
    Thread thread = delegate.newThread(task);
    thread.setName(name);
    thread.setDaemon(daemon);
    return new ThreadWorkerTask(thread, task);
  }

  
  private static class ThreadWorkerTask implements WorkerTask {

    private final Thread delegate;
    private final WorkerTask task;
    
    public ThreadWorkerTask(Thread delegate, WorkerTask task) {
      this.delegate = delegate;
      this.task = task;
    }

    @Override
    public void run() {
      delegate.start();
    }

    @Override
    public void terminate() {
      task.terminate();
    }

    @Override
    public void join() throws InterruptedException {
      task.join();
    }

    @Override
    public void interrupt() {
      task.interrupt();
    }

  }

}
