package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

/**
 * Moves an item from chassis FixedEquipment to mechdef inventory.
 * Use this when a fixed equipment item references a missing component
 * and needs to be replaced with a valid one in the mech's inventory.
 * By default, moves ALL matching items. Set all="false" to move only the first match.
 */
public class MoveFixedEquipment implements TaskCmd {
    @XmlAttribute
    public String item;

    @XmlAttribute
    public String with;

    @XmlAttribute
    public String itemType;

    @XmlAttribute
    public boolean all = true;  // default: move all matching items
}