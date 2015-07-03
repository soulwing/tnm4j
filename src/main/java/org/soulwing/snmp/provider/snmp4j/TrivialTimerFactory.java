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

import static org.soulwing.snmp.provider.snmp4j.Snmp4jLogger.logger;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.snmp4j.util.CommonTimer;
import org.snmp4j.util.TimerFactory;

/**
 * A {@link TimerFactory} that produces {@link CommonTimer} objects
 * that delegate directly to a {@link java.util.Timer}.
 *
 * @author Carl Harris
 */
class TrivialTimerFactory implements TimerFactory {

  @Override
  public CommonTimer createTimer() {
    return new TrivialCommonTimer();
  }

  class TrivialCommonTimer implements CommonTimer {

    private Timer timer;
    private TimerTaskWrapper wrapper;

    @Override
    public void schedule(TimerTask task, long delay) {
      if (timer != null) {
        cancel();
      }
      timer = new Timer();
      wrapper = new TimerTaskWrapper(task);
      timer.schedule(wrapper, delay);
      logger.debug("scheduled task {} with delay {}", task, delay);
    }

    @Override
    public void schedule(TimerTask task, Date firstTime, long period) {
      if (timer != null) {
        cancel();
      }
      timer = new Timer();
      wrapper = new TimerTaskWrapper(task);
      timer.schedule(wrapper, firstTime, period);
      logger.debug("scheduled task {} with firstTime {} and period {}", task,
          firstTime, period);
    }

    @Override
    public void schedule(TimerTask task, long delay, long period) {
      if (timer != null) {
        cancel();
      }
      timer = new Timer();
      wrapper = new TimerTaskWrapper(task);
      timer.schedule(wrapper, delay, period);
      logger.debug("scheduled task {} with delay {} and period {}", task, delay,
          period);
    }

    @Override
    public void cancel() {
      if (timer == null) return;
      timer.cancel();
      logger.debug("canceled timer for task {}", wrapper);
    }

  }

  private static class TimerTaskWrapper extends TimerTask {

    private final TimerTask delegate;

    public TimerTaskWrapper(TimerTask delegate) {
      this.delegate = delegate;
    }

    @Override
    public void run() {
      Snmp4jLogger.logger.debug("running timer task {}", delegate);
      delegate.run();
    }

    @Override
    public boolean cancel() {
      Snmp4jLogger.logger.debug("canceled timer task {}", delegate);
      return delegate.cancel();
    }

    @Override
    public long scheduledExecutionTime() {
      Snmp4jLogger.logger.debug("requested execution time for task {}", delegate);
      return delegate.scheduledExecutionTime();
    }

    @Override
    public String toString() {
      return delegate.toString();
    }

  }

}
