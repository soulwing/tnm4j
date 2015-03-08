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

import org.snmp4j.CommunityTarget;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OctetString;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.SnmpV2cTarget;

/**
 * A {@link TargetStrategy} that creates a {@link CommunityTarget}.
 *
 * @author Carl Harris
 */
class CommunityTargetStrategy implements TargetStrategy {

  @Override
  public Target newTarget(SnmpTarget target) {
    if (!(target instanceof SnmpV2cTarget)) return null;
    CommunityTarget communityTarget = new CommunityTarget();
    communityTarget.setVersion(SnmpConstants.version2c);
    String community = ((SnmpV2cTarget) target).getCommunity();
    communityTarget.setCommunity(new OctetString(community));
    return communityTarget;
  }

}
