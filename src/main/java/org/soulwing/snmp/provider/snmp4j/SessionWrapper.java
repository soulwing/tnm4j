/*
 * File created on Apr 30, 2015
 *
 * Copyright (c) 2015 Carl Harris, Jr
 * and others as noted
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.soulwing.snmp.provider.snmp4j;

import static org.soulwing.snmp.provider.snmp4j.Snmp4jLogger.logger;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.soulwing.snmp.SnmpFactory;

/**
 * A wrapper for session that provides support for per-request timeout.
 *
 * @author Carl Harris
 */
class SessionWrapper implements Snmp4jSession {

  private final Snmp delegate;
  private final ScheduledExecutorService executorService;
  private final int retries;
  private final long timeout;

  /**
   * Constructs a new instance.
   * @param delegate tne SNMP session delegate
   * @param retries the number of additional attempts for each request when
   *    the first request times out
   * @param timeout the timeout delay for each request
   */
  public SessionWrapper(Snmp delegate, int retries, long timeout) {
    this(delegate, retries, timeout,
        SnmpFactory.getInstance().getScheduledExecutorService());
  }

  /**
   * Constructs a new instance.
   * @param delegate the SNMP session delegate
   * @param retries the number of additional attempts for each request when
   *    the first request times out
   * @param timeout the timeout delay for each request
   * @param executorService scheduled executor service to use in scheduling
   *    request timeouts
   */
  SessionWrapper(Snmp delegate, int retries, long timeout,
      ScheduledExecutorService executorService) {
    this.delegate = delegate;
    this.retries = retries;
    this.timeout = timeout;
    this.executorService = executorService;
  }

  @Override
  public void close() throws IOException {
    delegate.close();
  }

  @Override
  public ResponseEvent send(final PDU pdu, final Target target)
      throws IOException {
    return send(pdu, target, null);
  }

  @Override
  public void send(PDU pdu, Target target, Object userHandle,
      ResponseListener listener) throws IOException {
    send(pdu, target, null, userHandle, listener);
  }

  @Override
  public ResponseEvent send(final PDU pdu, final Target target,
      final TransportMapping transport) throws IOException {
    return new SynchronousRequest(pdu, target, transport, retries, timeout).get();
  }

  @Override
  public void send(PDU pdu, Target target, TransportMapping transport,
      Object userHandle, ResponseListener listener) throws IOException {
    new AsynchronousRequest(pdu, target, transport, userHandle, retries,
        timeout, listener).send();
  }

  @Override
  public void cancel(PDU request, ResponseListener listener) {
    delegate.cancel(request, listener);
  }

  @Override
  public ResponseEvent get(PDU pdu, Target target) throws IOException {
    pdu.setType(PDU.GET);
    return send(pdu, target);
  }

  @Override
  public void get(PDU pdu, Target target, Object userHandle,
      ResponseListener listener) throws IOException {
    pdu.setType(PDU.GET);
    send(pdu, target, userHandle, listener);
  }

  @Override
  public ResponseEvent set(PDU pdu, Target target) throws IOException {
    pdu.setType(PDU.SET);
    return send(pdu, target);
  }

  @Override
  public void set(PDU pdu, Target target, Object userHandle,
      ResponseListener listener) throws IOException {
    pdu.setType(PDU.SET);
    send(pdu, target, userHandle, listener);
  }

  @Override
  public ResponseEvent getNext(PDU pdu, Target target) throws IOException {
    pdu.setType(PDU.GETNEXT);
    return send(pdu, target);
  }

  @Override
  public void getNext(PDU pdu, Target target, Object userHandle,
      ResponseListener listener) throws IOException {
    pdu.setType(PDU.GETNEXT);
    send(pdu, target, userHandle, listener);
  }

  @Override
  public ResponseEvent getBulk(PDU pdu, Target target) throws IOException {
    pdu.setType(PDU.GETBULK);
    return send(pdu, target);
  }

  @Override
  public void getBulk(PDU pdu, Target target, Object userHandle,
      ResponseListener listener) throws IOException {
    pdu.setType(PDU.GETBULK);
    send(pdu, target, userHandle, listener);
  }

  abstract class AbstractRequest implements ResponseListener, Runnable {

    protected final PDU request;
    protected final Target target;
    protected final TransportMapping<?> transportMapping;
    protected final Object userHandle;

    private final long timeout;

    private int retries;

    private ScheduledFuture<?> future;

    protected ResponseEvent event;

    AbstractRequest(PDU request, Target target,
        TransportMapping<?> transportMapping, Object userHandle,
        int retries, long timeout) {
      this.request = request;
      this.target = target;
      this.transportMapping = transportMapping;
      this.userHandle = userHandle;
      this.retries = retries;
      this.timeout = timeout;
    }

    public final void send() {
      try {
        if (logger.isTraceEnabled()) {
          logger.trace("sending request");
        }
        future = executorService.schedule(this, timeout, TimeUnit.MILLISECONDS);
        delegate.send(request, target, transportMapping, userHandle, this);
      }
      catch (IOException ex) {
        onResponse(new ResponseEvent(delegate, target.getAddress(),
            request, null, userHandle, ex));
        future.cancel(false);
      }
    }

    @Override
    public void run() {
      timeout();
    }

    private void timeout() {
      cancel(request, this);
      if (retries == 0) {
        if (logger.isDebugEnabled()) {
          logger.debug("signaling timeout");
        }
        onResponse(new ResponseEvent(delegate, target.getAddress(),
            request, null, userHandle));
        return;
      }
      retries--;
      send();
    }

    @Override
    public void onResponse(ResponseEvent event) {
      if (logger.isTraceEnabled()) {
        logger.trace("response received");
      }
      future.cancel(false);
      cancel(request, this);
    }

  }

  class SynchronousRequest extends AbstractRequest {

    private final Lock lock = new ReentrantLock();
    private final Condition readyCondition = lock.newCondition();

    SynchronousRequest(PDU request, Target target,
        TransportMapping<?> transportMapping, int retries, long timeout) {
      super(request, target, transportMapping, null, retries, timeout);
    }

    public ResponseEvent get() throws IOException {
      send();
      lock.lock();
      try {
        while (event == null) {
          readyCondition.await();
        }
        if (event.getError() instanceof IOException) {
          throw (IOException) event.getError();
        }
        return event;
      }
      catch (InterruptedException ex) {
        // treat this as though a timeout occurred
        cancel(request, this);
        return new ResponseEvent(delegate, target.getAddress(),
            request, null, userHandle);
      }
      finally {
        lock.unlock();
      }
    }

    @Override
    public void onResponse(ResponseEvent event) {
      super.onResponse(event);
      lock.lock();
      try {
        this.event = event;
        readyCondition.signalAll();
      }
      finally {
        lock.unlock();
      }
    }

  }

  class AsynchronousRequest extends AbstractRequest {

    private final ResponseListener listener;

    AsynchronousRequest(PDU request, Target target,
        TransportMapping<?> transportMapping, Object userHandle,
        int retries, long timeout, ResponseListener listener) {
      super(request, target, transportMapping, userHandle, retries, timeout);
      this.listener = listener;
    }

    @Override
    public void onResponse(ResponseEvent event) {
      super.onResponse(event);
      listener.onResponse(event);
    }

  }

}
