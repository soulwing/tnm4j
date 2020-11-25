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

import org.soulwing.snmp.SimpleSnmpV1Target;
import org.soulwing.snmp.SimpleSnmpV2cTarget;
import org.soulwing.snmp.SimpleSnmpV3Target;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.SnmpV3Target;

/**
 * Static helper methods to create target objects using properties that match
 * the configuration specified in snmpd.conf and docker-compose.yml
 */
class ExampleTargets {

  static final String HOST = "127.0.0.1";
  static final int PORT = 11611;

  static SnmpTarget v1ReadOnly() {
    SimpleSnmpV1Target target = new SimpleSnmpV1Target();
    target.setAddress(HOST);
    target.setPort(PORT);
    target.setCommunity("public");
    return target;
  }

  static SnmpTarget v1ReadWrite() {
    SimpleSnmpV1Target target = new SimpleSnmpV1Target();
    target.setAddress(HOST);
    target.setPort(PORT);
    target.setCommunity("private");
    return target;
  }

  static SnmpTarget v2ReadOnly() {
    SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
    target.setAddress(HOST);
    target.setPort(PORT);
    target.setCommunity("public");
    return target;
  }

  static SnmpTarget v2ReadWrite() {
    SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
    target.setAddress(HOST);
    target.setPort(PORT);
    target.setCommunity("private");
    return target;
  }

  static SnmpTarget v3ReadOnly() {
    SimpleSnmpV3Target target = new SimpleSnmpV3Target();
    target.setAddress(HOST);
    target.setPort(PORT);
    target.setSecurityName("everybody");
    target.setAuthType(SnmpV3Target.AuthType.MD5);
    target.setAuthPassphrase("everywhere");
    target.setPrivType(SnmpV3Target.PrivType.DES);
    target.setPrivPassphrase("everytime");
    return target;
  }

  static SnmpTarget v3ReadWrite() {
    SimpleSnmpV3Target target = new SimpleSnmpV3Target();
    target.setAddress(HOST);
    target.setPort(PORT);
    target.setSecurityName("somebody");
    target.setAuthType(SnmpV3Target.AuthType.SHA);
    target.setAuthPassphrase("somewhere");
    target.setPrivType(SnmpV3Target.PrivType.AES128);
    target.setPrivPassphrase("sometime");
    return target;
  }

}
