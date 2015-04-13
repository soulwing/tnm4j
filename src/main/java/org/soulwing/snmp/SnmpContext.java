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
package org.soulwing.snmp;

import java.io.Closeable;


/**
 * A context for performing SNMP operations with an SNMP agent.
 * <p>
 * A context is a lightweight object that represents a single SNMP agent
 * on a network.  Implementations may be designed such that a single 
 * provider object is shared by many context objects.
 * <p>
 * A context is obtained from an {@link SnmpFactory}.  When the context
 * is no longer used, the {@link SnmpContext#close()} method should be
 * called to release any provider resources associated with the context.
 *
 * @author Carl Harris
 */
public interface SnmpContext extends SnmpOperationFactory,
    SnmpOperations, SnmpAsyncOperations, Closeable {

  /**
   * Gets the target of this context.
   * @return target
   */
  SnmpTarget getTarget();
  
  /**
   * Gets the MIB associated with this context.
   * @return MIB
   */
  Mib getMib();

  /**
   * Closes this context, releasing any provider resources associated
   * with it.
   */
  void close();
  
  /**
   * Constructs a new {@link Varbind} suitable for use in operations
   * performed via this context.
   * @param oid MIB name or dotted-decimal OID
   * @param value value whose type is appropriate for the specified object
   *    identifier
   * @return bound instance of the given value
   */
  Varbind newVarbind(String oid, Object value);
  
}
