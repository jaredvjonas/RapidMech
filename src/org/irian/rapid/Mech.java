package org.irian.rapid;

import org.irian.rapid.commands.MechCmd;
import org.irian.rapid.core.ArmorType;
import org.irian.rapid.core.EngineType;
import org.irian.rapid.core.InternalStructureType;
import org.irian.rapid.core.TechnologyBase;
import org.irian.rapid.core.rules.FusionEngine;
import org.irian.rapid.core.rules.InternalStructure;
import org.irian.rapid.defs.ChasisDef;
import org.irian.rapid.defs.MechDef;

public class Mech {

    public MechDef mechDef;
    public ChasisDef chasisDef;

    public int engineRating;
    public EngineType engineType = EngineType.Fusion_Standard;
    public InternalStructureType internalType = InternalStructureType.Standard;

    public ArmorType armorType = ArmorType.Standard;

    public TechnologyBase techBase = TechnologyBase.InnerSphere;

    public Mech(MechCmd mechCmd, MechDef mechDef, ChasisDef chassisDef) {
        this.engineRating = mechCmd.engineRating;
        this.engineType = buildEngineType(mechCmd.engineType);
        this.internalType = mechCmd.endoSteel ? InternalStructureType.EndoSteel : InternalStructureType.Standard;
        this.armorType = mechCmd.ferroFibrous ? ArmorType.FerroFibrous : ArmorType.Standard;
        this.mechDef = mechDef;
        this.chasisDef = chassisDef;
        this.techBase = (mechCmd.techBase != null && mechCmd.techBase.equals("Clan")) ? TechnologyBase.Clan : TechnologyBase.InnerSphere;
    }

    private EngineType buildEngineType(String type) {
        return switch(type) {
            case "compact" -> EngineType.Fusion_Compact;
            case "standard" -> EngineType.Fusion_Standard;
            case "light" -> EngineType.Fusion_Light;
            case "XL" -> EngineType.Fusion_XL;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public void recalculateTonnage() {
        InternalStructure is = InternalStructure.find((int)chasisDef.Tonnage);
        FusionEngine engine = FusionEngine.find(engineRating);

        float internalWeight = (internalType == InternalStructureType.EndoSteel) ? is.EndoSteel : is.Standard;
        float engineWeight = engineType.getWeight(engine);
        float gyroWeight = engine.Gyro;

        chasisDef.InitialTonnage = internalWeight + engineWeight + gyroWeight;
    }

    public void recalculateMovement() {
        int mp = (int) (engineRating / chasisDef.Tonnage); // calculate movement points
        chasisDef.TopSpeed = mp * 30; // calculate TopSpeed in meters
    }

    public void recalculateCost(ResourceScanner resourceScanner) {
        var costEstimator = new CostEstimator(resourceScanner);
        costEstimator.calculateCost(this);
    }
}
