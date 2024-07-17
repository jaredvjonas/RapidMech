package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

public class SwapHardpoint implements TaskCmd {
    @XmlAttribute
    public String weaponMount;

    @XmlAttribute
    public String with;
}
