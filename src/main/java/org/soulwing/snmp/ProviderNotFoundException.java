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

/**
 * An exception thrown when a named provider is not found by the 
 * {@link ServiceLoader}.
 *
 * @author Carl Harris
 */
public class ProviderNotFoundException extends MIBException {

  private static final long serialVersionUID = -6910583914778740034L;

  private final String providerName;
  
  /**
   * Constructs a new instance.
   * @param providerName
   */
  public ProviderNotFoundException(String providerName) {
    super("provider not found: " + providerName);
    this.providerName = providerName;
  }

  /**
   * Gets the name of the provider that was not found.
   */
  public String getProviderName() {
    return providerName;
  }

}
