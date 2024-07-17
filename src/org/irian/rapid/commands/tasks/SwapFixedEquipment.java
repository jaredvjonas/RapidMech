package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

public class SwapFixedEquipment implements TaskCmd {
    @XmlAttribute
    public String item;

    @XmlAttribute
    public String with;
}
