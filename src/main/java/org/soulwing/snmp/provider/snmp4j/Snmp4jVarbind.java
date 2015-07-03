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
package org.soulwing.snmp.provider.snmp4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.snmp4j.smi.AbstractVariable;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.soulwing.snmp.Formatter;
import org.soulwing.snmp.IndexDescriptor;
import org.soulwing.snmp.IndexExtractor;
import org.soulwing.snmp.SnmpException;
import org.soulwing.snmp.Varbind;

class Snmp4jVarbind implements Varbind {

  private final String name;
  private final VariableBinding delegate;
  private final Formatter formatter;
  private final IndexExtractor indexExtractor;
  private final VarbindFactory varbindFactory;
  
  public Snmp4jVarbind(String name, VariableBinding delegate, 
      Formatter formatter, IndexExtractor indexExtractor,
      VarbindFactory varbindFactory) {
    this.name = name;
    this.delegate = delegate;
    this.formatter = formatter;
    this.indexExtractor = indexExtractor;
    this.varbindFactory = varbindFactory;
  }

  public VariableBinding getDelegate() {
    return delegate;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getOid() {
    return delegate.getOid().toString();
  }

  @Override
  public int getSyntax() {
    return delegate.getSyntax();
  }

  @Override
  public int asInt() {
    Variable variable = getVariable();
    if (variable == null) {
      return 0;
    }
    return variable.toInt();
  }

  @Override
  public long asLong() {
    Variable variable = getVariable();
    if (variable == null) {
      return 0;
    }
    return variable.toLong();
  }

  @Override
  public Object toObject() {
    return toObject(getVariable());
  }

  @Override
  public String asString() {
    return formatter.format(toObject(getVariable()));
  }

  @Override
  public void set(Object value) {
    delegate.setVariable(newVariable(delegate.getSyntax(), value));
  }

  private Variable getVariable() throws SnmpException {
    Variable variable = delegate.getVariable();
    if (variable.isException()) {
      throw new SnmpException(String.format("%s %s: %s",
          getName(), getOid(), variable.toString()));
    }
    return variable;
  }

  private static Object toObject(Variable variable) {
    if (variable instanceof OID) {
      return ((OID) variable).toIntArray();
    }
    else if (variable instanceof OctetString) {
      return ((OctetString) variable).toByteArray();
    }
    else if (variable instanceof IpAddress) {
      return ((IpAddress) variable).toByteArray();
    }
    else {
      return variable.toLong();
    }
  }
  
  @SuppressWarnings("unchecked")
  public <T> T asType(Class<T> targetClass) {
    if (Integer.class.isAssignableFrom(targetClass)) {
      return (T) Integer.valueOf(asInt());
    }
    if (Number.class.isAssignableFrom(targetClass)) {
      return (T) Long.valueOf(asLong());
    }
    if (String.class.isAssignableFrom(targetClass)) {
      return (T) asString();
    }
    if (InetAddress.class.isAssignableFrom(targetClass)) {
      try {
        return (T) InetAddress.getByName(asString());
      }
      catch (UnknownHostException ex) {
        throw new IllegalArgumentException("object does not contain an IP address");
      }
    }
    throw new IllegalArgumentException("unsupported target class");
  }
  
  @Override
  public Varbind[] getIndexes() {
    if (indexExtractor == null) {
      return new Varbind[0];
    }
    IndexDescriptor[] descriptors = indexExtractor.extractIndexes(getOid());
    Varbind[] varbinds = new Varbind[descriptors.length];
    for (int i = 0; i < descriptors.length; i++) {
      IndexDescriptor descriptor = descriptors[i];
      Variable variable = AbstractVariable.createFromSyntax(
          descriptor.getSyntax());
      variable.fromSubIndex(new OID(descriptor.getEncoded()), 
          descriptor.isImplied());
      VariableBinding vb = new VariableBinding(new OID(descriptor.getOid()), 
          variable);
      varbinds[i] = varbindFactory.newVarbind(vb);
    }
    return varbinds;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof Varbind)) return false;
    return this.toObject().equals(((Varbind) obj).toObject());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.toObject().hashCode();
  }

  @Override
  public String toString() {
    return asString();
  }

  static Variable newVariable(int syntax, Object value) {
    Variable variable = AbstractVariable.createFromSyntax(syntax);
    if (variable instanceof Integer32) {
      ((Integer32) variable).setValue(((Number) value).intValue());
    }
    else if (variable instanceof UnsignedInteger32) {
      ((UnsignedInteger32) variable).setValue(Math.abs(((Number) value).longValue()));
    }
    else if (variable instanceof Counter64) {
      ((Counter64) variable).setValue(((Number) value).longValue());
    }
    else if (variable instanceof OctetString) {
      if (value instanceof String) {
        ((OctetString) variable).setValue((String) value);
      }
      else {
        ((OctetString) variable).setValue((byte[]) value);
      }
    }
    else if (variable instanceof OID) {
      if (value instanceof String) {
        ((OID) variable).setValue((String) value);
      }
      else {
        ((OID) variable).setValue((int[]) value);
      }
    }
    else {
      throw new IllegalStateException("unrecognized type");
    }
    return variable;
  }

}
