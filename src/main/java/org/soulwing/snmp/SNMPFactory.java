/*
 * File created on Sep 11, 2013 
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

import java.util.Iterator;
import java.util.ServiceLoader;

import org.soulwing.snmp.provider.SNMPProvider;

/**
 * A factory that produces {@link SNMPContext} objects.
 *
 * @author Carl Harris
 */
public class SNMPFactory {

  private final ServiceLoader<SNMPProvider> loader = 
      ServiceLoader.load(SNMPProvider.class);
  
  private static volatile SNMPFactory instance;
  
  private SNMPFactory() {    
  }
  
  /**
   * Gets the singleton factory instance.
   * @return factory object
   */
  public static SNMPFactory getInstance() {
    if (instance == null) {
      synchronized (SNMPFactory.class) {
        if (instance == null) {
          instance= new SNMPFactory();
        }
      }
    }
    return instance;
  }
  
  /**
   * Gets a new SNMPv2c context using the first available provider and the
   * default MIB.
   * @return SNMP context object
   */
  public SNMPv2cContext newSnmpV2cContext() {
    return newSnmpV2cContext(MIBFactory.getInstance().newMIB(), null);
  }

  /**
   * Gets a new SNMPv2c context using the first available provider.
   * @param mib MIB that will be passed into the context
   * @return SNMP context object
   */
  public SNMPv2cContext newSnmpV2cContext(MIB mib) {
    return newSnmpV2cContext(mib, null);
  }

  /**
   * Gets a new SNMPv2c context using the named provider.
   * @param mib MIB that will be passed into the context
   * @param providerName name of the desired provider
   * @return SNMP context object
   * @throws ProviderNotFoundException if the named provider cannot be
   *    found on the class path
   */
  public SNMPv2cContext newSnmpV2cContext(MIB mib, String providerName) {
    return findProvider(providerName).newSnmpV2cContext(mib);
  }

  /**
   * Gets a new SNMPv3 context using the first available provider and the
   * default MIB.
   * @return SNMP context object
   */
  public SNMPv3Context newSnmpV3Context() {
    return newSnmpV3Context(MIBFactory.getInstance().newMIB(), null);
  }

  /**
   * Gets a new SNMPv3 context using the first available provider.
   * @param mib MIB that will be passed into the context
   * @return SNMP context object
   */
  public SNMPv3Context newSnmpV3Context(MIB mib) {
    return newSnmpV3Context(mib, null);
  }

  /**
   * Gets a new SNMPv3 context using the named provider.
   * @param mib MIB that will be passed into the context
   * @param providerName name of the desired provider
   * @return SNMP context object
   * @throws ProviderNotFoundException if the named provider cannot be
   *    found on the class path
   */
  public SNMPv3Context newSnmpV3Context(MIB mib, String providerName) {
    return findProvider(providerName).newSnmpV3Context(mib);
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
  private SNMPProvider findProvider(String providerName) {
    Iterator<SNMPProvider> i = loader.iterator();
    while (i.hasNext()) {
      SNMPProvider provider = i.next();
      if (providerName == null 
          || provider.getName().equalsIgnoreCase(providerName)) {
        return provider;
      }
    }
    throw new ProviderNotFoundException(providerName);
  }

}
