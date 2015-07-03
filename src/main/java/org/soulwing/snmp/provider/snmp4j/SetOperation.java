/*
 * File created on Jul 3, 2015
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

import java.io.IOException;

import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;

/**
 * An SNMP SET operation.
 *
 * @author Carl Harris
 */
class SetOperation extends VarbindCollectionOperation {

  /**
   * Constructs a new instance.
   * @param context
   * @param varbinds
   */
  public SetOperation(Snmp4jContext context, VariableBinding[] varbinds) {
    super(context, varbinds);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ResponseEvent doInvoke(PDU request) throws IOException {
    return context.getSession().set(request, context.getSnmp4jTarget());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doInvoke(PDU request, Object userObject)
      throws IOException {
    context.getSession().set(request, context.getSnmp4jTarget(), userObject, this);
  }

}
