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
 * A base class for runtime exceptions thrown by the MIB façade.
 *
 * @author Carl Harris
 */
public class MibException extends RuntimeException {

  private static final long serialVersionUID = -7654832789689702654L;

  /**
   * Constructs a new instance.
   */
  public MibException() {
    super();
  }

  /**
   * Constructs a new instance.
   * @param message
   * @param cause
   */
  public MibException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new instance.
   * @param message
   */
  public MibException(String message) {
    super(message);
  }

  /**
   * Constructs a new instance.
   * @param cause
   */
  public MibException(Throwable cause) {
    super(cause);
  }
  
}
