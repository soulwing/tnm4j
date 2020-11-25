FROM debian:buster

RUN apt-get update && \
    apt-get install -y snmpd snmp snmptrapd

COPY snmpd.conf /etc/snmp/snmpd.conf

CMD ["/usr/sbin/snmpd", "-f", "-V", "-Lo", "-u", "Debian-snmp", "-g", "Debian-snmp", "-I", "-smux,mteTrigger,mteTriggerConf"]
