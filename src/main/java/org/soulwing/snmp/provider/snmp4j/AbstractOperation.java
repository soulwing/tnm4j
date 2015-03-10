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

import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.soulwing.snmp.SnmpCallback;
import org.soulwing.snmp.SnmpEvent;
import org.soulwing.snmp.SnmpException;
import org.soulwing.snmp.SnmpOperation;
import org.soulwing.snmp.SnmpResponse;
import org.soulwing.snmp.TimeoutException;
import org.soulwing.snmp.Varbind;

/**
 * An abstract base for {@link SnmpOperation} implementations.
 *
 * @author Carl Harris
 */
abstract class AbstractOperation<V> implements SnmpOperation<V>, 
    ResponseListener {

  protected final Snmp4jContext context;
  private final OID[] oids;  
    
  /**
   * Constructs a new instance.
   * @param context
   * @param oids
   */
  public AbstractOperation(Snmp4jContext context, OID[] oids) {
    this.oids = oids;
    this.context = context;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public void onResponse(ResponseEvent event) {
    PDU response = event.getResponse();
    SnmpCallback<V> callback = (SnmpCallback<V>) event.getUserObject();
    try {
      validateResponse(response);
      V result = createResult(response);
      callback.onSnmpResponse(new SnmpEvent<V>(context, 
          new SuccessResponse<V>(result)));
    }
    catch (RuntimeException ex) {
      callback.onSnmpResponse(new SnmpEvent<V>(context, 
          new ExceptionResponse<V>(ex)));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<V> invoke() throws SnmpException, TimeoutException {
    PDU request = createRequest(oids);
    try {
      ResponseEvent event = doInvoke(request);
      PDU response = event.getResponse();
      validateResponse(response);
      V result = createResult(response);
      return new SuccessResponse<V>(result);
    }
    catch (RuntimeException ex) {
      return new ExceptionResponse<V>(ex);
    }
    catch (IOException ex) {
      return new ExceptionResponse<V>(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void invoke(SnmpCallback<V> callback) {
    PDU request = createRequest(oids);
    try {
      doInvoke(request, callback);
    }
    catch (IOException ex) {
      callback.onSnmpResponse(new SnmpEvent<V>(context,
          new ExceptionResponse<V>(ex)));
    }
  }

  /**
   * 
   * @param oids
   * @return
   */
  protected PDU createRequest(OID... oids) {
    PDU pdu = context.getPduFactory().newPDU();
    for (OID oid : oids) {
      pdu.add(new VariableBinding(oid));
    }
    return pdu;
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
    // TODO handle noSuchInstance variable bindings
  }

  protected String objectNameToKey(Varbind v) {
    String name = v.getName();
    int index = name.indexOf('.');
    return index != -1 ? name.substring(0, index) : name;
  }
  
  /**
   * Invokes an operation on the associated remote agent using the given
   * request PDU and waits for the response.
   * @param request request PDU
   * @return response
   * @throws IOException
   */
  protected abstract ResponseEvent doInvoke(PDU request) throws IOException;
  
  /**
   * Invokes an asynchronous operation on the associated remote agent using 
   * the given request PDU.
   * <p>
   * The target {@link ResponseListener} for the request must be this
   * instance and subclasses must not override the {@link #onResponse(ResponseEvent)}
   * method without invoking the superclass implementation.
   * @param request request PDU
   * @param userObject user object that should be passed to the underlying
   *    asynchronous operation
   * @throws IOException
   */
  protected abstract void doInvoke(PDU request, Object userObject)
      throws IOException;

  /**
   * Creates a result object from the given response PDU.
   * @param response subject response
   * @return result object
   */
  protected abstract V createResult(PDU response);

}
