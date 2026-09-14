package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

/**
 * Guarantees a minimum spread of AntiPersonnel hardpoints so AMS and support weapons can be
 * mounted in more than one place. Most RT-imported chassis ship with a single AP hardpoint (often
 * only in the head), which leaves no real choice about where point defence goes.
 *
 * Tech base drives the spread: Clan chassis (ChassisTag "ClanMech") get AP across every major
 * weapon-bearing location, Inner Sphere chassis get the torso subset. Legs are never touched.
 *
 * This TOPS UP rather than adds: a location that already has an AntiPersonnel hardpoint is left
 * alone, so it is idempotent across regens and never stacks duplicates.
 */
public class EnsureApHardpoints implements TaskCmd {
    /** Locations guaranteed an AP hardpoint on Clan chassis. */
    @XmlAttribute
    public String clanLocations = "Head,LeftArm,LeftTorso,CenterTorso,RightTorso,RightArm";

    /** Locations guaranteed an AP hardpoint on Inner Sphere chassis. */
    @XmlAttribute
    public String isLocations = "LeftTorso,CenterTorso,RightTorso";
}
