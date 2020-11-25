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

import static org.soulwing.snmp.provider.snmp4j.Snmp4jLogger.logger;

import java.io.IOException;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
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

  final Snmp4jContext context;
  final VariableBinding[] varbinds;

  /**
   * Constructs a new instance.
   * @param context
   * @param varbinds
   */
  AbstractOperation(Snmp4jContext context, VariableBinding[] varbinds) {
    this.varbinds = varbinds;
    this.context = context;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public void onResponse(ResponseEvent event) {
    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
    SnmpCallback<V> callback = (SnmpCallback<V>) event.getUserObject();
    try {
      validateResponse(event);
      V result = createResult(event.getResponse());
      try {
        callback.onSnmpResponse(new SnmpEvent<V>(context,
            new SuccessResponse<V>(result)));
      }
      catch (RuntimeException ex) {
        if (logger.isDebugEnabled()) {
          logger.error("callback threw an exception: " + ex, ex);
        }
        else {
          logger.warn("callback threw an exception: " + ex);
        }
      }
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
    PDU request = createRequest(varbinds);
    try {
      ResponseEvent event = doInvoke(request);
      validateResponse(event);
      V result = createResult(event.getResponse());
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
    PDU request = createRequest(varbinds);
    try {
      doInvoke(request, callback);
      if (logger.isDebugEnabled()) {
        logger.debug("sent request {}", request.getRequestID());
      }
    }
    catch (RuntimeException ex) {
      callback.onSnmpResponse(new SnmpEvent<V>(context,
          new ExceptionResponse<V>(ex)));
    }
    catch (IOException ex) {
      callback.onSnmpResponse(new SnmpEvent<V>(context,
          new ExceptionResponse<V>(ex)));
    }
  }

  protected PDU createRequest(VariableBinding[] varbinds) {
    PDU pdu = context.getPduFactory().newPDU();
    for (VariableBinding varbind : varbinds) {
      pdu.add(varbind);
    }
    return pdu;
  }

  protected void validateResponse(ResponseEvent event) {
    final Exception error = event.getError();
    if (error != null) {
      throw new SnmpException(error);
    }
    PDU response = event.getResponse();
    if (response == null) {
      throw new TimeoutException();
    }
    if (response.getErrorStatus() != 0) {
      throw new SnmpException("response indicates "
          + response.getErrorStatusText()
          + " at index " + response.getErrorIndex());
    }
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
