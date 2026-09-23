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

    /**
     * Opt-in: after recalc-armor has worked out how many armor points the chassis tonnage
     * allows, spend ALL of them instead of only trimming an overweight mech down. Off by
     * default, so every existing mech line keeps the trim-only behaviour. Use it where a
     * conversion frees tonnage the source design had spent on gear IrianTech does not
     * model (e.g. an XXL engine and a bespoke gyro), which would otherwise be left idle.
     */
    @XmlAttribute
    public boolean maxArmor;


    @XmlElements({
            @XmlElement(name="add-hardpoint", type = AddHardpoint.class),
            @XmlElement(name="set-hardpoint", type = SetHardpoint.class),
            @XmlElement(name="omnify-hardpoints", type = OmnifyHardpoints.class),
            @XmlElement(name="swap-inventory", type = SwapItem.class),
            @XmlElement(name="add-inventory", type = AddItem.class),
            @XmlElement(name="remove-inventory", type = RemoveItem.class),
            @XmlElement(name="move-inventory", type = MoveItem.class),
            @XmlElement(name="move-fixedEquipment", type = MoveFixedEquipment.class),
            @XmlElement(name="replace-details", type = ReplaceDetails.class)
    })
    public List<TaskCmd> taskList = new ArrayList<>();
}
