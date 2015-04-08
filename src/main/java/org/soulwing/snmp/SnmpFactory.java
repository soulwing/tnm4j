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

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.soulwing.snmp.provider.SnmpProvider;

/**
 * A factory that produces {@link SnmpContext} objects.
 *
 * @author Carl Harris
 */
public class SnmpFactory {

  private static volatile SnmpFactory instance;

  private final Lock lock = new ReentrantLock();

  private final ServiceLoader<SnmpProvider> loader = 
      ServiceLoader.load(SnmpProvider.class);
  
  private final AtomicBoolean closed = new AtomicBoolean();

  private final ConcurrentMap<String, SnmpProvider> providerMap =
      new ConcurrentHashMap<>();

  private final ExecutorService executorService;
  private final ScheduledExecutorService scheduledExecutorService;
  private final ThreadFactory threadFactory;

  private SimpleSnmpTargetConfig defaultTargetConfig = 
      new SimpleSnmpTargetConfig();
  

  private SnmpFactory(ExecutorService executorService,
      ScheduledExecutorService scheduledExecutorService,
      ThreadFactory threadFactory) {
    this.executorService = executorService;
    this.scheduledExecutorService = scheduledExecutorService;
    this.threadFactory = threadFactory;
  }
  
  /**
   * Gets the singleton factory instance.
   * @return factory object
   */
  public static SnmpFactory getInstance() {
    return getInstance(
        new SnmpFactoryConfig(), new TrivialThreadFactory());
  }

  /**
   * Gets the singleton factory instance.
   * @param threadFactory thread factory that will be used to create threads
   *    as needed for components produced by this factory
   * @return factory object
   */
  public static SnmpFactory getInstance(ThreadFactory threadFactory) {
    return getInstance(
        new SnmpFactoryConfig(), new TrivialThreadFactory());
  }
  
  /**
   * Gets the singleton factory instance.
   * @param config factory configuration
   * @param threadFactory thread factory that will be used to create threads
   *    as needed for components produced by this factory
   * @return factory object
   */
  public static SnmpFactory getInstance(SnmpFactoryConfig config, 
      ThreadFactory threadFactory) {
    if (instance == null) {
      synchronized (SnmpFactory.class) {
        if (instance == null) {
          ExecutorService executorService = new ThreadPoolExecutor(
              config.getWorkerPoolSize(), config.getWorkerPoolSize(), 0L,
              TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
              threadFactory);
          ScheduledExecutorService scheduledExecutorService =
              new ScheduledThreadPoolExecutor(
                  config.getScheduledWorkerPoolSize(), threadFactory);
          instance = new SnmpFactory(executorService, scheduledExecutorService,
              threadFactory);
        }
      }
    }
    return instance;
  }

  private static class TrivialThreadFactory implements ThreadFactory {
    public Thread newThread(Runnable r) {
      return new Thread(r);
    }
  }
  
  /**
   * Gets the executor service that should be used for short-lived tasks.
   * @return executor service
   */
  public ExecutorService getExecutorService() {
    assertNotClosed();
    return executorService;
  }

  /**
   * Gets the executor service that should be used for scheduled tasks.
   * @return executor service
   */
  public ScheduledExecutorService getScheduledExecutorService() {
    assertNotClosed();
    return scheduledExecutorService;
  }
  
  /**
   * Gets the thread factory that should be used to create threads for long
   * running tasks.
   * @return thread factory
   */
  public ThreadFactory getThreadFactory() {
    assertNotClosed();
    return threadFactory;
  }

  /**
   * Closes this factory, releasing any resources it might be holding.
   * @throws InterruptedException
   */
  public void close() throws InterruptedException {
    if (!closed.compareAndSet(false, true)) return;
    shutDownProviders();
    shutDownExecutor(executorService);
    shutDownExecutor(scheduledExecutorService);
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
  }

  private void shutDownProviders() {
    for (SnmpProvider provider : providerMap.values()) {
      provider.close();
    }
    providerMap.clear();
  }

  private void shutDownExecutor(ExecutorService executorService) {
    try {
      executorService.shutdownNow();
      executorService.awaitTermination(defaultTargetConfig.getTimeout(),
          TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    catch (IllegalStateException ex) {
      assert true;  // it's okay -- probably a ManagedExecutorService
    }
  }

  /**
   * Gets the default configuration that will be used for new context
   * instances created by this factory.
   * @return configuration
   */
  public SimpleSnmpTargetConfig getDefaultTargetConfig() {
    return defaultTargetConfig;
  }

  /**
   * Sets the default configuration that will be used for new context
   * instances created by this factory.
   * @param config the value to set
   */
  public void setDefaultTargetConfig(SimpleSnmpTargetConfig config) {
    this.defaultTargetConfig = config;
  }

  /**
   * Gets a new SNMPv2c context using the first available provider, default
   * MIB, and the factory's default configuration. 
   * @param target target agent
   * @return SNMP context object
   */
  public SnmpContext newContext(SnmpTarget target) {
    return newContext(target, MibFactory.getInstance().newMib(),
        defaultTargetConfig, null);
  }

  /**
   * Gets a new SNMPv2c context using the first available provider and the
   * factory's default configuration.
   * @param target target agent
   * @param mib MIB that will be passed into the context
   * @return SNMP context object
   */
  public SnmpContext newContext(SnmpTarget target, Mib mib) {
    return newContext(target, mib, defaultTargetConfig, null);
  }

  /**
   * Gets a new SNMPv2c context using the first available provider.
   * @param target target agent
   * @param config SNMP configuration for the context
   * @return SNMP context object
   */
  public SnmpContext newContext(SnmpTarget target, 
      SnmpTargetConfig config) {
    return newContext(target, MibFactory.getInstance().newMib(), config, null);
  }

  /**
   * Gets a new SNMPv2c context using the named provider.
   * @param target target agent
   * @param mib MIB that will be passed into the context
   * @param config SNMP configuration for the context
   * @param providerName name of the desired provider
   * @return SNMP context object
   * @throws ProviderNotFoundException if the named provider cannot be
   *    found on the class path
   */
  public SnmpContext newContext(SnmpTarget target, Mib mib,
      SnmpTargetConfig config, String providerName) {
    assertNotClosed();
    return getProvider(providerName).newContext(target, config.clone(), mib);
  }

  /**
   * Gets a named provider instance (which may have been previously cached)
   * @param providerName provider name or {@code null} to find the first
   *    available provider
   * @return provider object
   * @throws ProviderNotFoundException if the named provider cannot be
   *    found by the {@link ServiceLoader} (or if no provider can be found
   *    when the specified name is {@code null})
   */
  private SnmpProvider getProvider(String providerName) {
    SnmpProvider provider = null;
    if (!providerMap.isEmpty()) {
      provider = providerName != null ?
          providerMap.get(providerName) : providerMap.values().iterator().next();
    }
    if (provider == null) {
      lock.lock();
      try {
        provider = findProvider(providerName);
        providerMap.put(provider.getName(), provider);
      }
      finally {
        lock.unlock();
      }
    }
    return provider;
  }

  /**
   * Finds a named provider
   * @param providerName provider name or {@code null} to find the first
   *    available provider
   * @return provider object
   * @throws ProviderNotFoundException if the named provider cannot be
   *    found by the {@link ServiceLoader} (or if no provider can be found, 
   *    when the specified name is {@code null}
   */
  private SnmpProvider findProvider(String providerName) {
    Iterator<SnmpProvider> i = loader.iterator();
    while (i.hasNext()) {
      SnmpProvider provider = i.next();
      if (providerName == null 
          || provider.getName().equalsIgnoreCase(providerName)) {
        return provider;
      }
    }
    throw new ProviderNotFoundException(providerName);
  }

  private void assertNotClosed() {
    if (closed.get()) {
      throw new IllegalStateException("factory has been closed");
    }
  }

}
