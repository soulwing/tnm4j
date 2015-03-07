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

/**
 * A formatter for an SNMP object value.
 * <p>
 * An implementation has access to an underlying MIB and applies the rules
 * of SMIv1 and/or SMIv2 as appropriate to produce a textual representation
 * of a given object value.
 *
 * @author Carl Harris
 */
public interface Formatter {

  /**
   * Formats an SNMP object value as text.
   * <p>
   * The object value passed to this method <em>must</em> be one of the
   * following Java types:
   * <ul>
   *   <li>{@code Long} &mdash; for any integral object type</li>
   *   <li>{@code int[]} &mdash; for any object identifier</li>
   *   <li>{@code byte[]} &mdash; for any octet string or SMIv1 IpAddress</li>
   * </li>
   * 
   * @param value the value to format
   * @return textual representation of value
   */
  String format(Object value);
  
}
