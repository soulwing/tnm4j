/*
 * File created on Nov 30, 2012 
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
 * A descriptor for a conceptual table index object.
 *
 * @author Carl Harris
 */
public interface IndexDescriptor {

  /**
   * Gets the object identifier of the index value described by the receiver.
   * <p>
   * This method returns only that portion of the indexed object's OID that 
   * corresponds to the index value.
   * @return object identifier substring representing the index value
   */
  String getOid();
  
  /**
   * Gets the SMI syntax indicator for the index object described by the 
   * receiver.
   * @return syntax indicator
   */
  int getSyntax();
  
  /**
   * Gets the encoded form of the index value described by the receiver.
   * <p>
   * This method returns only that portion of the indexed object's OID that
   * corresponds to the index value. 
   * 
   * @return an integer array containing the OID-encoded index value
   */
  int[] getEncoded();
  
  /**
   * Gets the state of a flag indicating whether the length of the index
   *    value described by the receiver is implied (i.e. not included in
   *    the object identifier).
   * @return flag state
   */
  boolean isImplied();
  
}
