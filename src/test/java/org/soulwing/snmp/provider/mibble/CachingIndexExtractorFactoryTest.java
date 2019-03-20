/*
 * File created on Apr 3, 2015
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
package org.soulwing.snmp.provider.mibble;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.io.File;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.snmp.IndexExtractor;
import org.soulwing.snmp.MibFactory;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibValueSymbol;

/**
 * Unit tests for {@link CachingIndexExtractorFactory}.
 *
 * @author Carl Harris
 */
public class CachingIndexExtractorFactoryTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  private CachingIndexExtractorFactory factory =
      new CachingIndexExtractorFactory();

  private Mib mib;

  @Before
  public void setUp() throws Exception {
    MibLoader loader = new MibLoader();
    mib = loader.load("RFC1213-MIB");
  }

  @Test
  public void testNewIndexExtractor() throws Exception {
    MibValueSymbol symbol = mib.getSymbolByOid(Constants.IF_DESCR_OID + ".1");
    IndexExtractor extractor = factory.getIndexExtractor(symbol);
    assertThat(extractor, is(not(nullValue())));
    assertThat(factory.getIndexExtractor(symbol), is(sameInstance(extractor)));
  }

  @Test
  public void testNewIndexExtractorForUnknownObject() throws Exception {
    assertThat(factory.getIndexExtractor(null), is(nullValue()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNewIndexExtractorForNonTableColumn() throws Exception {
    MibValueSymbol symbol = mib.getSymbolByOid(Constants.SYS_DESCR_OID);
    factory.getIndexExtractor(symbol);
  }

  @Test
  public void testNewIndexExtractorForColumnType() throws Exception {
    MibValueSymbol symbol = mib.getSymbolByOid(Constants.IF_DESCR_OID);
    IndexExtractor extractor = factory.getIndexExtractor(symbol);
    assertThat(extractor, is(not(nullValue())));
    assertThat(factory.getIndexExtractor(symbol), is(sameInstance(extractor)));
  }

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
    loader.load(new File("ERICSSON-TOP-MIB"));
    loader.load(new File("ERICSSON-TC-MIB"));
    loader.load(new File("ERICSSON-ALARM-TC-MIB"));
    loader.load(new File("ERICSSON-ALARM-PC-MIB"));
    loader.load(new File("ERICSSON-ALARM-MIB"));

    final Mib mib = loader.getMib("ERICSSON-ALARM-MIB");
    MibValueSymbol symbol = mib.getSymbolByOid("1.3.6.1.4.1.193.183.4.1.3.5.1.5");
    IndexExtractor extractor = factory.getIndexExtractor(symbol);
    assertThat(extractor, is(not(nullValue())));
    assertThat(factory.getIndexExtractor(symbol), is(sameInstance(extractor)));
    extractor.extractIndexes("1.3.6.1.4.1.193.183.4.1.3.5.1.5");
  }
  

}
