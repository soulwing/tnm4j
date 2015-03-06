/*
 * File created on Sep 23, 2013 
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

/**
 * An exception thrown when a walk encounters a GETBULK response that is
 * unexpectedly truncated.
 *
 * @author Carl Harris
 */
public class TruncatedResponseException extends SNMPException {

  private static final long serialVersionUID = -6660192207091746358L;

  /**
   * Constructs a new instance.
   * @param message
   */
  public TruncatedResponseException(String message) {
    super(message);
  }

}
