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

    /**
     * Task lists run AFTER the per-mech <hardpoints apply="..."> block, so they see the final
     * hardpoint set. `tasks` runs before it and would miss anything those blocks add.
     */
    @XmlAttribute
    public String postTasks;
}
