package org.soulwing.snmp.provider.snmp4j;

import org.snmp4j.smi.VariableBinding;
import org.soulwing.snmp.Varbind;

interface VarbindFactory {

  Varbind newVarbind(VariableBinding vb);
  
}
