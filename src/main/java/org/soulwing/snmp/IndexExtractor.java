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
 * An object that produces a description of each of the index objects 
 * contained in the identifier for an object that is defined in
 * a conceptual table.
 *
 * @author Carl Harris
 */
public interface IndexExtractor {

  /**
   * Produces a description of each of the index objects contained in 
   * an OID for an object that is defined in a conceptual table.
   * @param oid OID of a conceptual table column value
   * @return
   */
  IndexDescriptor[] extractIndexes(String oid);
  
}
