package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

public class UpdateLocation implements TaskCmd {
    @XmlAttribute
    public String name;

    @XmlAttribute
    public String property;

    @XmlAttribute
    public String value;
}
