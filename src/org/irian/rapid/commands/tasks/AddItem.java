package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

public class AddItem implements TaskCmd {
    @XmlAttribute
    public String location;

    @XmlAttribute
    public String item;

    @XmlAttribute
    public String itemType;

    /** Prefab slot for the weapon model. Weapons want 0 (the mod-wide norm); equipment stays -1. */
    @XmlAttribute
    public int hardpointSlot = -1;
}
