/*
 * File created on Sep 11, 2013 
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
package org.soulwing.snmp.provider.mibble;

import org.soulwing.snmp.MIB;
import org.soulwing.snmp.provider.MIBProvider;

/**
 * DESCRIBE THE TYPE HERE.
 *
 * @author Carl Harris
 */
public class MibbleMIBProvider implements MIBProvider {

  private static final String PROVIDER_NAME = "Mibble";
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return PROVIDER_NAME;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MIB newMIB() {
    return new MibbleMIB();
  }

}
