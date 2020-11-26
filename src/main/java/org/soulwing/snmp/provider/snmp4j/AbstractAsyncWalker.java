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
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.soulwing.snmp.SnmpAsyncWalker;
import org.soulwing.snmp.SnmpCallback;
import org.soulwing.snmp.SnmpEvent;
import org.soulwing.snmp.SnmpException;
import org.soulwing.snmp.SnmpFactory;
import org.soulwing.snmp.SnmpResponse;
import org.soulwing.snmp.TimeoutException;
import org.soulwing.snmp.TruncatedResponseException;
import org.soulwing.snmp.WouldBlockException;

/**
 * An abstract base {@link SnmpAsyncWalker} implementations.
 *
 * @author Carl Harris
 */
abstract class AbstractAsyncWalker<V> 
    extends AbstractOperation<SnmpAsyncWalker<V>>
    implements SnmpAsyncWalker<V> {

  private final Lock lock = new ReentrantLock();
  
  final int nonRepeaters;
  final int maxRepetitions;
  final VariableBinding[] requestedVarbinds;
  
  private int repeaters;
  private PDU response;
  private int offset;

  
  /**
   * Constructs a new instance.
   * @param context
   * @param varbinds
   * @param nonRepeaters
   * @param maxRepetitions
   */
  protected AbstractAsyncWalker(Snmp4jContext context,
      VariableBinding[] varbinds, int nonRepeaters, int maxRepetitions) {
    super(context, varbinds);
    this.nonRepeaters = nonRepeaters;
    this.maxRepetitions = maxRepetitions;
    this.repeaters = varbinds.length - nonRepeaters;
    this.requestedVarbinds = Arrays.copyOf(varbinds, varbinds.length);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public void onResponse(ResponseEvent event) {
    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
    final SnmpCallback<SnmpAsyncWalker<V>> callback =
        (SnmpCallback<SnmpAsyncWalker<V>>) event.getUserObject();

    lock.lock();
    try {
      this.offset = 0;
      validateResponse(event);
      /*
       * Since we're in a response handler, we need to dispatch the callback
       * on another thread, so that it can invoke another request if needed.
       */
      SnmpFactory.getInstance().getExecutorService().execute(new Runnable() {
        @Override
        public void run() {
          try {
            response = event.getResponse();
            callback.onSnmpResponse(new SnmpEvent<SnmpAsyncWalker<V>>(context,
                new SuccessResponse<SnmpAsyncWalker<V>>(AbstractAsyncWalker.this)));
          }
          catch (WouldBlockException ex) {
            invoke(callback);
          }
          catch (RuntimeException ex) {
            callback.onSnmpResponse(new SnmpEvent<SnmpAsyncWalker<V>>(context,
                new ExceptionResponse<SnmpAsyncWalker<V>>(ex)));
          }
        }
      });
    }
    catch (SnmpException ex) {
      callback.onSnmpResponse(new SnmpEvent<SnmpAsyncWalker<V>>(context,
          new ExceptionResponse<SnmpAsyncWalker<V>>(ex)));
    }
    finally {
      lock.unlock();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<SnmpAsyncWalker<V>> invoke() throws SnmpException,
      TimeoutException {
    PDU request = createRequest(varbinds);
    try {
      ResponseEvent event = doInvoke(request);
      validateResponse(event);
      this.response = event.getResponse();
      this.offset = 0;
      return new SuccessResponse<SnmpAsyncWalker<V>>(this);
    }
    catch (RuntimeException ex) {
      return new ExceptionResponse<SnmpAsyncWalker<V>>(ex);
    }
    catch (IOException ex) {
      return new ExceptionResponse<SnmpAsyncWalker<V>>(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void invoke(SnmpCallback<SnmpAsyncWalker<V>> callback) {
    lock.lock();
    try {
      super.invoke(callback);
    }
    finally {
      lock.unlock();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateResponse(ResponseEvent event) {
    super.validateResponse(event);
    PDU response = event.getResponse();
    final int responseSize = response.size();
    
    if (responseSize <= nonRepeaters) {
      throw new TruncatedResponseException(
          "response contains no repeaters; too many non-repeaters?");
    }
    
    if (nonRepeaters + repeaters > responseSize) {
      if (context.getConfig().isWalkAllowsTruncatedRepetition()) {
        this.repeaters = responseSize - nonRepeaters;
      }
      else {
        throw new TruncatedResponseException(
            "response contains partial first repetition; "
            + "set walkAllowsTruncatedRepetition if you wish to allow it");
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected abstract ResponseEvent doInvoke(PDU request) throws IOException;

  /**
   * {@inheritDoc}
   */
  @Override
  protected abstract void doInvoke(PDU request, Object userObject) 
      throws IOException;

  /**
   * {@inheritDoc}
   */
  @Override
  protected SnmpAsyncWalker<V> createResult(PDU response) {
    throw new UnsupportedOperationException();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public SnmpResponse<V> next() throws WouldBlockException {
    lock.lock();
    try {
      int offset = this.offset == 0 ? nonRepeaters : this.offset;
  
      if (endOfTable(offset)) {
        return new SuccessResponse<V>(null);
      }
  
      SnmpResponse<V> response = new SuccessResponse<V>(
          createRow(this.response, nonRepeaters, repeaters, offset));
     
      this.offset = offset + repeaters;
     
      return response;
    }
    finally {
      lock.unlock();
    }
  }

  private boolean endOfTable(int offset) {
    if (response == null) {
      throw new WouldBlockException();
    }
    int i = 0;
    while (offset + i < response.size() && i < repeaters) {
      OID oid = response.get(offset + i).getOid();
      if (!oid.startsWith(requestedVarbinds[nonRepeaters + i].getOid())) {
        return true;
      }
      i++;
    }
    if (i < repeaters) {
      setNextOids(offset - repeaters);
      throw new WouldBlockException();
    }
    return false;
  }
  
  private void setNextOids(int offset) {
    for (int i = 0; i < repeaters; i++) {
      OID oid = response.get(offset + i).getOid();
      varbinds[i + nonRepeaters] = new VariableBinding(oid);
    }
  }

  protected abstract V createRow(PDU response, int nonRepeaters,
      int repeaters, int offset);

}
