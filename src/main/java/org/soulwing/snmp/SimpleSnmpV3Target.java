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
 * A {@link SnmpV3Target} implemented as a simple configurable bean.
 *
 * @author Carl Harris
 */
public class SimpleSnmpV3Target extends SnmpTargetBase
    implements SnmpV3Target {

  private String securityName;
  private AuthType authType;
  private String authPassphrase;
  private PrivType privType;
  private String privPassphrase;
  private String scope;
  
  /** 
   * {@inheritDoc}
   */
  @Override
  public String getSecurityName() {
    return securityName;
  }

  /**
   * Sets the {@code securityName} property.
   * @param securityName the value to set
   */
  public void setSecurityName(String securityName) {
    this.securityName = securityName;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public AuthType getAuthType() {
    return authType;
  }

  /**
   * Sets the {@code authType} property.
   * @param authType the value to set
   */
  public void setAuthType(AuthType authType) {
    this.authType = authType;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public String getAuthPassphrase() {
    return authPassphrase;
  }

  /**
   * Sets the {@code authPassphrase} property.
   * @param authPassphrase the value to set
   */
  public void setAuthPassphrase(String authPassphrase) {
    this.authPassphrase = authPassphrase;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public PrivType getPrivType() {
    return privType;
  }

  /**
   * Sets the {@code privType} property.
   * @param privType the value to set
   */
  public void setPrivType(PrivType privType) {
    this.privType = privType;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public String getPrivPassphrase() {
    return privPassphrase;
  }

  /**
   * Sets the {@code privPassphrase} property.
   * @param privPassphrase the value to set
   */
  public void setPrivPassphrase(String privPassphrase) {
    this.privPassphrase = privPassphrase;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public String getScope() {
    return scope;
  }

  /**
   * Sets the {@code scope} property.
   * @param scope the value to set
   */
  public void setScope(String scope) {
    this.scope = scope;
  }

}
