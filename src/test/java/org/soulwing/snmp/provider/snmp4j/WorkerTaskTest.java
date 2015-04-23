/*
 * File created on Apr 23, 2015
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Before;
import org.junit.Test;
import org.snmp4j.util.WorkerTask;

/**
 * Integration tests for {@link WorkerTask} objects produced by
 * {@link Snmp4jThreadFactory}.
 *
 * @author Carl Harris
 */
public class WorkerTaskTest {

  private Snmp4jThreadFactory factory;

  @Before
  public void setUp() throws Exception {
    factory = new Snmp4jThreadFactory(Executors.defaultThreadFactory());
  }

  @Test
  public void testSingleTask() throws Exception {
    final MockWorkerTask delegate = new MockWorkerTask();
    final WorkerTask task = factory.createWorkerThread("some name",
      delegate, false);
    task.run();
    assertThat(delegate.awaitReady(1000), is(true));
  }

  @Test
  public void testMultipleTasks() throws Exception {
    final int taskCount = 50;
    final MockWorkerTask[] delegates = new MockWorkerTask[taskCount];
    for (int i = 0; i < taskCount; i++) {
      delegates[i] = new MockWorkerTask();
      final WorkerTask task = factory.createWorkerThread("task " + i,
          delegates[i], false);
      task.run();
    }

    final long start = System.currentTimeMillis();
    boolean done = false;
    while (!done && System.currentTimeMillis() - start < taskCount * 100) {
      done = true;
      for (int i = 0; i < taskCount; i++) {
        done = done && delegates[i].awaitReady(10);
      }
    }
    assertThat(done, is(true));
  }

  private static class MockWorkerTask implements WorkerTask {

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private boolean ready;

    @Override
    public void terminate() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void join() throws InterruptedException {
      throw new UnsupportedOperationException();
    }

    @Override
    public void interrupt() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void run() {
      lock.lock();
      try {
        ready = true;
        condition.signalAll();
      }
      finally {
        lock.unlock();
      }
    }

    public boolean awaitReady(long timeout) throws InterruptedException {
      lock.lock();
      try {
        long start = System.currentTimeMillis();
        while (!ready && System.currentTimeMillis() - start < timeout) {
          condition.await(10, TimeUnit.MILLISECONDS);
        }
        return ready;
      }
      finally {
        lock.unlock();
      }
    }

  }

}
