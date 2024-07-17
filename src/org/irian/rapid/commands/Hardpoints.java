package org.irian.rapid.commands;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import org.irian.rapid.commands.tasks.*;

import java.util.ArrayList;
import java.util.List;

public class Hardpoints {
    @XmlAttribute
    public String id;

    @XmlElements({
            @XmlElement(name = "add-hardpoint", type = AddHardpoint.class),
            @XmlElement(name = "set-hardpoint", type = SetHardpoint.class)
    })
    public List<TaskCmd> tasks = new ArrayList<>();
}
