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
 * An {@link SnmpTargetConfig} implemented as a simple configurable 
 * bean.
 *
 * @author Carl Harris
 */
public class SimpleSnmpTargetConfig implements SnmpTargetConfig {

  private int retries = SnmpDefaults.RETRIES;
  private long timeout = SnmpDefaults.TIMEOUT;
  private int walkMaxRepetitions = SnmpDefaults.WALK_MAX_REPETITIONS;
  
  private boolean walkAllowsTruncatedRepetition;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public int getRetries() {
    return retries;
  }

  /**
   * Sets the {@code retries} property.
   * @param retries the value to set
   */
  public void setRetries(int retries) {
    this.retries = retries;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getTimeout() {
    return timeout;
  }

  /**
   * Sets the {@code timeout} property.
   * @param timeout the value to set
   */
  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getWalkMaxRepetitions() {
    return walkMaxRepetitions;
  }

  /**
   * Sets the {@code walkMaxRepetitions} property.
   * @param walkMaxRepetitions the value to set
   */
  public void setWalkMaxRepetitions(int walkMaxRepetitions) {
    this.walkMaxRepetitions = walkMaxRepetitions;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isWalkAllowsTruncatedRepetition() {
    return walkAllowsTruncatedRepetition;
  }

  /**
   * Sets the {@code walkAllowsTruncatedRepetition} property.
   * @param walkAllowsTruncatedRepetition the value to set
   */
  public void setWalkAllowsTruncatedRepetition(
      boolean walkAllowsTruncatedRepetition) {
    this.walkAllowsTruncatedRepetition = walkAllowsTruncatedRepetition;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpTargetConfig clone() {
    try {
      return (SnmpTargetConfig) super.clone();
    }
    catch (CloneNotSupportedException ex) {
      throw new RuntimeException(ex);
    }
  }

}
