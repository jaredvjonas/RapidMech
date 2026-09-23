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

    /** Opt-in from the mech line: spend all tonnage-allowed armor rather than only trimming. */
    public boolean maxArmor = false;

    public Mech(MechCmd mechCmd, MechDef mechDef, ChasisDef chassisDef) {
        this.engineRating = mechCmd.engineRating;
        this.engineType = buildEngineType(mechCmd.engineType);
        this.internalType = mechCmd.endoSteel ? InternalStructureType.EndoSteel : InternalStructureType.Standard;
        this.armorType = mechCmd.ferroFibrous ? ArmorType.FerroFibrous : ArmorType.Standard;
        this.mechDef = mechDef;
        this.chasisDef = chassisDef;
        this.techBase = (mechCmd.techBase != null && mechCmd.techBase.equals("Clan")) ? TechnologyBase.Clan : TechnologyBase.InnerSphere;
        this.maxArmor = mechCmd.maxArmor;

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
            // Match on CONTAINS, not startsWith: RT prefixes some engines with "Unique_"
            // (Unique_Gear_Engine_XL_Supercharged on the bushwacker GTL/GTL2,
            // Unique_Gear_Engine_Light_Prototype on the apollo APL-3F). A missed engine silently
            // fell back to Standard, whose extra weight then ate the mech's armor in the trim pass.
            int coreAt = id.indexOf("Gear_EngineCore_");
            if (coreAt >= 0) {
                foundEngine = true;
                try {
                    rating = Integer.parseInt(id.substring(coreAt + "Gear_EngineCore_".length()));
                } catch (NumberFormatException ignored) {
                    // non-numeric engine core id; leave rating from attribute
                }
            } else if (id.contains("Gear_Engine_")) {
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
            if (maxArmor && currentArmor < maxArmorPoints) {
                fillArmor(maxArmorPoints, currentArmor, available);
            }
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

    /**
     * Spends the armor tonnage a conversion left idle. Only ever called when the mech already
     * fits its chassis tonnage and `maxArmor` is set on the mech line, so it cannot make a mech
     * overweight: the ceiling is the same tonnage-derived point budget recalculateArmor uses to
     * trim, and every location is additionally capped by the chassis MaxArmor / MaxRearArmor.
     *
     * Points are handed out front-first in a fixed location order, then to rear arcs, so the
     * result is deterministic and idempotent across regens (a second run finds nothing to add).
     * Armor that cannot be placed because every location is already at its cap is simply left
     * unspent -- the chassis caps win over the tonnage budget, never the other way round.
     */
    private void fillArmor(int maxArmorPoints, int currentArmor, double available) {
        int budget = maxArmorPoints - currentArmor;
        if (budget <= 0) {
            return;
        }

        // Front arcs first: a point of front armor is worth more than a point of rear armor.
        for (var location : mechDef.Locations) {
            if (budget <= 0) break;
            var cap = findChassisLocation(location.Location);
            if (cap == null || cap.MaxArmor <= 0) continue;
            int room = cap.MaxArmor - location.AssignedArmor;
            if (room <= 0) continue;
            int add = Math.min(room, budget);
            location.AssignedArmor += add;
            location.CurrentArmor = location.AssignedArmor;
            budget -= add;
        }

        // Then rear arcs, where MaxRearArmor of -1 means the location has no rear facing.
        for (var location : mechDef.Locations) {
            if (budget <= 0) break;
            var cap = findChassisLocation(location.Location);
            if (cap == null || cap.MaxRearArmor <= 0) continue;
            int room = cap.MaxRearArmor - location.AssignedRearArmor;
            if (room <= 0) continue;
            int add = Math.min(room, budget);
            location.AssignedRearArmor += add;
            location.CurrentRearArmor = location.AssignedRearArmor;
            budget -= add;
        }

        System.out.printf("Filled armor on %s: %d -> %d pts of %d allowed (%.2ft for armor of %.1ft chassis)%s\n",
                mechDef.Description.Id, currentArmor, mechDef.armorValue(), maxArmorPoints,
                available, chasisDef.Tonnage,
                budget > 0 ? String.format(", %d pts unspent (all locations at chassis cap)", budget) : "");
    }

    private org.irian.rapid.defs.chassis.Location findChassisLocation(String name) {
        for (var location : chasisDef.Locations) {
            if (location.Location.equals(name)) {
                return location;
            }
        }
        return null;
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
