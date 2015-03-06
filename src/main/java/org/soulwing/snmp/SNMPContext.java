/*
 * File created on Nov 30, 2012 
 *
 * Copyright (c) 2013 Carl Harris, Jr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.soulwing.snmp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public interface SNMPContext {

  String getAddress();
  
  void setAddress(String address) throws UnknownHostException;
  
  int getPort();
  
  void setPort(int port);
  
  int getRetries();
  
  void setRetries(int retries);
  
  long getTimeout();
  
  void setTimeout(long timeout);
  
  int getWalkMaxRepetitions();
  
  void setWalkMaxRepetitions(int value);
  
  boolean isWalkAllowsTruncatedRepetition();
  
  void setWalkAllowsTruncatedRepetition(boolean state);
  
  MIB getMib();
  
  void dispose();
  
  List<Varbind> get(List<String> oids) throws IOException;

  List<Varbind> get(String... oids) throws IOException;

  List<Varbind> getNext(List<String> oids) throws IOException;

  List<Varbind> getNext(String... oids) throws IOException;

  List<Varbind> getBulk(int nonRepeaters, int maxRepetitions, 
      List<String> oids) throws IOException;

  List<Varbind> getBulk(int nonRepeaters, int maxRepetitions, 
      String... oids) throws IOException;
  
  List<Map<String, Varbind>> walk(int nonRepeaters, List<String> oids)
      throws IOException;

  List<Map<String, Varbind>> walk(int nonRepeaters, String... oids)
      throws IOException;

  List<Map<String, Varbind>> walk(List<String> nonRepeaters, 
      List<String> repeaters) throws IOException;

  List<Map<String, Varbind>> walk(List<String> repeaters) throws IOException;

  Varbind newVarbind(String oid, Object value);
  
}
