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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.net.URL;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import net.percederberg.mibble.MibLoader;

/**
 * Tests for {@link MibbleMib}.
 *
 * @author Carl Harris
 */
public class MibbleMibTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private MibRepository repository;

  @Mock
  private FormatterFactory formatterFactory;

  @Mock
  private IndexExtractorFactory indexExtractorFactory;

  private net.percederberg.mibble.Mib delegate;

  private MibbleMib mib;

  @Before
  public void setUp() throws Exception {
    mib = new MibbleMib(repository, formatterFactory, indexExtractorFactory);
    MibLoader loader = new MibLoader();
    delegate = loader.load(Constants.RFC1213_MIB);
  }

  @Test
  public void testOidToInstanceName() throws Exception {
    context.checking(symbolByOidExpectations());
    assertThat(mib.oidToInstanceName(Constants.SYS_DESCR_OID + ".0"),
        is(equalTo("sysDescr.0")));
  }

  @Test
  public void testOidToInstanceNameWhenNameNotFound() throws Exception {
    context.checking(symbolByOidExpectations());
    assertThat(mib.oidToInstanceName(Constants.UNKNOWN_OID),
        is(equalTo(Constants.UNKNOWN_OID)));
  }

  @Test
  public void testOidToObjectName() throws Exception {
    context.checking(symbolByOidExpectations());
    assertThat(mib.oidToObjectName(Constants.SYS_DESCR_OID + ".0"),
        is(equalTo("sysDescr")));
  }

  @Test
  public void testOidToObjectNameWhenNameNotFound() throws Exception {
    context.checking(symbolByOidExpectations());
    assertThat(mib.oidToObjectName(Constants.UNKNOWN_OID),
        is(equalTo(Constants.UNKNOWN_OID)));
  }

  @Test
  public void testNameToOidWithUnqualifiedName() throws Exception {
    context.checking(symbolByOidExpectations());
    assertThat(mib.nameToOid("sysDescr"),
        is(equalTo(Constants.SYS_DESCR_OID)));
  }

  @Test
  public void testNameToOidWithQualifiedName() throws Exception {
    context.checking(symbolByOidExpectations());
    assertThat(mib.nameToOid("RFC1213-MIB!sysDescr"),
        is(equalTo(Constants.SYS_DESCR_OID)));
  }

  @Test
  public void testNameToOidWithIndexedName() throws Exception {
    context.checking(symbolByOidExpectations());
    assertThat(mib.nameToOid("sysDescr.0"),
        is(equalTo("1.3.6.1.2.1.1.1.0")));
  }

  @Test
  public void testSyntaxForObject() throws Exception {
    context.checking(symbolByOidExpectations());
    assertThat(mib.syntaxForObject(Constants.SYS_DESCR_OID),
        is(equalTo(4)));    // simple type; OCTET STRING
    assertThat(mib.syntaxForObject("1.3.6.1.2.1.1.2"),
        is(equalTo(6)));    // simple type; OBJECT IDENTIFIER
    assertThat(mib.syntaxForObject("1.3.6.1.2.1.1.3"),
        is(equalTo(67)));   // application type; TimeTicks
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSyntaxForObjectWithUnknownObject() throws Exception {
    context.checking(symbolByOidExpectations());
    mib.syntaxForObject(Constants.UNKNOWN_OID);
  }

  private Expectations symbolByOidExpectations() {
    return new Expectations() { {
      allowing(repository).names();
      will(returnValue(Collections.singleton(Constants.RFC1213_MIB)));
      allowing(repository).get(Constants.RFC1213_MIB);
      will(returnValue(delegate));
    } };
  }

  @Test
  public void testLoadName() throws Exception {
    final String name = "some name";
    context.checking(new Expectations() { {
      oneOf(repository).load(name);
      will(returnValue(delegate));
    } });

    mib.load(name);
  }

  @Test
  public void testLoadFile() throws Exception {
    final File file = new File("some file");
    context.checking(new Expectations() {
      {
        oneOf(repository).load(file);
        will(returnValue(delegate));
      }
    });

    mib.load(file);
  }

  @Test
  public void testLoadUrl() throws Exception {
    final URL url = new URL("file:/some/url");
    context.checking(new Expectations() {
      {
        oneOf(repository).load(url);
        will(returnValue(delegate));
      }
    });

    mib.load(url);
  }

  @Test
  public void testFindBestNameForOid() throws Exception {
    final MibbleMib mib = new MibbleMib();
    mib.load("SNMPv2-SMI");
    mib.load("SNMPv2-TC");
    mib.load("SNMPv2-MIB");
    mib.load("RFC1155-SMI");
    assertThat(mib.oidToInstanceName("1.3.6.1.6.3.1.1.4.1.0"),
        is(equalTo("snmpTrapOID.0")));
  }

  @Test
  public void testFindBestNameForOidWhenMultipleEqualMatches() throws Exception {
    final MibbleMib mib = new MibbleMib();
    mib.load("SNMPv2-SMI");
    mib.load("SNMPv2-TC");
    mib.load("SNMPv2-MIB");
    mib.load("RFC1213-MIB");
    assertThat(mib.oidToInstanceName("1.3.6.1.6.3.1.1.4.1.0"),
        is(equalTo("snmpTrapOID.0")));
  }


}
