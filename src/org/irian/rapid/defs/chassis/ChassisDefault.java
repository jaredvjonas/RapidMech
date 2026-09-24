package org.irian.rapid.defs.chassis;

import java.util.List;

/**
 * One entry of an RT chassisdef's CustomComponents "ChassisDefaults" block - the chassis quirk parts RT
 * installs as the default for a slot category (cockpit, FCS, sensors, gyro, armor type...):
 *
 *   "ChassisDefaults": [
 *     {
 *       "CategoryID": "CockpitSensors",
 *       "Defaults": [ { "Location": "Head", "DefID": "Unique_Sensors_Totem", "Type": "Upgrade" } ]
 *     }
 *   ]
 *
 * Read-only: IrianTech registers no CC slot categories, so the block itself is never written back out.
 * Whitelisted entries are promoted to plain FixedEquipment by the promote-chassis-default task (TKT-138).
 */
public class ChassisDefault {
    public String CategoryID;
    public List<Entry> Defaults;

    public static class Entry {
        public String Location;
        public String DefID;
        public String Type;
    }
}
