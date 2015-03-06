/*
 * File created on Nov 30, 2012 
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
