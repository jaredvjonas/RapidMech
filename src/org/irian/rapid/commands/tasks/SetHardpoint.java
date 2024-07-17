package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

public class SetHardpoint implements TaskCmd {
    @XmlAttribute
    public String location;

    @XmlAttribute
    public String weaponMounts; // comma separated list
}
