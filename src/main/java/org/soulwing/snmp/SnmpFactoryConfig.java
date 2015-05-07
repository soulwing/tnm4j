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

import java.util.HashMap;
import java.util.Map;

/**
 * A configuration object for {@link SnmpFactory}.
 *
 * @author Carl Harris
 */
public class SnmpFactoryConfig {

  /**
   * Default size of the worker pool.
   */
  public static final int DEFAULT_WORKER_POOL_SIZE = 4;

  /**
   * Default size of the scheduled worker pool.
   */
  public static final int DEFAULT_SCHEDULED_WORKER_POOL_SIZE = 1;

  private final Map<String, Object> properties = new HashMap<String, Object>();

  private int workerPoolSize = DEFAULT_WORKER_POOL_SIZE;

  private int scheduledWorkerPoolSize = DEFAULT_SCHEDULED_WORKER_POOL_SIZE;

  /**
   * Gets the minimum number of threads to keep in the pool for short-lived 
   * tasks.
   * <p> 
   * The specified number of threads will remain available in the pool, even
   * if idle.  Tasks will queue when all threads are busy. 
   *
   * @return number of threads
   */
  public int getWorkerPoolSize() {
    return workerPoolSize;
  }

  /**
   * Sets the minimum number of threads to keep in the pool for short-lived 
   * tasks.
   * <p> 
   * The specified number of threads will remain available in the pool, even
   * if idle.  Tasks will queue when all threads are busy. 
   * @param workerPoolSize number of threads
   */
  public void setWorkerPoolSize(int workerPoolSize) {
    this.workerPoolSize = workerPoolSize;
  }

  /**
   * Gets the number of threads to keep in the pool for scheduled tasks.
   * <p>
   * The specified number of threads will remain available in the pool, even
   * if idle.  Tasks that are ready to run (based on configured schedule)
   * will queue when all threads are busy.
   * @return number of threads
   */
  public int getScheduledWorkerPoolSize() {
    return scheduledWorkerPoolSize;
  }

  /**
   * Sets the number of threads to keep in the pool for scheduled tasks.
   * <p>
   * The specified number of threads will remain available in the pool, even
   * if idle.  Tasks that are ready to run (based on configured schedule)
   * will queue when all threads are busy.
   * 
   * @param scheduledWorkerPoolSize number of threads
   */
  public void setScheduledWorkerPoolSize(int scheduledWorkerPoolSize) {
    this.scheduledWorkerPoolSize = scheduledWorkerPoolSize;
  }

  /**
   * Gets the value of a provider-specific property.
   * @param name name of the value to retrieve
   * @return property value associated with {@code name} or {@code null} if
   *    the named property does not exist
   */
  public Object getProperty(String name) {
    return properties.get(name);
  }

  /**
   * Sets the value of a provider-specific property.
   * @param name name of the property to retrieve
   * @param value the value to set
   */
  public void setProperty(String name, Object value) {
    properties.put(name, value);
  }

}

