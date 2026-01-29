package org.irian.rapid.commands;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import org.irian.rapid.commands.tasks.*;

import java.util.ArrayList;
import java.util.List;

public class TaskList {

    @XmlAttribute
    public String id;

    @XmlElements({
            // Chassis Def Commands
            @XmlElement(name = "update-location", type = UpdateLocation.class),
            @XmlElement(name = "swap-fixedEquipment", type = SwapFixedEquipment.class),
            @XmlElement(name = "remove-fixedEquipment", type = RemoveFixedEquipment.class),
            @XmlElement(name = "move-fixedEquipment", type = MoveFixedEquipment.class),
            @XmlElement(name = "swap-hardpoint", type = SwapHardpoint.class),

            // Mech Def Commands
            @XmlElement(name = "add-inventory", type = AddItem.class),
            @XmlElement(name = "remove-inventory", type = RemoveItem.class),
            @XmlElement(name = "swap-inventory", type = SwapItem.class),
            @XmlElement(name = "remove-engine", type = RemoveEngine.class),
            @XmlElement(name = "remove-gyro", type = RemoveGyro.class),
            @XmlElement(name = "remove-endosteel", type = RemoveEndoSteel.class),
            @XmlElement(name = "remove-quirk", type = RemoveQuirk.class),
            @XmlElement(name = "recalc-tonnage", type = RecalcTonnage.class),
            @XmlElement(name = "recalc-movement", type = RecalcMovement.class),
            @XmlElement(name = "recalc-cost", type = RecalcCost.class)
    })
    public List<TaskCmd> taskItems = new ArrayList<>();
}
