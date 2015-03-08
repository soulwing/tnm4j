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


/**
 * A {@link SnmpV2cTarget} implemented as a simple configurable bean.
 *
 * @author Carl Harris
 */
public class SimpleSnmpV2cTarget extends SnmpTargetBase 
    implements SnmpV2cTarget {

  private String community;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getCommunity() {
    return community;
  }

  /**
   * Sets the {@code community} property.
   * @param community the value to set
   */
  public void setCommunity(String community) {
    this.community = community;
  }

}
