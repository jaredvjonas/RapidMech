package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

/**
 * Promotes a whitelisted RT chassis quirk from the source chassisdef's Custom.ChassisDefaults block into
 * chassis FixedEquipment, at the location RT gives it (TKT-138). Every other ChassisDefaults entry is still
 * dropped, as before. No-op on chassis that do not carry the item.
 */
public class PromoteChassisDefault implements TaskCmd {
    @XmlAttribute
    public String item;
}
