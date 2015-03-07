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
