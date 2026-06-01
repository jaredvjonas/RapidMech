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
import org.irian.rapid.defs.ReaderWriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mech {

    // Game weight model: 1 armor point = 0.0125 tons (MechStatisticsConstants).
    private static final double TONNAGE_PER_ARMOR_POINT = 0.0125;
    // RogueTech fake/variant hardpoint markers and DLC stubs use a sentinel tonnage (999);
    // the game does not count them toward weight, so neither do we.
    private static final double SENTINEL_TONNAGE = 100.0;
    private static final Map<String, Double> tonnageCache = new HashMap<>();

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

        // The hand-typed engine/structure/armor attributes are error-prone. Override them with
        // what the source mech actually carries, so InitialTonnage is computed correctly.
        autoDetectFromSource();
    }

    private EngineType buildEngineType(String type) {
        if (type == null) {
            return EngineType.Fusion_Standard;
        }
        return switch(type) {
            case "compact" -> EngineType.Fusion_Compact;
            case "standard" -> EngineType.Fusion_Standard;
            case "light" -> EngineType.Fusion_Light;
            case "XL" -> EngineType.Fusion_XL;
            case "XXL" -> EngineType.Fusion_XXL;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    /**
     * Detects engineRating, engineType, internal structure and armor type from the source mech's
     * own components (before the remove-engine/-gyro/-endo tasks strip them) and overrides the
     * hand-typed attributes. Logs every value that disagreed so wrong move-mechs.xml entries surface.
     */
    private void autoDetectFromSource() {
        Integer rating = null;
        EngineType engine = EngineType.Fusion_Standard;
        InternalStructureType internal = InternalStructureType.Standard;
        ArmorType armor = ArmorType.Standard;
        boolean foundEngine = false;

        for (var id : sourceComponentIds()) {
            if (id == null) {
                continue;
            }
            if (id.startsWith("Gear_EngineCore_")) {
                foundEngine = true;
                try {
                    rating = Integer.parseInt(id.substring("Gear_EngineCore_".length()));
                } catch (NumberFormatException ignored) {
                    // non-numeric engine core id; leave rating from attribute
                }
            } else if (id.startsWith("Gear_Engine_")) {
                if (id.contains("XXL")) engine = EngineType.Fusion_XXL;
                else if (id.contains("XL")) engine = EngineType.Fusion_XL;
                else if (id.contains("Light")) engine = EngineType.Fusion_Light;
                else if (id.contains("Compact")) engine = EngineType.Fusion_Compact;
            }
            if (id.contains("Structure_Endo")) {
                internal = InternalStructureType.EndoSteel;
            }
            if (id.contains("Armor_FerroFibrous")) {
                armor = ArmorType.FerroFibrous;
            }
        }

        String mechId = (mechDef != null && mechDef.Description != null) ? mechDef.Description.Id : "<unknown>";
        if (foundEngine && rating != null && rating != engineRating) {
            System.out.printf("auto-detect %s: engineRating %d -> %d\n", mechId, engineRating, rating);
            engineRating = rating;
        }
        if (foundEngine && engine != engineType) {
            System.out.printf("auto-detect %s: engineType %s -> %s\n", mechId, engineType, engine);
            engineType = engine;
        }
        if (internal != internalType) {
            System.out.printf("auto-detect %s: structure %s -> %s\n", mechId, internalType, internal);
            internalType = internal;
        }
        if (armor != armorType) {
            System.out.printf("auto-detect %s: armor %s -> %s\n", mechId, armorType, armor);
            armorType = armor;
        }
    }

    private List<String> sourceComponentIds() {
        var ids = new ArrayList<String>();
        if (mechDef != null && mechDef.inventory != null) {
            for (var item : mechDef.inventory) {
                ids.add(item.ComponentDefID);
            }
        }
        if (chasisDef != null && chasisDef.FixedEquipment != null) {
            for (var equipment : chasisDef.FixedEquipment) {
                ids.add(equipment.ComponentDefID);
            }
        }
        return ids;
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

    /**
     * Trims armor so the mech fits its chassis tonnage, based on the actual weight of the
     * equipment it carries. Run AFTER the inventory is finalized and after recalculateTonnage()
     * (which sets InitialTonnage). Equipment weight is read from the component def files via the
     * scanner; fake/variant/stub placeholders (sentinel tonnage) are not counted, matching the game.
     */
    public void recalculateArmor(ResourceScanner resourceScanner) {
        double equipmentWeight = 0;
        for (var item : mechDef.inventory) {
            equipmentWeight += lookupTonnage(item.ComponentDefID, resourceScanner);
        }
        for (var equipment : chasisDef.FixedEquipment) {
            equipmentWeight += lookupTonnage(equipment.ComponentDefID, resourceScanner);
        }

        double available = chasisDef.Tonnage - chasisDef.InitialTonnage - equipmentWeight;
        if (available < 0) {
            available = 0;
        }
        int maxArmorPoints = (int) Math.floor(available / TONNAGE_PER_ARMOR_POINT);

        int currentArmor = mechDef.armorValue();
        if (currentArmor <= maxArmorPoints) {
            return; // already fits within tonnage
        }

        double scale = (double) maxArmorPoints / currentArmor;
        for (var location : mechDef.Locations) {
            location.AssignedArmor = (int) Math.floor(location.AssignedArmor * scale);
            location.AssignedRearArmor = (int) Math.floor(location.AssignedRearArmor * scale);
            location.CurrentArmor = location.AssignedArmor;
            location.CurrentRearArmor = location.AssignedRearArmor;
        }

        System.out.printf("Trimmed armor on %s: %d -> %d pts (equip %.2ft, %.2ft for armor of %.1ft chassis)\n",
                mechDef.Description.Id, currentArmor, mechDef.armorValue(), equipmentWeight, available, chasisDef.Tonnage);
    }

    private double lookupTonnage(String componentDefID, ResourceScanner resourceScanner) {
        if (tonnageCache.containsKey(componentDefID)) {
            return tonnageCache.get(componentDefID);
        }

        double tonnage = 0;
        var file = resourceScanner.scanDirectories(componentDefID + ".json");
        if (file != null) {
            try (var stream = new FileInputStream(file)) {
                var item = ReaderWriter.readItem(stream);
                if (item != null && item.Tonnage < SENTINEL_TONNAGE) {
                    tonnage = item.Tonnage;
                }
            } catch (IOException e) {
                System.out.printf("recalc-armor: failed to read tonnage for %s\n", componentDefID);
            }
        }

        tonnageCache.put(componentDefID, tonnage);
        return tonnage;
    }
}
