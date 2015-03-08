/*
 * tnm4j - Simplified SNMP API for Java
 * Copyright (C) 2012 Carl Harris, Jr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.soulwing.snmp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * An API for synchronous SNMP operations.
 *
 * @author Carl Harris
 */
public interface SnmpOperations {

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

}
