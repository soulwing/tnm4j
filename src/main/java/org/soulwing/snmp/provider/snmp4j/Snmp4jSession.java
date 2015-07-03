/*
 * File created on Apr 30, 2015
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

import org.snmp4j.PDU;
import org.snmp4j.Session;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;

/**
 * A session context for an operation.
 * <p>
 * @author Carl Harris
 */
interface Snmp4jSession extends Session {

  ResponseEvent get(PDU pdu, Target target) throws IOException;

  void get(PDU pdu, Target target, Object userHandle,
      ResponseListener listener) throws IOException;

  ResponseEvent set(PDU pdu, Target target) throws IOException;

  void set(PDU pdu, Target target, Object userHandle,
      ResponseListener listener) throws IOException;

  ResponseEvent getNext(PDU pdu, Target target) throws IOException;

  void getNext(PDU pdu, Target target, Object userHandle,
      ResponseListener listener) throws IOException;

  ResponseEvent getBulk(PDU pdu, Target target) throws IOException;

  void getBulk(PDU pdu, Target target, Object userHandle,
      ResponseListener listener) throws IOException;

}