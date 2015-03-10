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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.AbstractVariable;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.soulwing.snmp.Formatter;
import org.soulwing.snmp.IndexExtractor;
import org.soulwing.snmp.Mib;
import org.soulwing.snmp.MutableVarbindCollection;
import org.soulwing.snmp.SnmpConfiguration;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpOperation;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.TimeoutException;
import org.soulwing.snmp.TruncatedResponseException;
import org.soulwing.snmp.Varbind;
import org.soulwing.snmp.VarbindCollection;

class Snmp4jContext implements SnmpContext, VarbindFactory {

  private static final Pattern OID_PATTERN = Pattern.compile("^[0-9.]*$");
  
  private final SnmpTarget target;
  private final SnmpConfiguration config;
  private final Mib mib;
  private final Snmp snmp;
  private final Target snmp4jTarget;
  private final PduFactory pduFactory;

  
  public Snmp4jContext(SnmpTarget target, SnmpConfiguration config,
      Mib mib, Snmp snmp, Target snmp4jTarget, PduFactory pduFactory) {
    this.target = target;
    this.config = config;
    this.mib = mib;
    this.snmp = snmp;
    this.snmp4jTarget = snmp4jTarget;
    this.pduFactory = pduFactory;
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
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    Snmp4jContextFactory.dispose(this);
  }

    
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
    VariableBinding vb = new VariableBinding(resolvedOid, variable);
    return newVarbind(vb);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VarbindCollection get(List<String> oids) throws IOException {
    return new GetOperation(this, resolveOids(oids)).invoke().get();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VarbindCollection get(String... oids) 
      throws IOException {
    return get(Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VarbindCollection getNext(List<String> oids)
      throws IOException {
    return new GetNextOperation(this, resolveOids(oids)).invoke().get();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VarbindCollection getNext(String... oids)
      throws IOException {
    return getNext(Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VarbindCollection getBulk(int nonRepeaters, int maxRepetitions,
      List<String> oids) throws IOException {
    return new GetBulkOperation(this, resolveOids(oids), nonRepeaters, 
        maxRepetitions).invoke().get();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VarbindCollection getBulk(int nonRepeaters, int maxRepetitions,
      String... oids) throws IOException {
    return getBulk(nonRepeaters, maxRepetitions, Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<VarbindCollection> walk(int nonRepeaters, List<String> oids) 
      throws IOException {
    return doBulkWalk(nonRepeaters, resolveOids(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<VarbindCollection> walk(int nonRepeaters, String... oids) 
      throws IOException {
    return doBulkWalk(nonRepeaters, resolveOids(Arrays.asList(oids)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<VarbindCollection> walk(List<String> nonRepeaters,
      List<String> repeaters) throws IOException {
    List<String> oids = new ArrayList<String>(nonRepeaters.size() 
        + repeaters.size());
    oids.addAll(nonRepeaters);
    oids.addAll(repeaters);
    return doBulkWalk(nonRepeaters.size(), resolveOids(oids));
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<VarbindCollection> walk(List<String> repeaters)
      throws IOException {
    return doBulkWalk(0, resolveOids(repeaters));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<VarbindCollection> walk(String... repeaters)
      throws IOException {
    return doBulkWalk(0, resolveOids(Arrays.asList(repeaters)));
  }

  private PDU doGetBulk(int nonRepeaters, int maxRepetitions, OID... oids)
      throws IOException {
    PDU request = createRequest(oids);
    request.setNonRepeaters(nonRepeaters);
    request.setMaxRepetitions(maxRepetitions);
    ResponseEvent event = snmp.getBulk(request, snmp4jTarget);
    PDU response = event.getResponse();
    validateResponse(response);
    return response;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> asyncGet(List<String> oids) {
    return new GetOperation(this, resolveOids(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> asyncGet(String... oids) {
    return asyncGet(Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> asyncGetNext(List<String> oids) {
    return new GetNextOperation(this, resolveOids(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> asyncGetNext(String... oids) {
    return asyncGetNext(Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> asyncGetBulk(int nonRepeaters, 
      int maxRepetitions, List<String> oids) {
    return new GetBulkOperation(this, resolveOids(oids), nonRepeaters, 
        maxRepetitions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<VarbindCollection> asyncGetBulk(int nonRepeaters,
      int maxRepetitions, String... oids) {
    return asyncGetBulk(nonRepeaters, maxRepetitions, Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<List<VarbindCollection>> asyncWalk(int nonRepeaters,
      List<String> oids) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<List<VarbindCollection>> asyncWalk(int nonRepeaters,
      String... oids) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<List<VarbindCollection>> asyncWalk(List<String> nonRepeaters,
      List<String> repeaters) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<List<VarbindCollection>> asyncWalk(List<String> repeaters) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpOperation<List<VarbindCollection>> asyncWalk(String... repeaters) {
    // TODO Auto-generated method stub
    return null;
  }

  private void validateResponse(PDU response) {
    if (response == null) {
      throw new TimeoutException();
    }
    if (response.getErrorStatus() != 0) {
      throw new RuntimeException("response indicates " 
          + response.getErrorStatusText()
          + " at index " + response.getErrorIndex());
    }
  }
  
  private List<VarbindCollection> doBulkWalk(final int nonRepeaters, OID... oids) 
      throws IOException {
    OID[] nextOids = Arrays.copyOf(oids, oids.length);
    List<VarbindCollection> results = new LinkedList<VarbindCollection>();

    int repeaters = oids.length - nonRepeaters;
    if (repeaters == 0) {
      results.add(new GetNextOperation(this, oids).invoke().get());
      return results;
    }
  
    walk:
    while (true) {
      PDU response = doGetBulk(nonRepeaters, config.getWalkMaxRepetitions(), 
          nextOids);
      final int responseSize = response.size();
      
      if (responseSize <= nonRepeaters) {
        throw new TruncatedResponseException(
            "response contains no repeaters; too many non-repeaters?");
      }
      
      if (nonRepeaters + repeaters >= responseSize) {
        if (config.isWalkAllowsTruncatedRepetition()) {
          repeaters = responseSize - nonRepeaters;
        }
        else {
          throw new TruncatedResponseException(
              "response contains partial first repetition; "
              + "set walkAllowsTruncatedRepetition if you wish to allow it");
        }
      }
      
      if (endOfTable(oids, response, nonRepeaters, repeaters, nonRepeaters)) {
        break walk;
      }
      
      results.add(createRow(oids, response, nonRepeaters, repeaters, nonRepeaters));
      
      int offset = nonRepeaters + repeaters;
      while (offset + repeaters <= responseSize) {
        if (endOfTable(oids, response, nonRepeaters, repeaters, offset)) {
          break walk;
        }
        
        results.add(createRow(oids, response, nonRepeaters, repeaters, offset));
        offset += repeaters;
      }
     
      offset -= repeaters;
      
      if (nextOids.length > nonRepeaters + repeaters) {
        nextOids = new OID[nonRepeaters + repeaters];
        for (int i = 0; i < nonRepeaters; i++) {
          nextOids[i] = oids[i];
        }
      }
      for (int i = 0; i < repeaters; i++) {
        nextOids[i + nonRepeaters] = response.get(offset + i).getOid();
      }
    }
    return results;
  }
  
  private boolean endOfTable(OID[] oids, PDU response, int nonRepeaters, 
      int repeaters, int offset) {
    for (int i = 0; 
         offset + i < response.size() && i < repeaters; 
         i++) {
      if (response.get(offset + i).getOid().startsWith(oids[nonRepeaters + i])) {
        return false;
      }
    }
    
    return true;
  }
  
  protected PDU createRequest(OID... oids) {
    PDU pdu = pduFactory.newPDU();
    for (OID oid : oids) {
      pdu.add(new VariableBinding(oid));
    }
    return pdu;
  }

  protected OID[] resolveOids(List<String> oids) {
    OID[] resolvedOids = new OID[oids.size()];
    for (int i = 0; i < oids.size(); i++) {
      resolvedOids[i] = resolveOid(oids.get(i));
    }
    return resolvedOids;
  }

  protected OID resolveOid(String oid) {
    if (!OID_PATTERN.matcher(oid).matches() && mib != null) {
      oid = mib.nameToOid(oid);
    }
    return new OID(oid);
  }


  private VarbindCollection createRow(OID[] oids, PDU response, 
      int nonRepeaters, int repeaters, int offset) {
    final int responseSize = response.size();
    MutableVarbindCollection row = new MutableVarbindCollection();
    for (int i = 0; i < nonRepeaters; i++) {
      if (i < responseSize && response.get(i).getOid().startsWith(oids[i])) {
        Varbind v = newVarbind(response.get(i));
        row.add(i, objectNameToKey(v), v);
      }
    }
    if (repeaters > 0) {
      Varbind[] indexes = null;
      int count = 0;
      for (int i = 0; i < repeaters; i++) {
        if (i + offset < response.size() 
            && response.get(i + offset).getOid()
                  .startsWith(oids[i + nonRepeaters])) {
          Varbind v = newVarbind(response.get(i + offset));
          row.add(i, objectNameToKey(v), v);
          count++;
          if (indexes == null) {
            indexes = v.getIndexes();
          }
        }
      }
      for (int i = 0; i < indexes.length; i++) {
        row.add(count + i, objectNameToKey(indexes[i]), indexes[i]);
      }            
    }
    return row.immutableCopy();
  }

  private String objectNameToKey(Varbind v) {
    String name = v.getName();
    int index = name.indexOf('.');
    return index != -1 ? name.substring(0, index) : name;
  }
  
  /**
   * {@inheritDoc}
   */
  public Varbind newVarbind(VariableBinding vb) {
    String oid = vb.getOid().toString();
    String name = mib.oidToInstanceName(oid.toString());
    Formatter formatter = mib.newFormatter(oid);
    IndexExtractor indexExtractor = createIndexExtractor(oid);
    return new Snmp4jVarbind(name, vb, formatter, indexExtractor, this);
  }

  private IndexExtractor createIndexExtractor(String oid) {
    try {
      return mib.newIndexExtractor(oid);
    }
    catch (IllegalArgumentException ex) {
      return null;
    }
  }

}
