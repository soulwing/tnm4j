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

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibValueSymbol;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.snmp.Formatter;

/**
 * Unit tests for {@link CachingFormatterFactory}.
 *
 * @author Carl Harris
 */
public class CachingFormatterFactoryTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();


  @Mock
  private Formatter objectFormatter;

  private CachingFormatterFactory factory = new CachingFormatterFactory();

  private Mib mib;

  @Before
  public void setUp() throws Exception {
    MibLoader loader = new MibLoader();
    mib = loader.load("RFC1213-MIB");
  }

  @Test
  public void testNewFormatter() throws Exception {
    MibValueSymbol symbol = mib.getSymbolByOid(Constants.IF_DESCR_OID);
    Formatter formatter = factory.getFormatter(symbol);
    assertThat(formatter, is(not(nullValue())));
    assertThat(factory.getFormatter(symbol), is(sameInstance(formatter)));
  }

  @Test
  public void testNewFormatterForUnknownObject() throws Exception {
    Formatter formatter = factory.getFormatter(null);
    assertThat(formatter,
        is(sameInstance(CachingFormatterFactory.TO_STRING_FORMATTER)));
  }

}
