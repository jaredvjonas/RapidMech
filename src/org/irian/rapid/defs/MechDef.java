package org.irian.rapid.defs;

import org.irian.rapid.defs.mech.Description;
import org.irian.rapid.defs.mech.Inventory;
import org.irian.rapid.defs.mech.Location;
import org.irian.rapid.defs.shared.Tags;

import java.util.List;

public class MechDef {

    public Tags MechTags;

    public String ChassisID;

    public Description Description;

    public long simGameMechPartCost;

    public List<Location> Locations;

    public List<Inventory> inventory;

    public int armorValue() {
        int result = 0;
        for (var location : Locations) {
            result += location.AssignedArmor;
            result += location.AssignedRearArmor;
        }
        return result;
    }

    public Inventory findAttachment(String localGuid) {
        if (localGuid == null) return null;

        for (var item : inventory) {
            if (localGuid.equals(item.TargetComponentGUID)) {
                return item;
            }
        }
        return null;
    }
}
