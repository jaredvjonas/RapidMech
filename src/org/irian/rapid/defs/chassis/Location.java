package org.irian.rapid.defs.chassis;

import java.util.List;

/**
 *     {
 *       "Location": "Head",
 *       "Hardpoints": [
 *         {
 *           "WeaponMountID": "Energy",
 *           "Omni": false
 *         }
 *       ],
 *       "Tonnage": 0,
 *       "InventorySlots": 6,
 *       "MaxArmor": 45,
 *       "MaxRearArmor": -1,
 *       "InternalStructure": 16
 *     },
 */
public class Location {
    public String Location;
    public List<Hardpoint> Hardpoints;
    public int Tonnage;
    public int InventorySlots;
    public int MaxArmor;
    public int MaxRearArmor;
    public int InternalStructure;
}
