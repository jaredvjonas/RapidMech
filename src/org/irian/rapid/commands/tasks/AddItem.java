package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

public class AddItem implements TaskCmd {
    @XmlAttribute
    public String location;

    @XmlAttribute
    public String item;

    @XmlAttribute
    public String itemType;
}
