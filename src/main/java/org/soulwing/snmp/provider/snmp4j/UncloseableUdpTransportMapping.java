/*
 * File created on Apr 24, 2015
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

import java.io.IOException;

import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * The implementation of {@link org.snmp4j.Snmp#close()} invokes the close
 * operation on all transport mappings associated with the session.  Our
 * SNMP context objects share a single transport mapping since each transport
 * mapping has an associated listen thread and we want to minimize the number
 * of thread resources needed.  Therefore, we ignore attempts to close the
 * tranpsort mapping until the provider instance is shut down.
 *
 * @author Carl Harris
 */
class UncloseableUdpTransportMapping extends DefaultUdpTransportMapping {

  public UncloseableUdpTransportMapping() throws IOException {
    super();
  }

  @Override
  public void close() throws IOException {
    Snmp4jLogger.logger.debug("deferring transport mapping close operation");
  }

  public void shutdown() throws IOException {
    super.close();
  }

}
