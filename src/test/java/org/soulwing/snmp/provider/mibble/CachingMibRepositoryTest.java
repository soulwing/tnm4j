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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.describedAs;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import net.percederberg.mibble.Mib;

/**
 * Unit tests for {@link CachingMibRepository}.
 *
 * @author Carl Harris
 */
public class CachingMibRepositoryTest {

  @Test
  public void testMibNameOrder() throws Exception {
    final CachingMibRepository repository = new CachingMibRepository();
    repository.load("SNMPv2-SMI");
    repository.load("SNMPv2-TC");
    repository.load("SNMPv2-CONF");
    repository.load("SNMPv2-MIB");

    assertThat(repository.names(), contains("SNMPv2-MIB", "SNMPv2-CONF",
        "SNMPv2-TC", "SNMPv2-SMI"));

    final MibbleMib mib = new MibbleMib(repository, null, null);
    assertThat(mib.getSymbolByOid("1.3.6.1.2.1.1.3.0").getName(),
        is(equalTo("sysUpTime")));
  }

}