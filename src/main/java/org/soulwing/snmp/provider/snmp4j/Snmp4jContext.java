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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.AbstractVariable;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.soulwing.snmp.Mib;
import org.soulwing.snmp.SnmpAsyncWalker;
import org.soulwing.snmp.SnmpCallback;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpOperation;
import org.soulwing.snmp.SnmpResponse;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.SnmpTargetConfig;
import org.soulwing.snmp.SnmpWalker;
import org.soulwing.snmp.Varbind;
import org.soulwing.snmp.VarbindCollection;

class Snmp4jContext implements SnmpContext {

  private static final Pattern OID_PATTERN = Pattern.compile("^[0-9.]*$");

  private final AtomicBoolean closed = new AtomicBoolean();

  private final SnmpTarget target;
  private final SnmpTargetConfig config;
  private final Mib mib;
  private final Snmp4jSession snmp;
  private final Target snmp4jTarget;
  private final PduFactory pduFactory;
  private final VarbindFactory varbindFactory;
  private final DisposeListener disposeListener;
  
  public Snmp4jContext(SnmpTarget target, SnmpTargetConfig config,
      Mib mib, Snmp snmp, Target snmp4jTarget, PduFactory pduFactory,
      VarbindFactory varbindFactory, DisposeListener disposeListener) {
    this.target = target;
    this.config = config;
    this.mib = mib;
    this.snmp = new SessionWrapper(snmp, config.getRetries(),
        config.getTimeout());
    this.snmp4jTarget = snmp4jTarget;
    this.pduFactory = pduFactory;
    this.varbindFactory = varbindFactory;
    this.disposeListener = disposeListener;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Mib getMib() {
    return mib;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpTarget getTarget() {
    return target;
  }

  /**
   * Gets the {@code session} property.
   * @return property value
   */
  public Snmp4jSession getSession() {
    return snmp;
  }

  /**
   * Gets the {@code snmp4jTarget} property.
   * @return property value
   */
  public Target getSnmp4jTarget() {
    return snmp4jTarget;
  }

  /**
   * Gets the {@code pduFactory} property.
   * @return property value
   */
  public PduFactory getPduFactory() {
    return pduFactory;
  }

  /**
   * Gets the varbind factory associated with this context.
   * @return varbind factory
   */
  public VarbindFactory getVarbindFactory() {
    return varbindFactory;
  }

  /**
   * Gets the {@code config} property.
   * @return property value
   */
  public SnmpTargetConfig getConfig() {
    return config;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() {
    if (!closed.compareAndSet(false, true)) return;
    disposeListener.onDispose(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void finalize() throws Throwable {
    close();
    super.finalize();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Varbind newVarbind(String oid, Object value) {
    VariableBinding varbind = resolveOid(oid);
    int syntax = mib.syntaxForObject(varbind.getOid().toString());
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

    varbind.setVariable(variable);
    return varbindFactory.newVarbind(varbind);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<VarbindCollection> get(List<String> oids) {
    return newGet(oids).invoke();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<VarbindCollection> get(VarbindCollection varbinds) {
    return newGet(varbinds).invoke();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<VarbindCollection> get(String... oids) {
    return newGet(oids).invoke();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<VarbindCollection> set(Varbind... varbinds) {
    return newSet(varbinds).invoke();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<VarbindCollection> set(List<Varbind> varbinds) {
    return newSet(varbinds).invoke();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<VarbindCollection> set(VarbindCollection varbinds) {
    return newSet(varbinds).invoke();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<VarbindCollection> getNext(List<String> oids) {
    return newGetNext(oids).invoke();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<VarbindCollection> getNext(VarbindCollection varbinds) {
    return newGetNext(varbinds).invoke();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<VarbindCollection> getNext(String... oids) {
    return newGetNext(oids).invoke();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<List<VarbindCollection>> getBulk(int nonRepeaters,
      int maxRepetitions, List<String> oids) {
    return newGetBulk(nonRepeaters, maxRepetitions, oids).invoke();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<List<VarbindCollection>> getBulk(int nonRepeaters,
      int maxRepetitions, VarbindCollection varbinds) {
    return newGetBulk(nonRepeaters, maxRepetitions, varbinds).invoke();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<List<VarbindCollection>> getBulk(int nonRepeaters,
      int maxRepetitions, String... oids) {
    return newGetBulk(nonRepeaters, maxRepetitions, oids).invoke();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void asyncGet(SnmpCallback<VarbindCollection> callback,
      List<String> oids) {
    newGet(oids).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void asyncGet(SnmpCallback<VarbindCollection> callback, VarbindCollection varbinds) {
    newGet(varbinds).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void asyncGet(SnmpCallback<VarbindCollection> callback, String... oids) {
    newGet(oids).invoke(callback);
  }

  @Override
  public void asyncSet(SnmpCallback<VarbindCollection> callback, List<Varbind> varbinds) {
    newSet(varbinds).invoke(callback);
  }

  @Override
  public void asyncSet(SnmpCallback<VarbindCollection> callback, VarbindCollection varbinds) {
    newSet(varbinds).invoke(callback);
  }

  @Override
  public void asyncSet(SnmpCallback<VarbindCollection> callback, Varbind... varbinds) {
    newSet(varbinds).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void asyncGetNext(SnmpCallback<VarbindCollection> callback, List<String> oids) {
    newGetNext(oids).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void asyncGetNext(SnmpCallback<VarbindCollection> callback,
      VarbindCollection varbinds) {
    newGetNext(varbinds).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void asyncGetNext(SnmpCallback<VarbindCollection> callback, String... oids) {
    newGetNext(oids).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void asyncGetBulk(SnmpCallback<List<VarbindCollection>> callback,
      int nonRepeaters, int maxRepetitions, List<String> oids) {
    newGetBulk(nonRepeaters, maxRepetitions, oids).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void asyncGetBulk(SnmpCallback<List<VarbindCollection>> callback,
      int nonRepeaters, int maxRepetitions, VarbindCollection varbinds) {
    newGetBulk(nonRepeaters, maxRepetitions, varbinds).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void asyncGetBulk(SnmpCallback<List<VarbindCollection>> callback,
      int nonRepeaters, int maxRepetitions, String... oids) {
    newGetBulk(nonRepeaters, maxRepetitions, oids).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpWalker<VarbindCollection> walk(int nonRepeaters,
      List<String> oids) {
    return new GetBulkSyncWalker(this, resolveOids(oids), nonRepeaters, 
        config.getWalkMaxRepetitions());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpWalker<VarbindCollection> walk(int nonRepeaters, String... oids) {
    return walk(nonRepeaters, Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpWalker<VarbindCollection> walk(List<String> nonRepeaters,
      List<String> repeaters) {
    final int size = nonRepeaters.size() + repeaters.size();
    List<String> oids = new ArrayList<String>(size);
    oids.addAll(nonRepeaters);
    oids.addAll(repeaters);
    return walk(nonRepeaters.size(), oids);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpWalker<VarbindCollection> walk(List<String> repeaters) {
    return walk(0, repeaters);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpWalker<VarbindCollection> walk(String... repeaters) {
    return walk(0, Arrays.asList(repeaters));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void asyncWalk(SnmpCallback<SnmpAsyncWalker<VarbindCollection>> callback,
      int nonRepeaters, List<String> oids) {
    newWalk(nonRepeaters, oids).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void asyncWalk(SnmpCallback<SnmpAsyncWalker<VarbindCollection>> callback,
      int nonRepeaters, String... oids) {
    newWalk(nonRepeaters, oids).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void  asyncWalk(SnmpCallback<SnmpAsyncWalker<VarbindCollection>> callback,
      List<String> nonRepeaters, List<String> repeaters) {
    newWalk(nonRepeaters, repeaters).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void asyncWalk(SnmpCallback<SnmpAsyncWalker<VarbindCollection>> callback,
      List<String> repeaters) {
    newWalk(repeaters).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void asyncWalk(SnmpCallback<SnmpAsyncWalker<VarbindCollection>> callback,
      String... repeaters) {
    newWalk(repeaters).invoke(callback);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> newGet(List<String> oids) {
    return new GetOperation(this, resolveOids(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> newGet(VarbindCollection varbinds) {
    return new GetOperation(this, resolveOids(varbinds));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> newGet(String... oids) {
    return newGet(Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> newSet(List<Varbind> varbinds) {
    return new SetOperation(this, resolveVarbinds(varbinds));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> newSet(VarbindCollection varbinds) {
    return new SetOperation(this, resolveVarbinds(varbinds));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> newSet(Varbind... varbinds) {
    return newSet(Arrays.asList(varbinds));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> newGetNext(List<String> oids) {
    return new GetNextOperation(this, resolveOids(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> newGetNext(
      VarbindCollection varbinds) {
    return new GetNextOperation(this, resolveOids(varbinds));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> newGetNext(String... oids) {
    return newGetNext(Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<List<VarbindCollection>> newGetBulk(int nonRepeaters,
      int maxRepetitions, List<String> oids) {
    return new GetBulkOperation(this, resolveOids(oids), nonRepeaters, 
        maxRepetitions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<List<VarbindCollection>> newGetBulk(int nonRepeaters,
      int maxRepetitions, VarbindCollection varbinds) {
    return new GetBulkOperation(this, resolveOids(varbinds), nonRepeaters,
        maxRepetitions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<List<VarbindCollection>> newGetBulk(int nonRepeaters,
      int maxRepetitions, String... oids) {
    return newGetBulk(nonRepeaters, maxRepetitions, Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpAsyncWalker<VarbindCollection> newWalk(int nonRepeaters,
      List<String> oids) {
    return new GetBulkAsyncWalker(this, resolveOids(oids), nonRepeaters, 
        config.getWalkMaxRepetitions());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpAsyncWalker<VarbindCollection> newWalk(int nonRepeaters,
      String... oids) {
    return newWalk(nonRepeaters, Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpAsyncWalker<VarbindCollection> newWalk(List<String> nonRepeaters,
      List<String> repeaters) {
    final int size = nonRepeaters.size() + repeaters.size();
    List<String> oids = new ArrayList<String>(size);
    oids.addAll(nonRepeaters);
    oids.addAll(repeaters);
    return newWalk(nonRepeaters.size(), oids);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpAsyncWalker<VarbindCollection> newWalk(List<String> repeaters) {
    return newWalk(0, repeaters);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpAsyncWalker<VarbindCollection> newWalk(String... repeaters) {
    return newWalk(0, Arrays.asList(repeaters));
  }

  private VariableBinding[] resolveOids(List<String> oids) {
    VariableBinding[] resolvedOids = new VariableBinding[oids.size()];
    for (int i = 0; i < oids.size(); i++) {
      resolvedOids[i] = resolveOid(oids.get(i));
    }
    return resolvedOids;
  }

  private VariableBinding[] resolveOids(VarbindCollection varbinds) {
    VariableBinding[] resolvedOids = new VariableBinding[varbinds.size()];
    for (int i = 0; i < varbinds.size(); i++) {
      Varbind varbind = varbinds.get(i);
      resolvedOids[i] = new VariableBinding(varbind instanceof Snmp4jVarbind ?
          ((Snmp4jVarbind) varbind).getDelegate().getOid() : new OID(varbind.getOid()));
    }
    return resolvedOids;
  }

  private VariableBinding[] resolveVarbinds(List<Varbind> varbinds) {
    VariableBinding[] resolvedVarbinds = new VariableBinding[varbinds.size()];
    for (int i = 0; i < varbinds.size(); i++) {
      resolvedVarbinds[i] = resolveVarbind(varbinds.get(i));
    }
    return resolvedVarbinds;
  }

  private VariableBinding[] resolveVarbinds(VarbindCollection varbinds) {
    VariableBinding[] resolvedVarbinds = new VariableBinding[varbinds.size()];
    for (int i = 0; i < varbinds.size(); i++) {
      Varbind varbind = varbinds.get(i);
      if (varbind instanceof Snmp4jVarbind) {
        VariableBinding vb = ((Snmp4jVarbind) varbind).getDelegate();
        resolvedVarbinds[i] = new VariableBinding(vb.getOid());
        resolvedVarbinds[i].setVariable(vb.getVariable());
      }
      else {
        resolvedVarbinds[i] = resolveVarbind(varbind);
      }
    }
    return resolvedVarbinds;
  }

  private VariableBinding resolveVarbind(Varbind varbind) {
    VariableBinding vb = resolveOid(varbind.getOid());
    vb.setVariable(Snmp4jVarbind.newVariable(varbind.getSyntax(), varbind.toObject()));
    return vb;
  }

  private VariableBinding resolveOid(String oid) {
    if (!OID_PATTERN.matcher(oid).matches() && mib != null) {
      String resolvedOid = mib.nameToOid(oid);
      if (resolvedOid == null) {
        throw new IllegalArgumentException("'" + oid + "' cannot be resolved");
      }
      oid = resolvedOid;
    }
    return new VariableBinding(new OID(oid));
  }

}
