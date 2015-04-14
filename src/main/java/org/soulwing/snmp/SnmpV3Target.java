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
 * An SNMPv3 {@link SnmpTarget}.
 * <p>
 * An SNMPv3 target supports strong authentication and cryptographic privacy.
 * If the {@link #getAuthType() authType} property is not set, the underlying 
 * provider will not attempt authentication.  If the 
 * {@link #getPrivType() privType} property is not set, the underlying
 * provider will not attempt to ensure privacy of communications.
 * 
 * @author Carl Harris
 */
public interface SnmpV3Target extends SnmpTarget {

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
   * Gets the authentication type.
   * @return authentication type or {@code null} if none has been set
   */
  AuthType getAuthType();

  /**
   * Gets the authentication passphrase.
   * @return passphrase or {@code null} if none has been set
   */
  String getAuthPassphrase();

  /**
   * Gets the privacy type.
   * @return privacy type or {@code null} if none has been set.
   */
  PrivType getPrivType();

  /**
   * Gets the privacy passphrase.
   * @return passphrase or {@code null} if none has been set
   */
  String getPrivPassphrase();

  /**
   * Gets the configured scope.
   * @return scope or {@code null} if none has been configured
   */
  String getScope();

}
