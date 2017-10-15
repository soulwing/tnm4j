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

import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.TSM;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.soulwing.snmp.SnmpTarget;
import org.soulwing.snmp.SnmpV3Target;

/**
 * A {@link TargetStrategy} that produces a {@link UserTarget}.
 *
 * @author Carl Harris
 */
class UserTargetStrategy implements TargetStrategy {
  private static final OctetString localEngineId = new OctetString(MPv3.createLocalEngineID());
  private static final USM usm = new USM(SecurityProtocols.getInstance(),
      localEngineId, 0);

  @Override
  public Target newTarget(SnmpTarget target) {
    if (!(target instanceof SnmpV3Target)) return null;
    SnmpV3Target v3Target = (SnmpV3Target) target;
    Assert.notNull(v3Target.getSecurityName(), "securityName is required");
    if (v3Target.getAuthPassphrase() != null) {
      Assert.notNull(v3Target.getAuthType(), "authType is required");
    }
    if (v3Target.getPrivPassphrase() != null){
      Assert.notNull(v3Target.getPrivType(), "privType is required");
    }
    
    OctetString securityName = new OctetString(v3Target.getSecurityName());
    SecurityModels.getInstance().addSecurityModel(usm);

    usm.addUser(new UsmUser(securityName, 
            authType(v3Target), new OctetString(v3Target.getAuthPassphrase()),
            privType(v3Target), new OctetString(v3Target.getPrivPassphrase())));    

    SecurityModels.getInstance().addSecurityModel(new TSM(localEngineId, false));
    UserTarget userTarget = new UserTarget();
    userTarget.setSecurityName(securityName);
    userTarget.setVersion(SnmpConstants.version3);

    if (v3Target.getAuthType() == null && v3Target.getPrivType() == null) {
      userTarget.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
    }
    else if (v3Target.getPrivType() == null) {
      userTarget.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
    }
    else if (v3Target.getAuthType() != null) {
      userTarget.setSecurityLevel(SecurityLevel.AUTH_PRIV);
    }
    else {
      throw new IllegalStateException(
          "cannot support privacy without authentication");
    }

    return userTarget;
  }

  private OID authType(SnmpV3Target v3Target) {
    switch (v3Target.getAuthType()) {
      case SHA:
        return AuthSHA.ID;
      case MD5:
        return AuthMD5.ID;
      default:
        throw new IllegalArgumentException("unrecognized auth type");
    }
  }
  
  private OID privType(SnmpV3Target v3Target) {
    switch (v3Target.getPrivType()) {
      case DES:
        return PrivDES.ID;
      case DES3:
        return Priv3DES.ID;
      case AES128:
        return PrivAES128.ID;
      case AES192:
        return PrivAES192.ID;
      case AES256:
        return PrivAES256.ID;
      default:
        throw new IllegalArgumentException("unrecognized privacy type");
    }
  }

}
