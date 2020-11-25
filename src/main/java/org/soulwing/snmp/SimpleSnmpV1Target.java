/*
 * File created on Nov 25, 2020
 *
 * Copyright (c) 2020 Carl Harris, Jr
 * and others as noted
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.soulwing.snmp;


/**
 * A {@link SnmpV1Target} implemented as a simple configurable bean.
 *
 * @author Carl Harris
 */
public class SimpleSnmpV1Target extends SnmpTargetBase
    implements SnmpV1Target {

  private String community;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getCommunity() {
    return community;
  }

  /**
   * Sets the {@code community} property.
   * @param community the value to set
   */
  public void setCommunity(String community) {
    this.community = community;
  }

}
