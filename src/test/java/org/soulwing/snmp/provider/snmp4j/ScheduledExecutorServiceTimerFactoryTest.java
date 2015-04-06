/*
 * File created on Apr 3, 2015
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

import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.snmp4j.util.CommonTimer;

/**
 * Unit tests for {@link ScheduledExecutorServiceTimerFactory}.
 *
 * @author Carl Harris
 */
public class ScheduledExecutorServiceTimerFactoryTest {

  private static final long INITIAL_DELAY = 1L;
  private static final long DELAY = 2L;
  private static final Date FIRST_TIME = new Date();

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery() {
    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  @Mock
  private TimerTask task;

  @Mock
  private ScheduledFuture<?> future;

  @Mock
  private ScheduledExecutorService executorService;

  private ScheduledExecutorServiceTimerFactory factory;

  @Before
  public void setUp() throws Exception {
    factory = new ScheduledExecutorServiceTimerFactory(executorService);
  }

  @Test
  public void testCreateAndScheduleOneShotTimer() throws Exception {
    context.checking(new Expectations() { {
      oneOf(executorService).schedule(task, DELAY, TimeUnit.MILLISECONDS);
      will(returnValue(future));
      oneOf(future).cancel(false);
    } });

    CommonTimer timer = factory.createTimer();
    timer.schedule(task, DELAY);
    timer.cancel();
  }

  @Test
  public void testCreateAndScheduleRecurringTimer() throws Exception {
    context.checking(new Expectations() { {
      oneOf(executorService).scheduleWithFixedDelay(task, INITIAL_DELAY, DELAY,
          TimeUnit.MILLISECONDS);
      will(returnValue(future));
      oneOf(future).cancel(false);
    } });

    CommonTimer timer = factory.createTimer();
    timer.schedule(task, INITIAL_DELAY, DELAY);
    timer.cancel();
  }

  @Test
  public void testCreateAndScheduleRecurringTimerWithDate() throws Exception {
    context.checking(new Expectations() { {
      oneOf(executorService).scheduleWithFixedDelay(
          with(task),
          with(any(Long.class)),
          with(DELAY),
          with(TimeUnit.MILLISECONDS));
      will(returnValue(future));
      oneOf(future).cancel(false);
    } });

    CommonTimer timer = factory.createTimer();
    timer.schedule(task, FIRST_TIME, DELAY);
    timer.cancel();
  }

}
