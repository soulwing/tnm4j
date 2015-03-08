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
import java.util.ServiceLoader;

import org.soulwing.snmp.provider.SnmpProvider;

/**
 * A factory that produces {@link SnmpContext} objects.
 *
 * @author Carl Harris
 */
public class SnmpFactory {

  private final ServiceLoader<SnmpProvider> loader = 
      ServiceLoader.load(SnmpProvider.class);
  
  private SimpleSnmpConfiguration defaultConfiguration = 
      new SimpleSnmpConfiguration();
  
  private static volatile SnmpFactory instance;
  
  private SnmpFactory() {    
  }
  
  /**
   * Gets the singleton factory instance.
   * @return factory object
   */
  public static SnmpFactory getInstance() {
    if (instance == null) {
      synchronized (SnmpFactory.class) {
        if (instance == null) {
          instance= new SnmpFactory();
        }
      }
    }
    return instance;
  }
  
  /**
   * Gets the default configuration that will be used for new context
   * instances created by this factory.
   * @return configuration
   */
  public SimpleSnmpConfiguration getDefaultConfiguration() {
    return defaultConfiguration;
  }

  /**
   * Sets the default configuration that will be used for new context
   * instances created by this factory.
   * @param defaultConfiguration the value to set
   */
  public void setDefaultConfiguration(SimpleSnmpConfiguration config) {
    this.defaultConfiguration = config;
  }

  /**
   * Gets a new SNMPv2c context using the first available provider, default
   * MIB, and the factory's default configuration. 
   * @param target target agent
   * @return SNMP context object
   */
  public SnmpContext newContext(SnmpTarget target) {
    return newContext(target, MibFactory.getInstance().newMIB(), 
        defaultConfiguration, null);
  }

  /**
   * Gets a new SNMPv2c context using the first available provider and the
   * factory's default configuration.
   * @param target target agent
   * @param mib MIB that will be passed into the context
   * @return SNMP context object
   */
  public SnmpContext newContext(SnmpTarget target, Mib mib) {
    return newContext(target, mib, defaultConfiguration, null);
  }

  /**
   * Gets a new SNMPv2c context using the first available provider.
   * @param target target agent
   * @param config SNMP configuration for the context
   * @param mib MIB that will be passed into the context
   * @return SNMP context object
   */
  public SnmpContext newContext(SnmpTarget target, 
      SnmpConfiguration config) {
    return newContext(target, MibFactory.getInstance().newMIB(), config, null);
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
      SnmpConfiguration config, String providerName) {
    return findProvider(providerName).newContext(target, config.clone(), mib);
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

}
