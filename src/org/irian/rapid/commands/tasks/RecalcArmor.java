package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

/**
 * Trims a mech's armor so its total weight fits the chassis tonnage, based on the actual
 * weight of the equipment it carries. Must run AFTER the inventory is finalized and after
 * recalc-tonnage (which sets InitialTonnage). `apply` names the resource-list of component
 * folders to scan for component tonnages (same list used by recalc-cost).
 */
public class RecalcArmor implements TaskCmd {
    @XmlAttribute
    public String apply;
}
