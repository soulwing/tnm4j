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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoaderException;
import org.soulwing.snmp.ModuleParseException;

/**
 * A repository of MIB modules.
 *
 * @author Carl Harris
 */
interface MibRepository {

  /**
   * Retrieves an iterable containing the names of MIB modules that have
   * been loaded.
   * @return iterable of names
   */
  Iterable<String> names();

  /**
   * Retrieves a previously cached MIB module by name.
   * @param name name of the module to retrieve
   * @return mib or {@code null} if a MIB with the given name has not been
   *    cached
   */
  Mib get(String name);

  /**
   * Loads a MIB module by name.
   * <p>
   * A previously cached value of the MIB module may be returned by this method.
   * @param name name of the module to load
   * @return MIB module
   * @throws MibLoaderException if the module cannot be parsed
   * @throws IOException if an I/O error occurs
   */
  Mib load(String name) throws MibLoaderException, IOException;

  /**
   * Loads a MIB module from a file.
   * <p>
   * Any previously cached instance of the same MIB module (matched by name)
   * will be replaced by the loaded module.
   *
   * @param file a MIB file to load
   * @return MIB module
   * @throws MibLoaderException if the module cannot be parsed
   * @throws IOException if an I/O error occurs
   */
  Mib load(File file) throws MibLoaderException, IOException;

  /**
   * Loads a MIB module from a resource location.
   * <p>
   * Any previously cached instance of the same MIB module (matched by name)
   * will be replaced by the loaded module.
   *
   * @param url a MIB resource location
   * @return MIB module
   * @throws MibLoaderException if the module cannot be parsed
   * @throws IOException if an I/O error occurs
   */
  Mib load(URL url) throws MibLoaderException, IOException;

  /**
   * Adds a directory to the MIB file search path.
   * @param directory the directory to add
   */
  void addDirectory(File directory);

  /**
   * Removes a directory from the MIB file search path.
   * @param directory the directory to remove
   */
  void removeDirectory(File directory);

}
