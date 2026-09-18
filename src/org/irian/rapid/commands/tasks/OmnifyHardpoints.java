package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

/**
 * Converts typed weapon hardpoints into omni pods (Omni = true) on an OmniMech chassis.
 *
 * RT omni chassis carry a mix of omni pods and plain typed mounts; CustomComponents swaps a pod's
 * nominal type for weapon category 1000 "Omni" (accepts Ballistic/Energy/Missile/AntiPersonnel),
 * but leaves the typed mounts alone. On a chassis whose configs never carry that weapon type the
 * typed mounts are dead capacity and the MechLab shows hardpoints the loadout does not match.
 *
 * AntiPersonnel is excluded by default: a pod already accepts AP weapons, so the dedicated AP
 * mounts stay as guaranteed point-defence slots on top of the pods.
 *
 * TOPS UP rather than adds: a hardpoint already flagged Omni is left alone, and no hardpoint is
 * ever created or removed, so this is idempotent across regens and weapon capacity is unchanged.
 */
public class OmnifyHardpoints implements TaskCmd {
    /** Locations to convert; empty means every location on the chassis. */
    @XmlAttribute
    public String locations = "";

    /** Hardpoint types eligible for conversion. AntiPersonnel is deliberately absent. */
    @XmlAttribute
    public String weaponMounts = "Ballistic,Energy,Missile";
}
