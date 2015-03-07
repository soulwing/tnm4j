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
 * An {@link SNMPContext} for SNMPv3.
 * <p>
 * An SNMPv3 context supports strong authentication and cryptographic privacy.
 * If the {@code authType} property is not set (or is reset to {@code null}),
 * the underlying provider will not attempt authentication.  If the {@code
 * privType} property is not set (or is reset to {@code null}), the underlying
 * provider will not attempt to ensure privacy of communications.
 * 
 * @author Carl Harris
 */
public interface SNMPv3Context extends SNMPContext {

  /**
   * An enumeration of SNMPv3 authentication types.
   */
  enum AuthType {
    SHA, MD5
  }

  /**
   * An enumeration of SNMPv3 privacy types.
   */
  enum PrivType {
    DES, DES3, AES128, AES192, AES256
  }

  /**
   * Gets the security name (typically a username).
   * @return security name or {@code null} if none has been set
   */
  String getSecurityName();

  /**
   * Sets the security name.
   * @param name the security name to set
   */
  void setSecurityName(String name);

  /**
   * Gets the authentication type.
   * @return authentication type or {@code null} if none has been set
   */
  AuthType getAuthType();

  /**
   * Sets the authentication type.
   * @param type the authentication type to set
   */
  void setAuthType(AuthType type);

  /**
   * Gets the authentication passphrase.
   * @return passphrase or {@code null} if none has been set
   */
  String getAuthPassphrase();

  /**
   * Sets the authentication passphrase.
   * <p>
   * RFC 3414 section 11.2 requires this passphrase (if non-null) to have
   * a minimum length of eight characters.  The underlying provider may throw
   * an {@code IllegalArgumentException} if the given passphrase is less than 
   * this minimum length.
   *  
   * @param passphrase the passphrase to set
   * @throws IllegalArgumentException if {@code passphrase} is unacceptable to
   *    the underlying provider 
   */
  void setAuthPassphrase(String passphrase);

  /**
   * Gets the privacy type.
   * @return privacy type or {@code null} if none has been set.
   */
  PrivType getPrivType();

  /**
   * Sets the privacy type.
   * @param type the privacy type to set
   */
  void setPrivType(PrivType type);

  /**
   * Gets the privacy passphrase.
   * @return passphrase or {@code null} if none has been set
   */
  String getPrivPassphrase();

  /**
   * Sets the privacy passphrase.
   * <p>
   * RFC 3414 section 11.2 requires this passphrase (if non-null) to have
   * a minimum length of eight characters.  The underlying provider may throw
   * an {@code IllegalArgumentException} if the given passphrase is less than 
   * this minimum length.
   *  
   * @param passphrase the passphrase to set
   * @throws IllegalArgumentException if {@code passphrase} is unacceptable to
   *    the underlying provider 
   */
  void setPrivPassphrase(String passphrase);

  /**
   * Gets the configured scope.
   * @return scope or {@code null} if none has been configured
   */
  String getScope();
  
  /**
   * Sets the scope.
   * @param scope the scope to set
   */
  void setScope(String scope);

}
