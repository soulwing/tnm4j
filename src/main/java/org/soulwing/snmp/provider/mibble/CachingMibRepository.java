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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;

/**
 * A {@link MibRepository} that caches and reuses loaded MIB modules.
 *
 * @author Carl Harris
 */
class CachingMibRepository implements MibRepository {

  private final Lock lock = new ReentrantLock();

  private final MibLoader loader = new MibLoader();

  private final List<String> names = new ArrayList<>();

  private final Map<String, Mib> cache = new HashMap<String, Mib>();

  /**
   * Retrieves an iterable containing the names of MIB modules that have
   * been loaded.
   *
   * @return iterable of names
   */
  @Override
  public Iterable<String> names() {
    return names;
  }

  /**
   * Retrieves a previously cached MIB module by name.
   *
   * @param name name of the module to retrieve
   * @return mib or {@code null} if a MIB with the given name has not been
   * cached
   */
  @Override
  public Mib get(String name) {
    return cache.get(name);
  }

  @Override
  public Mib load(String name) throws MibLoaderException, IOException {
    lock.lock();
    try {
      Mib mib = cache.get(name);
      if (mib == null) {
        mib = installMib(loader.load(name));
      }
      return mib;
    }
    finally {
      lock.unlock();
    }
  }

  @Override
  public Mib load(File file) throws MibLoaderException, IOException {
    return installMib(loader.load(file));
  }

  @Override
  public Mib load(URL url) throws MibLoaderException, IOException {
    return installMib(loader.load(url));
  }

  private Mib installMib(Mib mib) {
    lock.lock();
    try {
      final String name = mib.getName();
      names.add(0, name);
      cache.put(name, mib);
      return mib;
    }
    finally {
      lock.unlock();
    }
  }

  @Override
  public void addDirectory(File directory) {
    loader.addDir(directory);
  }

  @Override
  public void removeDirectory(File directory) {
    loader.removeDir(directory);
  }

}
