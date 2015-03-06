package org.soulwing.snmp.provider.snmp4j;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OctetString;
import org.soulwing.snmp.MIB;
import org.soulwing.snmp.SNMPv2cContext;

class Snmp4jV2cContext extends Snmp4jContext implements SNMPv2cContext {

  private String community;
  
  /**
   * Constructs a new instance.
   * @param mib
   */
  public Snmp4jV2cContext(MIB mib) {
    super(mib);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCommunity() {
    return community;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCommunity(String securityName) {
    this.community = securityName;
    reconfigure();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Target createTarget() {
    CommunityTarget target = new CommunityTarget();
    target.setVersion(SnmpConstants.version2c);
    target.setCommunity(new OctetString(getCommunity()));
    return target;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PDU createPDU() {
    return new PDU();
  }

}
