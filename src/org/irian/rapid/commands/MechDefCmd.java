package org.irian.rapid.commands;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * source="${clanInvasion}/mech" dest="${target}/mech" tasks="mech-standard"
 */
public class MechDefCmd {
    @XmlAttribute
    public String source;

    @XmlAttribute
    public String dest;

    @XmlAttribute
    public String tasks;
}
