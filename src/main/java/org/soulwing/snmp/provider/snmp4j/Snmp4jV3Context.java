package org.soulwing.snmp.provider.snmp4j;

import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
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
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.soulwing.snmp.MIB;
import org.soulwing.snmp.SNMPv3Context;

class Snmp4jV3Context extends Snmp4jContext implements SNMPv3Context {

  
  private final USM usm = new USM(SecurityProtocols.getInstance(),
      new OctetString(MPv3.createLocalEngineID()), 0);

  private String securityName;
  private AuthType authType;
  private String authPassphrase;
  private PrivType privType;
  private String privPassphrase;
  private String scope;
  
  /**
   * Constructs a new instance.
   * @param mib
   */
  public Snmp4jV3Context(MIB mib) {
    super(mib);
    SecurityModels.getInstance().addSecurityModel(usm);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSecurityName() {
    return securityName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSecurityName(String securityName) {
    this.securityName = securityName;
    reconfigure();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AuthType getAuthType() {
    return authType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAuthType(AuthType type) {
    this.authType = type;
    reconfigure();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAuthPassphrase() {
    return authPassphrase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAuthPassphrase(String passphrase) {
    this.authPassphrase = passphrase;
    reconfigure();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PrivType getPrivType() {
    return privType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPrivType(PrivType type) {
    this.privType = type;
    reconfigure();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrivPassphrase() {
    return privPassphrase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPrivPassphrase(String passphrase) {
    this.privPassphrase = passphrase;
    reconfigure();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getScope() {
    return scope;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setScope(String scope) {
    this.scope = scope;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  protected Target createTarget() {
    assertNotNull(securityName, "securityName is required");
    if (authPassphrase != null) {
      assertNotNull(authType, "authType is required");
    }
    if (privPassphrase != null){
      assertNotNull(privType, "privType is required");
    }
    
    OctetString securityName = new OctetString(getSecurityName());
    
    usm.setUsers(new UsmUser[] {
        new UsmUser(securityName, 
            authType(), new OctetString(getAuthPassphrase()),
            privType(), new OctetString(getPrivPassphrase()))});    

    UserTarget target = new UserTarget();
    target.setSecurityName(securityName);
    target.setVersion(SnmpConstants.version3);

    if (getAuthType() == null && getPrivType() == null) {
      target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
    }
    else if (getPrivType() == null) {
      target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
    }
    else if (getAuthType() != null) {
      target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
    }
    else {
      throw new IllegalStateException(
          "cannot support privacy without authentication");
    }

    return target;
  }
  
  private OID authType() {
    switch (authType) {
      case SHA:
        return AuthSHA.ID;
      case MD5:
        return AuthMD5.ID;
      default:
        throw new IllegalArgumentException("unrecognized auth type");
    }
  }
  
  private OID privType() {
    switch (privType) {
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
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected PDU createPDU() {
    ScopedPDU pdu = new ScopedPDU();
    if (scope != null) {
      pdu.setContextName(new OctetString(scope));
    }
    return pdu;
  }

}
