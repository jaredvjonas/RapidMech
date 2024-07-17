package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

public class SwapItem implements TaskCmd {
    @XmlAttribute
    public String item;

    @XmlAttribute
    public String with;

    @XmlAttribute
    public String whenAttachment;

    @XmlAttribute
    public String itemType;
}
