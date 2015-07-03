/*
 * File created on Apr 10, 2015
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

import org.soulwing.snmp.Formatter;
import org.soulwing.snmp.ObjectValue;

/**
 * An immutable {@link ObjectValue}.
 *
 * @author Carl Harris
 */
class ImmutableObjectValue implements ObjectValue {

  private final int syntax;
  private final Number value;
  private final Formatter formatter;

  ImmutableObjectValue(int syntax, Number value, Formatter formatter) {
    this.syntax = syntax;
    this.value = value;
    this.formatter = formatter;
  }

  @Override
  public int getSyntax() {
    return syntax;
  }

  @Override
  public int asInt() {
    return value.intValue();
  }

  @Override
  public long asLong() {
    return value.longValue();
  }

  @Override
  public Object toObject() {
    return value;
  }

  @Override
  public String asString() {
    return formatter.format(value);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof ImmutableObjectValue)) return false;
    return this.toObject().equals(((ImmutableObjectValue) obj).toObject());
  }

  @Override
  public int hashCode() {
    return this.toObject().hashCode();
  }

  @Override
  public String toString() {
    return asString();
  }

  public Object asType(Class<?> type) {
    if (Integer.class.equals(type)) {
      return asInt();
    }
    else if (Long.class.equals(type)) {
      return asLong();
    }
    else if (String.class.equals(type)) {
      return asString();
    }
    else {
      throw new ClassCastException("unsupported type " + type);
    }
  }

  @Override
  public void set(Object value) {
  }

}
