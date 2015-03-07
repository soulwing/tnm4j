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

import org.soulwing.snmp.provider.MIBProvider;

/**
 * A factory that produces {@link MIB} objects.
 *
 * @author Carl Harris
 */
public class MIBFactory {

  private volatile static MIBFactory instance;
  
  private final ServiceLoader<MIBProvider> loader = ServiceLoader.load(MIBProvider.class);
  
  /**
   * Constructs a new instance.
   */
  private MIBFactory() {
  }

  /**
   * Gets the singleton MIB factory instance.
   * @return MIB factory
   */
  public static MIBFactory getInstance() {
    if (instance == null) {
      synchronized (MIBFactory.class) {
        if (instance == null) {
          instance = new MIBFactory();
        }
      }
    }
    return instance;
  }
  
  /**
   * Creates a new MIB using the first available provider.
   * @return new MIB instance
   */
  public MIB newMIB() {
    return newMIB(null);
  }
  
  /**
   * Creates a new MIB using the named provider.
   * @param providerName name of the provider to use
   * @return new MIB instance
   * @throws ProviderNotFoundException if the named provider is not found
   *    by the {@link ServiceLoader}
   */
  public MIB newMIB(String providerName) {
    return findProvider(providerName).newMIB();
  }
  
  private MIBProvider findProvider(String providerName) {
    Iterator<MIBProvider> providers = loader.iterator();   
    while (providers.hasNext()) {
      MIBProvider provider = providers.next();
      if (providerName == null 
          || provider.getName().equalsIgnoreCase(providerName)) {
        return provider;
      }
    }
    throw new ProviderNotFoundException(providerName);
  }

}
