/*
 * File created on Jan 24, 2013 
 *
 * Copyright (c) 2013 Carl Harris, Jr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.soulwing.snmp.provider.mibble;

import org.soulwing.snmp.Formatter;


class TimeTicksFormatter implements Formatter {

  private final Formatter delegate;
  
  public TimeTicksFormatter(String hint) {
    this.delegate = hint != null ? 
        new IntegerFormatter(hint) : new InnerFormatter();
  }

  @Override
  public String format(Object value) {
    return delegate.format(value);
  }

  private static class InnerFormatter implements Formatter {
    @Override
    public String format(Object value) {
      long ticks = ((Number) value).longValue();
      int hsecs = (int) (ticks % 100);
      ticks = ticks / 100;
      int secs = (int) (ticks % 60);
      ticks = ticks / 60;
      int mins = (int) (ticks % 60);
      ticks = ticks / 60;
      int hours = (int) (ticks % 24);
      int days = (int) (ticks / 24);
      return String.format("%dd %02d:%02d:%02d.%02d",
          days, hours, mins, secs, hsecs);
    }
  }
  
}
