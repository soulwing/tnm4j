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
package org.soulwing.snmp.provider.mibble;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibType;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.percederberg.mibble.value.ObjectIdentifierValue;
import org.soulwing.snmp.Formatter;
import org.soulwing.snmp.IndexExtractor;
import org.soulwing.snmp.Mib;
import org.soulwing.snmp.ModuleParseException;

class MibbleMib implements Mib {

  private final MibLoader loader = new MibLoader();

  private final MibRepository repository;

  private final FormatterFactory formatterFactory;

  private final IndexExtractorFactory indexExtractorFactory;

  public MibbleMib() {
    this(new CachingMibRepository(), new CachingFormatterFactory(),
        new CachingIndexExtractorFactory());
  }

  MibbleMib(MibRepository repository, FormatterFactory formatterFactory,
      IndexExtractorFactory indexExtractorFactory) {
    this.repository = repository;
    this.formatterFactory = formatterFactory;
    this.indexExtractorFactory = indexExtractorFactory;
  }

  @Override
  public String oidToInstanceName(String oid) {
    MibValueSymbol symbol = getSymbolByOid(oid);
    if (symbol == null) return oid;
    String name = oid;
    String baseOid = symbol.getValue().toString();
    name = name.replaceFirst("^" + baseOid, symbol.getName());
    return name;
  }

  @Override
  public String oidToObjectName(String oid) {
    MibValueSymbol symbol = getSymbolByOid(oid);
    if (symbol == null) return oid;
    return symbol.getName();
  }

  @Override
  public String nameToOid(String name) {
    int i = name.indexOf('!');
    int j = name.indexOf('.');
    ObjectIdentifierValue value = null;
    String instanceId = null;
    if (j != -1) {
      instanceId = name.substring(j);
      name = name.substring(0, j);
    }
    if (i != -1) {
      String scope = name.substring(0, i);
      name = name.substring(i + 1);
      value = getSymbol(scope, name);
    }
    else {
      value = getSymbol(name);
    }
    if (value == null) return null;
    if (instanceId != null) {
      return value.toString() + instanceId;
    }
    return value.toString();
  }

  @Override
  public int syntaxForObject(String oid) {
    MibValueSymbol symbol = getSymbolByOid(oid);
    if (symbol == null) {
      throw new IllegalArgumentException("unrecognized object identifier");
    }
    if (!(symbol.getType() instanceof SnmpObjectType)) {
      throw new IllegalArgumentException("not an OBJECT-TYPE object");
    }
    MibType type = symbol.getType();
    MibType syntax = ((SnmpObjectType) type).getSyntax();
    int category = syntax.getTag().getCategory();
    int value = syntax.getTag().getValue();
    return (category<<6) + value;
  }

  MibValueSymbol getSymbolByOid(String oid) {
    MibValueSymbol symbol = null;
    for (String scope : repository.names()) {
      symbol = getSymbolByOid(scope, oid);
      if (symbol != null) break;
    }
    return symbol;
  }

  private MibValueSymbol getSymbolByOid(String scope, String oid) {
    final net.percederberg.mibble.Mib mib = repository.get(scope);
    if (mib == null) return null;
    return mib.getSymbolByOid(oid);
  }
  
  private ObjectIdentifierValue getSymbol(String name) {
    ObjectIdentifierValue oid = null;
    for (String scope : repository.names()) {
      oid = getSymbol(scope, name);
      if (oid != null) break;
    }
    return oid;
  }
  
  private ObjectIdentifierValue getSymbol(String scope, String name) {
    final net.percederberg.mibble.Mib mib = repository.get(scope);
    if (mib == null) return null;
    MibValueSymbol symbol = (MibValueSymbol) mib.getSymbol(name);  
    if (symbol == null) return null;
    return (ObjectIdentifierValue) symbol.getValue();
  }
  
  @Override
  public Formatter newFormatter(String oid) {
    return formatterFactory.getFormatter(getSymbolByOid(oid));
  }

  @Override
  public IndexExtractor newIndexExtractor(String oid) {
    return indexExtractorFactory.getIndexExtractor(getSymbolByOid(oid));
  }

  @Override
  public void load(String name) throws ModuleParseException, IOException {
    try {
      repository.load(name);
    }
    catch (MibLoaderException ex) {
      throw new ModuleParseException(ex.getMessage(), ex);
    }
  }

  @Override
  public void load(File file) throws ModuleParseException, IOException {
    try {
      repository.load(file);
    }
    catch (MibLoaderException ex) {
      throw new ModuleParseException(ex.getMessage(), ex);
    }
  }
  
  @Override
  public void load(URL url) throws ModuleParseException, IOException {
    try {
      repository.load(url);
    }
    catch (MibLoaderException ex) {
      throw new ModuleParseException(ex.getMessage(), ex);
    }
  }

}
