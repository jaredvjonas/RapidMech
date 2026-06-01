package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

/**
 * Relocates an inventory item to a different MountedLocation (keeping all its other fields).
 * Needed because IrianTech normalizes chassis to vanilla mechlab slots (e.g. 1 head slot),
 * so RogueTech loadouts that placed bulky gear in the 6-slot head must be moved elsewhere.
 */
public class MoveItem implements TaskCmd {
    @XmlAttribute
    public String item;      // ComponentDefID to relocate

    @XmlAttribute
    public String location;  // new MountedLocation
}
