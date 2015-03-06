package org.soulwing.snmp.provider.snmp4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.snmp4j.smi.AbstractVariable;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.soulwing.snmp.Formatter;
import org.soulwing.snmp.IndexDescriptor;
import org.soulwing.snmp.IndexExtractor;
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
  public int toInt() {
    if (delegate.getVariable() == null) {
      return 0;
    }
    return delegate.getVariable().toInt();
  }

  @Override
  public long toLong() {
    if (delegate.getVariable() == null) {
      return 0;
    }
    return delegate.getVariable().toLong();
  }

  @Override
  public Object toObject() {
    return toObject(delegate.getVariable());
  }

  @Override
  public String toString() {
    return formatter.format(toObject(delegate.getVariable()));
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
      return (T) Integer.valueOf(toInt());
    }
    if (Number.class.isAssignableFrom(targetClass)) {
      return (T) Long.valueOf(toLong());
    }
    if (String.class.isAssignableFrom(targetClass)) {
      return (T) toString();
    }
    if (InetAddress.class.isAssignableFrom(targetClass)) {
      try {
        return (T) InetAddress.getByName(toString());
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
      throw new IllegalArgumentException("not an indexed object: " + getOid());
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

}
