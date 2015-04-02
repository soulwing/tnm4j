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

import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.snmp4j.util.CommonTimer;
import org.snmp4j.util.TimerFactory;

/**
 * An SNMP4j {@link TimerFactory} layered over a Java SE
 * {@link ScheduledExecutorService}.
 *
 * @author Carl Harris
 */
class ScheduledExecutorServiceTimerFactory implements TimerFactory {

  private final ScheduledExecutorService executorService;
  
  /**
   * Constructs a new instance.
   * @param scheduledExecutorService scheduled executor service that will be
   *   used to schedule timers
   */
  public ScheduledExecutorServiceTimerFactory(
      ScheduledExecutorService scheduledExecutorService) {
    this.executorService = scheduledExecutorService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CommonTimer createTimer() {
    return new ScheduledCommonTimer();
  }

  class ScheduledCommonTimer implements CommonTimer {

    private ScheduledFuture<?> future;

    @Override
    public void schedule(TimerTask task, long delay) {
      if (future != null) throw new IllegalStateException();
      future = executorService.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void schedule(TimerTask task, Date firstTime, long period) {
      long now = System.currentTimeMillis();
      long then = firstTime.getTime();
      schedule(task, then - now, period);
    }

    @Override
    public void schedule(TimerTask task, long delay, long period) {
      if (future != null) throw new IllegalStateException();
      future = executorService.scheduleAtFixedRate(task, delay, period,
          TimeUnit.MILLISECONDS);
    }

    @Override
    public void cancel() {
      if (future == null) return;
      future.cancel(false);
    }

  }

}
