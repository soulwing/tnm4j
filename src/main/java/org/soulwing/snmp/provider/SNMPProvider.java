/*
 * File created on Sep 11, 2013 
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
package org.soulwing.snmp.provider;

import org.soulwing.snmp.MIB;
import org.soulwing.snmp.SNMPContext;
import org.soulwing.snmp.SNMPv2cContext;
import org.soulwing.snmp.SNMPv3Context;

/**
 * A provider of {@link SNMPContext} objects.
 *
 * @author Carl Harris
 */
public interface SNMPProvider {

  /**
   * Gets the provider name.
   * @return provider name (never {@code null})
   */
  String getName();
  
  /**
   * Creates a new SNMPv2c context.
   * @param mib MIB provider
   * @return SNMP context
   */
  SNMPv2cContext newSnmpV2cContext(MIB mib);

  /**
   * Creates a new SNMPv3 context.
   * @param mib MIB provider
   * @return SNMP context
   */
  SNMPv3Context newSnmpV3Context(MIB mib);

}
