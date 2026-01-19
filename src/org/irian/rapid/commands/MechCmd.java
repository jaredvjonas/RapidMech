package org.irian.rapid.commands;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import org.irian.rapid.commands.tasks.*;

import java.util.ArrayList;
import java.util.List;

/**
 * model="anvil_ANV-3M" engineRating="300" engineType="standard" endoSteel="true"
 */
public class MechCmd {
    @XmlAttribute
    public String model;

    @XmlAttribute
    public int engineRating;

    @XmlAttribute
    public String engineType;

    @XmlAttribute
    public boolean endoSteel;

    @XmlAttribute
    public boolean ferroFibrous;

    @XmlAttribute
    public String techBase; // IS, Clan

    @XmlAttribute
    public String apply;

    @XmlAttribute
    public String tasks;

    @XmlElements({
            @XmlElement(name="add-hardpoint", type = AddHardpoint.class),
            @XmlElement(name="set-hardpoint", type = SetHardpoint.class),
            @XmlElement(name="swap-inventory", type = SwapItem.class),
            @XmlElement(name="add-inventory", type = AddItem.class),
            @XmlElement(name="remove-inventory", type = RemoveItem.class)
    })
    public List<TaskCmd> taskList = new ArrayList<>();
}
