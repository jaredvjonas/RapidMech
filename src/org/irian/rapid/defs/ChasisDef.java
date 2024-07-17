package org.irian.rapid.defs;

import org.irian.rapid.defs.chassis.*;
import org.irian.rapid.defs.shared.Tags;

import java.util.List;

public class ChasisDef {
    public CustomTags Custom;
    public CustomParts CustomParts;
    public List<Equipment> FixedEquipment;
    public Description Description;
    public String VariantName;
    public Tags ChassisTags;
    public String StockRole;
    public String YangsThoughts;
    public String MovementCapDefID;
    public String PathingCapDefID;
    public String HardpointDataDefID;
    public String PrefabIdentifier;
    public String PrefabBase;
    public float Tonnage;
    public float InitialTonnage;
    public String weightClass;
    public long BattleValue;
    public int Heatsinks;
    public int TopSpeed;
    public int TurnRadius;
    public int MaxJumpjets;
    public int Stability;
    public List<Integer> StabilityDefenses;
    public float SpotterDistanceMultiplier;
    public float VisibilityMultiplier;
    public float SensorRangeMultiplier;
    public float Signature;
    public int Radius;
    public boolean PunchesWithLeftArm;
    public int MeleeDamage;
    public int MeleeInstability;
    public float MeleeToHitModifier;
    public int DFADamage;
    public float DFAToHitModifier;
    public int DFASelfDamage;
    public int DFAInstability;
    public int MechPartMax;
    public int MechPartCount;
    public int EngageRangeModifier;
    public boolean AlwaysPunchIfPossible;
    public List<Location> Locations;
    public List<Coords> LOSSourcePositions;
    public List<Coords> LOSTargetPositions;
}
