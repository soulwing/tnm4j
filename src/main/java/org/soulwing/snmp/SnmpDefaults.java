/*
 * File created on Apr 14, 2015
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
package org.soulwing.snmp;

/**
 * Various defaults for SNMP.
 *
 * @author Carl Harris
 */
public interface SnmpDefaults {

  int AGENT_PORT = 161;

  int NOTIFICATION_PORT = 162;

  int RETRIES = 2;

  long TIMEOUT = 10000L;

  int WALK_MAX_REPETITIONS = 10;

}
