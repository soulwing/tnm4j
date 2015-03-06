/*
 * File created on Sep 11, 2013 
 *
 * Copyright 2008-2011 Carl Harris, Jr.
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
