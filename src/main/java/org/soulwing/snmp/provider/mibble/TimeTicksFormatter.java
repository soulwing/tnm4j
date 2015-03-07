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
