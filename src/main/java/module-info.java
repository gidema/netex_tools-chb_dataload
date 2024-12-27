module nl.gertjanidema.netex.chb_dataload {
    requires java.desktop;
    requires spring.batch.core;
    requires spring.context;
    requires spring.beans;
    requires spring.batch.infrastructure;
    requires spring.core;
    requires spring.tx;
    requires jakarta.persistence;
    requires lombok;
    requires spring.data.commons;
    requires spring.oxm;
    requires jakarta.inject;
    requires nl.gertjanidema.netex.core;
    requires chbhaltebestand;
    requires passengerstopassignment;
    requires org.slf4j;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires org.apache.commons.net;
    requires spring.data.jpa;
    
    opens nl.gertjanidema.netex.chb_dataload to spring.core, spring.beans, spring.context;
    opens nl.gertjanidema.netex.chb_dataload.cli to spring.core, spring.beans, spring.context;
    opens nl.gertjanidema.netex.chb_dataload.jobs to spring.core, spring.beans, spring.context;
    opens nl.gertjanidema.netex.chb_dataload.ndov to spring.core, spring.beans, spring.context;
    opens nl.gertjanidema.netex.chb_dataload.dto;
}