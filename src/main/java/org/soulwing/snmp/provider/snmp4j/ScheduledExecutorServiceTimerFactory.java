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
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
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
   * @param scheduledExecutorService
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
    return new DelegatingCommonTimer();
  }

  class DelegatingCommonTimer implements CommonTimer {

    private FutureTask<Object> wrapper;
    
    @Override
    public void cancel() {
      if (wrapper == null) return;
      wrapper.cancel(false);
    }

    @Override
    public void schedule(TimerTask task, long initialDelay, long delay) {
      if (wrapper != null) {
        throw new IllegalStateException("already scheduled");
      }
      wrapper = new FutureTask<Object>(task, null);
      executorService.scheduleWithFixedDelay(wrapper, initialDelay, 
          delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void schedule(TimerTask task, Date firstTime, long delay) {
      long now = System.currentTimeMillis();
      long then = firstTime.getTime();
      long initialDelay = then - now;
      if (initialDelay > 0) {
        initialDelay = 0;
      }
      schedule(task, initialDelay, delay);
    }

    @Override
    public void schedule(TimerTask task, long delay) {
      schedule(task, 0, delay);
    }
    
  }
}
