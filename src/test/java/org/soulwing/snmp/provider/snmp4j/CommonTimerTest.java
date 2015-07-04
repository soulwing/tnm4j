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
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Before;
import org.junit.Test;
import org.snmp4j.util.CommonTimer;

/**
 * Integration tests for the {@link CommonTimer} objects produced by
 * {@link ScheduledExecutorServiceTimerFactory}.
 *
 * @author Carl Harris
 */
public class CommonTimerTest {

  private static final int THREAD_COUNT = 4;

  private static final long ONE_SHOT_DELAY = 500;

  private static final long REPEATING_DELAY = 100;

  private static final long REPEAT_COUNT = 4;

  private static final long TIMING_FUDGE = 10;

  private ScheduledExecutorServiceTimerFactory factory;

  @Before
  public void setUp() throws Exception {
    factory = new ScheduledExecutorServiceTimerFactory(
        Executors.newScheduledThreadPool(THREAD_COUNT));
  }

  @Test
  public void testOneShotTimer() throws Exception {
    final MockTimerTask task = new MockTimerTask();
    final CommonTimer timer = factory.createTimer();
    final long start = System.currentTimeMillis();
    timer.schedule(task, ONE_SHOT_DELAY);
    assertThat(task.awaitReady(2 * ONE_SHOT_DELAY), is(true));
    assertThat(System.currentTimeMillis() - start,
        is(greaterThanOrEqualTo(ONE_SHOT_DELAY)));
  }

  @Test
  public void testMultipleOneShotTimers() throws Exception {
    final int timerCount = 1000;
    final MockTimerTask[] tasks = new MockTimerTask[timerCount];
    final long start = System.currentTimeMillis();
    for (int i = 0; i < timerCount; i++) {
      final CommonTimer timer = factory.createTimer();
      tasks[i] = new MockTimerTask();
      timer.schedule(tasks[i], ONE_SHOT_DELAY);
    }
    for (int i = 0; i < timerCount; i++) {
      assertThat(tasks[i].awaitReady(2 * ONE_SHOT_DELAY), is(true));
      assertThat(System.currentTimeMillis() - start,
          is(greaterThanOrEqualTo(ONE_SHOT_DELAY)));
    }
  }

  @Test
  public void testFixedDelayTimer() throws Exception {
    final MockTimerTask task = new MockTimerTask();
    final CommonTimer timer = factory.createTimer();
    timer.schedule(task, REPEATING_DELAY, REPEATING_DELAY);

    for (int i = 0; i < REPEAT_COUNT; i++) {
      final long start = System.currentTimeMillis();
      assertThat(task.awaitReady(2 * REPEATING_DELAY), is(true));
      assertThat(System.currentTimeMillis() - start,
          is(greaterThanOrEqualTo(REPEATING_DELAY - TIMING_FUDGE)));
    }

    timer.cancel();
    for (int i = 0; i < REPEAT_COUNT; i++) {
      if (!task.awaitReady(REPEATING_DELAY)) {
        assertTrue(true);
        return;
      }
    }
    assertFalse(false);

  }

  private static class MockTimerTask extends TimerTask {

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private boolean ready;

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
        final boolean state = ready;
        ready = false;
        return state;
      }
      finally {
        lock.unlock();
      }
    }

  }

}
