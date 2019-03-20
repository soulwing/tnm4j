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
package org.soulwing.snmp.provider.mibble;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibValueSymbol;

/**
 * Unit tests for {@link MibbleIndexExtractor}.
 *
 * @author Carl Harris
 */
public class MibbleIndexExtractorTest {

  @Test
  public void testGithubIssue9() throws Exception {
    // Test case for https://github.com/soulwing/tnm4j/issues/9
    MibLoader loader = new MibLoader();
    loader.load("SNMPv2-SMI");
    loader.load("SNMPv2-TC");
    loader.load("SNMPv2-CONF");
    loader.load("SNMP-FRAMEWORK-MIB");
    loader.load("SNMP-TARGET-MIB");
    loader.load("SNMP-NOTIFICATION-MIB");
    loader.load("IANA-ITU-ALARM-TC-MIB");
    loader.load("ITU-ALARM-TC-MIB");
    loader.load("INET-ADDRESS-MIB");
    loader.load("RFC1155-SMI");
    loader.load("RFC1158-MIB");
    loader.load("RFC-1212");
    loader.load("RFC1213-MIB");
    loader.load("RMON-MIB");
    loader.load("RFC1271-MIB");
    loader.load("TOKEN-RING-RMON-MIB");
    loader.load("RMON2-MIB");
    loader.load("ALARM-MIB");
    loader.load(getClass().getClassLoader().getResource("mibs/ERICSSON-TOP-MIB"));
    loader.load(getClass().getClassLoader().getResource("mibs/ERICSSON-TC-MIB"));
    loader.load(getClass().getClassLoader().getResource("mibs/ERICSSON-ALARM-TC-MIB"));
    loader.load(getClass().getClassLoader().getResource("mibs/ERICSSON-ALARM-PC-MIB"));
    loader.load(getClass().getClassLoader().getResource("mibs/ERICSSON-ALARM-MIB"));

    // this is an OID reported in the notification
    final String oid = "1.3.6.1.4.1.193.183.4.1.3.5.1.5";

    final Mib mib = loader.getMib("ERICSSON-ALARM-MIB");
    final MibValueSymbol symbol = mib.getSymbolByOid(oid);
    final MibbleIndexExtractor extractor = new MibbleIndexExtractor(symbol);
    assertThat(extractor.extractIndexes(oid), is(emptyArray()));
  }

}
