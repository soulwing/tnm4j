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

package org.soulwing.snmp.provider.snmp4j;

import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.SnmpV1Target;

/**
 * A {@link PduFactoryStrategy} for an SNMPv1 target.
 *
 * @author Carl Harris
 */
class SnmpV1PduFactoryStrategy implements PduFactoryStrategy {

  private static final PduFactory factory = new SnmpV1PduFactory();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public PduFactory newPduFactory(SnmpTarget target) {
    if (!(target instanceof SnmpV1Target)) return null;
    return factory;
  }
  
  static class SnmpV1PduFactory implements PduFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public PDU newPDU() {
      return new PDUv1();
    }
    
  }

}
