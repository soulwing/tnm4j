/*
 * File created on Mar 20, 2019
 *
 * Copyright (c) 2019 Carl Harris, Jr
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.soulwing.snmp.Mib;
import org.soulwing.snmp.Varbind;
import org.soulwing.snmp.provider.mibble.MibbleMibProvider;
import net.percederberg.mibble.MibLoader;

/**
 * Unit tests for {@link SimpleVarbindFactory}.
 *
 * @author Carl Harris
 */
public class SimpleVarbindFactoryTest {

  @Test
  public void testGitHubIssue9() throws Exception {
    final MibbleMibProvider provider = new MibbleMibProvider();
    final Mib mib = provider.newMib();
    mib.load("SNMPv2-SMI");
    mib.load("SNMPv2-TC");
    mib.load("SNMPv2-CONF");
    mib.load("SNMP-FRAMEWORK-MIB");
    mib.load("SNMP-TARGET-MIB");
    mib.load("SNMP-NOTIFICATION-MIB");
    mib.load("IANA-ITU-ALARM-TC-MIB");
    mib.load("ITU-ALARM-TC-MIB");
    mib.load("INET-ADDRESS-MIB");
    mib.load("RFC1155-SMI");
    mib.load("RFC1158-MIB");
    mib.load("RFC-1212");
    mib.load("RFC1213-MIB");
    mib.load("RMON-MIB");
    mib.load("RFC1271-MIB");
    mib.load("TOKEN-RING-RMON-MIB");
    mib.load("RMON2-MIB");
    mib.load("ALARM-MIB");
    mib.load(getClass().getClassLoader().getResource("mibs/ERICSSON-TOP-MIB"));
    mib.load(getClass().getClassLoader().getResource("mibs/ERICSSON-TC-MIB"));
    mib.load(getClass().getClassLoader().getResource("mibs/ERICSSON-ALARM-TC-MIB"));
    mib.load(getClass().getClassLoader().getResource("mibs/ERICSSON-ALARM-PC-MIB"));
    mib.load(getClass().getClassLoader().getResource("mibs/ERICSSON-ALARM-MIB"));

    VariableBinding binding = new VariableBinding();
    binding.setOid(new OID("1.3.6.1.2.1.1.3.0"));
    binding.setVariable(new Integer32(42));

    final SimpleVarbindFactory factory = new SimpleVarbindFactory(mib);
    final Varbind vb = factory.newVarbind(binding);
    assertThat(vb.getName(), is(equalTo("sysUpTime.0")));
  }

}