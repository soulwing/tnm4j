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
package org.soulwing.snmp.provider.snmp4j;

import org.soulwing.snmp.MIB;
import org.soulwing.snmp.SNMPv2cContext;
import org.soulwing.snmp.SNMPv3Context;
import org.soulwing.snmp.provider.SNMPProvider;

/**
 * An {@link SNMPProvider} based on SNMP4j.
 *
 * @author Carl Harris
 */
public class Snmp4jProvider implements SNMPProvider {

  private static final String PROVIDER_NAME = "snmp4j";
  
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
  public SNMPv2cContext newSnmpV2cContext(MIB mib) {
    return new Snmp4jV2cContext(mib);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SNMPv3Context newSnmpV3Context(MIB mib) {
    return new Snmp4jV3Context(mib);
  }

}
