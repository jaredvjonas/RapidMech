package org.irian.rapid.commands;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * source="${clanInvasion}/chassis" dest="${target}/chassis" tasks="chassis-standard"
 */
public class ChassisDefCmd {
    @XmlAttribute
    public String source;

    @XmlAttribute
    public String dest;

    @XmlAttribute
    public String tasks;
}
