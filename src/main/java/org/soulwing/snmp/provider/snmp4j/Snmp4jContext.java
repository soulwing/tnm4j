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
  private final Snmp snmp;
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
    this.snmp = snmp;
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
   * Gets the {@code snmp} property.
   * @return property value
   */
  public Snmp getSnmp() {
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
    OID resolvedOid = resolveOid(oid);
    int syntax = mib.syntaxForObject(resolvedOid.toString());
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

    return varbindFactory.newVarbind(new VariableBinding(resolvedOid, variable));
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
  public SnmpAsyncWalker<VarbindCollection> asyncWalk(int nonRepeaters,
      List<String> oids) {
    return new GetBulkAsyncWalker(this, resolveOids(oids), nonRepeaters, 
        config.getWalkMaxRepetitions());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpAsyncWalker<VarbindCollection> asyncWalk(int nonRepeaters,
      String... oids) {
    return asyncWalk(nonRepeaters, Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpAsyncWalker<VarbindCollection> asyncWalk(List<String> nonRepeaters,
      List<String> repeaters) {
    final int size = nonRepeaters.size() + repeaters.size();
    List<String> oids = new ArrayList<String>(size);
    oids.addAll(nonRepeaters);
    oids.addAll(repeaters);
    return asyncWalk(nonRepeaters.size(), oids);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpAsyncWalker<VarbindCollection> asyncWalk(List<String> repeaters) {
    return asyncWalk(0, repeaters);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpAsyncWalker<VarbindCollection> asyncWalk(String... repeaters) {
    return asyncWalk(0, Arrays.asList(repeaters));
  }

  protected OID[] resolveOids(List<String> oids) {
    OID[] resolvedOids = new OID[oids.size()];
    for (int i = 0; i < oids.size(); i++) {
      resolvedOids[i] = resolveOid(oids.get(i));
    }
    return resolvedOids;
  }

  protected OID[] resolveOids(VarbindCollection varbinds) {
    OID[] resolvedOids = new OID[varbinds.size()];
    for (int i = 0; i < varbinds.size(); i++) {
      Varbind varbind = varbinds.get(i);
      resolvedOids[i] = varbind instanceof Snmp4jVarbind ?
          ((Snmp4jVarbind) varbind).getDelegate().getOid() : new OID(varbind.getOid());
    }
    return resolvedOids;
  }

  protected OID resolveOid(String oid) {
    if (!OID_PATTERN.matcher(oid).matches() && mib != null) {
      String resolvedOid = mib.nameToOid(oid);
      if (resolvedOid == null) {
        throw new IllegalArgumentException("'" + oid + "' cannot be resolved");
      }
      oid = resolvedOid;
    }
    return new OID(oid);
  }

}
