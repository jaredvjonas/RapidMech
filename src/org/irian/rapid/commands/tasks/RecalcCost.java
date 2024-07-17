package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

public class RecalcCost implements TaskCmd {
    @XmlAttribute
    public String apply;
}
