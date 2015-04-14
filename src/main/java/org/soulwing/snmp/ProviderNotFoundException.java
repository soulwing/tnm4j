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
 * An exception thrown when a named provider is not found by the 
 * {@link java.util.ServiceLoader}.
 *
 * @author Carl Harris
 */
public class ProviderNotFoundException extends MibException {

  private static final long serialVersionUID = -6910583914778740034L;

  private final String providerName;
  
  /**
   * Constructs a new instance.
   * @param providerName name of the subject provider
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
