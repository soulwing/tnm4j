/*
 * File created on Apr 9, 2015
 *
 * Copyright (c) 2015 Carl Harris, Jr
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

import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.SMIConstants;
import org.soulwing.snmp.MibTrapV1Support;
import org.soulwing.snmp.SimpleSnmpV2cTarget;
import org.soulwing.snmp.SimpleSnmpV3Target;
import org.soulwing.snmp.SnmpListener;
import org.soulwing.snmp.SnmpNotification;
import org.soulwing.snmp.SnmpNotificationEvent;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.SnmpTargetBase;
import org.soulwing.snmp.VarbindCollection;

/**
 * An {@link SnmpNotificationEventFactory} implemented on top of SNMP4j.
 *
 * @author Carl Harris
 */
class Snmp4jNotificationEventFactory implements SnmpNotificationEventFactory {

  private final VarbindFactory varbindFactory;

  public Snmp4jNotificationEventFactory(VarbindFactory varbindFactory) {
    this.varbindFactory = varbindFactory;
  }

  @Override
  public SnmpNotificationEvent newEvent(SnmpListener source,
      CommandResponderEvent event) {
    return new Snmp4jNotificationEvent(source, newNotification(event));
  }

  private SnmpNotification newNotification(CommandResponderEvent event) {
    return newNotification(event, newTarget(event),
        varbindFactory.newVarbindCollection(event.getPDU()));
  }

  private Snmp4jNotification newNotification(CommandResponderEvent event,
      SnmpTarget target, VarbindCollection varbinds) {
    switch (event.getPDU().getType()) {
      case PDU.V1TRAP:
        Snmp4jV1Trap trap = new Snmp4jV1Trap(target, varbinds);
        PDUv1 pdu = (PDUv1) event.getPDU();
        trap.setEnterprise(pdu.getEnterprise().toString());
        trap.setAgentAddress(pdu.getAgentAddress().toString());
        MibTrapV1Support trapSupport = varbindFactory.getMib()
            .getV1TrapSupport();
        trap.setGenericType(new ImmutableObjectValue(SMIConstants.SYNTAX_INTEGER,
            pdu.getGenericTrap(), trapSupport.getGenericTrapFormatter()));
        trap.setSpecificType(new ImmutableObjectValue(SMIConstants.SYNTAX_INTEGER,
            pdu.getSpecificTrap(), trapSupport.getSpecificTrapFormatter()));
        trap.setTimestamp(new ImmutableObjectValue(SMIConstants.SYNTAX_TIMETICKS,
            pdu.getTimestamp(), trapSupport.getTimestampFormatter()));
        return trap;
      case PDU.INFORM:
        return new Snmp4jNotification(SnmpNotification.Type.INFORM, target,
            varbinds);
      case PDU.TRAP:
        return new Snmp4jNotification(SnmpNotification.Type.TRAP, target,
            varbinds);
      default:
        throw new IllegalArgumentException("unrecognized PDU type");
    }
  }

  private SnmpTarget newTarget(CommandResponderEvent event) {
    SnmpTargetBase target = newTarget(event.getMessageProcessingModel());
    Address address = event.getPeerAddress();
    String uri = address.toString();
    int i = uri.indexOf('/');
    if (i == -1) i = uri.length();
    target.setAddress(uri.substring(0, i));
    return target;
  }

  private SnmpTargetBase newTarget(int messageProcessingModel) {
    switch (messageProcessingModel) {
      case MessageProcessingModel.MPv1:
      case MessageProcessingModel.MPv2c:
        return new SimpleSnmpV2cTarget();
      case MessageProcessingModel.MPv3:
        return new SimpleSnmpV3Target();
      default:
        throw new IllegalArgumentException(
            "unsupported message processing model");
    }
  }

}
