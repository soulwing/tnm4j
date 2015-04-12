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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A mutable {@link VarbindCollection}.
 *
 * @author Carl Harris
 */
public class MutableVarbindCollection implements VarbindCollection {

  private final List<Varbind> varbinds;
  
  private final Map<String, Varbind> varbindMap;
  
  public MutableVarbindCollection() {
    this(new ArrayList<Varbind>(), new LinkedHashMap<String, Varbind>());
  }
  
  private MutableVarbindCollection(List<Varbind> varbinds, 
      Map<String, Varbind> varbindMap) {
    this.varbinds = varbinds;
    this.varbindMap = varbindMap;
  }
  
  /**
   * Creates an immutable copy of the receiver.
   * @return immutable copy
   */
  public VarbindCollection immutableCopy() {
    return new MutableVarbindCollection(
        Collections.unmodifiableList(varbinds),
        Collections.unmodifiableMap(varbindMap));
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<Varbind> iterator() {
    return varbinds.iterator();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int size() {
    return varbinds.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> keySet() {
    return varbindMap.keySet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Varbind get(int index) {
    return varbinds.get(index);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Varbind get(String oid) {
    return varbindMap.get(oid);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Varbind> asList() {
    return Collections.unmodifiableList(varbinds);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Varbind> asMap() {
    return Collections.unmodifiableMap(varbindMap);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> nextIdentifiers(List<String> oids) {
    List<String> list = new ArrayList<String>(
        varbinds.size());
    list.addAll(oids);
    for (int i = oids.size(), max = varbinds.size(); i < max; i++) {
      list.add(varbinds.get(i).getOid());
    }
    return list;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> nextIdentifiers(String... ids) {
    return nextIdentifiers(Arrays.asList(ids));
  }

  public void add(int index, String key, Varbind varbind) {
    varbinds.add(index, varbind);
    varbindMap.put(key, varbind);
  }

  public void addIndex(String key, Varbind varbind) {
    varbindMap.put(key, varbind);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{ ");
    for (Varbind varbind : varbinds) {
      sb.append(varbind.getName())
        .append('=')
        .append(varbind.asString())
        .append(" ");
    }
    sb.append("}");
    return sb.toString();
  }

}
