package org.irian.rapid.commands.tasks;


import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

public class RemoveItem implements TaskCmd {
    @XmlAttribute
    public String item;
}
