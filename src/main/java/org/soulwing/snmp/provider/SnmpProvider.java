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
package org.soulwing.snmp.provider;

import org.soulwing.snmp.Mib;
import org.soulwing.snmp.SnmpContext;
import org.soulwing.snmp.SnmpFactoryConfig;
import org.soulwing.snmp.SnmpListener;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.SnmpTargetConfig;

/**
 * A provider of {@link SnmpContext} objects.
 *
 * @author Carl Harris
 */
public interface SnmpProvider {

  /**
   * Gets the provider name.
   * @return provider name (never {@code null})
   */
  String getName();

  /**
   * Initializes this provider instance.
   * <p>
   * This method is invoked once, after the factory has been instantiated,
   * before the provider is called upon to create contexts or listeners.
   * @param config configuration associated with the caling factory
   */
  void init(SnmpFactoryConfig config);

  /**
   * Creates a new SNMP context.
   * @param target target agent
   * @param config context configuration; the provider can safely
   *    assume that the configuration is immutable for the lifetime of the
   *    returned context
   * @param mib MIB provider
   * @return SNMP context
   */
  SnmpContext newContext(SnmpTarget target, SnmpTargetConfig config,
      Mib mib);

  /**
   * Creates a new listener that will receive and distribute SNMP notifications
   * (traps, informs).
   * @param address address on which to listen ({@code null} to indicate ANY)
   * @param port port on which to listen
   * @param mib MIB provider
   * @return listener object
   */
  SnmpListener newListener(String address, int port, Mib mib);

  /**
   * Notifies the recipient that the {@link org.soulwing.snmp.SnmpFactory}
   * has been closed.
   * <p>
   * The provider should release any resources such as threads, locks, open
   * files/sockets, etc when this method is invoked.
   */
  void close();

}
