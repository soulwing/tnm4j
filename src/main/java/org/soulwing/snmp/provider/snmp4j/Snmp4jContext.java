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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.soulwing.snmp.SnmpConfiguration;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.TimeoutException;
import org.soulwing.snmp.TruncatedResponseException;
import org.soulwing.snmp.Varbind;

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
  public List<Varbind> get(List<String> oids) throws IOException {
    PDU response = doGet(resolveOids(oids));
    return createList(response);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Varbind> get(String... oids) 
      throws IOException {
    return get(Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Varbind> getNext(List<String> oids)
      throws IOException {
    PDU response = doGetNext(resolveOids(oids));
    return createList(response);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Varbind> getNext(String... oids)
      throws IOException {
    return getNext(Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Varbind> getBulk(int nonRepeaters, int maxRepetitions,
      List<String> oids) throws IOException {
    PDU response = doGetBulk(nonRepeaters, maxRepetitions, 
        resolveOids(oids));
    return createList(response);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Varbind> getBulk(int nonRepeaters, int maxRepetitions,
      String... oids) throws IOException {
    return getBulk(nonRepeaters, maxRepetitions, Arrays.asList(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Map<String, Varbind>> walk(int nonRepeaters, List<String> oids) 
      throws IOException {
    return doBulkWalk(nonRepeaters, resolveOids(oids));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Map<String, Varbind>> walk(int nonRepeaters, String... oids) 
      throws IOException {
    return doBulkWalk(nonRepeaters, resolveOids(Arrays.asList(oids)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Map<String, Varbind>> walk(List<String> nonRepeaters,
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
  public List<Map<String, Varbind>> walk(List<String> repeaters)
      throws IOException {
    return doBulkWalk(0, resolveOids(repeaters));
  }

  private PDU doGet(OID... oids) throws IOException {
    PDU request = createRequest(oids);
    ResponseEvent event = snmp.get(request, snmp4jTarget);
    PDU response = event.getResponse();
    validateResponse(response);
    return response;
  }
  
  private PDU doGetNext(OID... oids) throws IOException {
    PDU request = createRequest(oids);
    ResponseEvent event = snmp.getNext(request, snmp4jTarget);
    PDU response = event.getResponse();
    validateResponse(response);
    return response;
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
  
  private List<Map<String, Varbind>> doBulkWalk(final int nonRepeaters, OID... oids) 
      throws IOException {
    OID[] nextOids = Arrays.copyOf(oids, oids.length);
    List<Map<String, Varbind>> results = new LinkedList<Map<String, Varbind>>();

    int repeaters = oids.length - nonRepeaters;
    if (repeaters == 0) {
      PDU response = doGetNext(nextOids);
      results.add(createRow(oids, response, nonRepeaters, 0, 0));
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
  
  private PDU createRequest(OID... oids) {
    PDU pdu = pduFactory.newPDU();
    for (OID oid : oids) {
      pdu.add(new VariableBinding(oid));
    }
    return pdu;
  }

  private OID[] resolveOids(List<String> oids) {
    OID[] resolvedOids = new OID[oids.size()];
    for (int i = 0; i < oids.size(); i++) {
      resolvedOids[i] = resolveOid(oids.get(i));
    }
    return resolvedOids;
  }

  private OID resolveOid(String oid) {
    if (!OID_PATTERN.matcher(oid).matches() && mib != null) {
      oid = mib.nameToOid(oid);
    }
    return new OID(oid);
  }

  private List<Varbind> createList(PDU response) {
    List<Varbind> results = new ArrayList<Varbind>();
    for (int i = 0; i < response.size(); i++) {
      results.add(newVarbind(response.get(i)));
    }
    return results;
  }

  private Map<String, Varbind> createRow(OID[] oids, PDU response, 
      int nonRepeaters, int repeaters, int offset) {
    final int responseSize = response.size();
    Map<String, Varbind> row = new LinkedHashMap<String, Varbind>();
    for (int i = 0; i < nonRepeaters; i++) {
      if (i < responseSize && response.get(i).getOid().startsWith(oids[i])) {
        Varbind v = newVarbind(response.get(i));
        row.put(objectNameToKey(v), v);
      }
    }
    if (repeaters > 0) {
      boolean haveIndexes = false;
      for (int i = 0; i < repeaters; i++) {
        if (i + offset < response.size() 
            && response.get(i + offset).getOid()
                  .startsWith(oids[i + nonRepeaters])) {
          Varbind v = newVarbind(response.get(i + offset));
          row.put(objectNameToKey(v), v);
          if (!haveIndexes) {
            haveIndexes = true;
            Varbind[] indexes = v.getIndexes();
            for (int j = 0; j < indexes.length; j++) {
              row.put(objectNameToKey(indexes[j]), indexes[j]);
            }            
          }
        }
      }
    }
    return row;
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
