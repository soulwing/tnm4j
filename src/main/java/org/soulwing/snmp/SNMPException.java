/*
 * File created on Sep 12, 2013 
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
 * Base class for any SNMP exceptions.
 *
 * @author Carl Harris
 */
public class SNMPException extends RuntimeException {

  private static final long serialVersionUID = -713788800474374648L;

  /**
   * Constructs a new instance.
   */
  public SNMPException() {
    super();
  }

  /**
   * Constructs a new instance.
   * @param message
   * @param cause
   */
  public SNMPException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new instance.
   * @param message
   */
  public SNMPException(String message) {
    super(message);
  }

  /**
   * Constructs a new instance.
   * @param cause
   */
  public SNMPException(Throwable cause) {
    super(cause);
  }

}
