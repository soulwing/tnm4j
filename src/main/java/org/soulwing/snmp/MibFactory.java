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

import org.soulwing.snmp.provider.MibProvider;

/**
 * A factory that produces {@link Mib} objects.
 *
 * @author Carl Harris
 */
public class MibFactory {

  private volatile static MibFactory instance;
  
  private final ServiceLoader<MibProvider> loader = ServiceLoader.load(MibProvider.class);
  
  /**
   * Constructs a new instance.
   */
  private MibFactory() {
  }

  /**
   * Gets the singleton MIB factory instance.
   * @return MIB factory
   */
  public static MibFactory getInstance() {
    if (instance == null) {
      synchronized (MibFactory.class) {
        if (instance == null) {
          instance = new MibFactory();
        }
      }
    }
    return instance;
  }
  
  /**
   * Creates a new MIB using the first available provider.
   * @return new MIB instance
   */
  public Mib newMib() {
    return newMib(null);
  }
  
  /**
   * Creates a new MIB using the named provider.
   * @param providerName name of the provider to use
   * @return new MIB instance
   * @throws ProviderNotFoundException if the named provider is not found
   *    by the {@link ServiceLoader}
   */
  public Mib newMib(String providerName) {
    return findProvider(providerName).newMib();
  }
  
  private MibProvider findProvider(String providerName) {
    Iterator<MibProvider> providers = loader.iterator();   
    while (providers.hasNext()) {
      MibProvider provider = providers.next();
      if (providerName == null 
          || provider.getName().equalsIgnoreCase(providerName)) {
        return provider;
      }
    }
    throw new ProviderNotFoundException(providerName);
  }

}
