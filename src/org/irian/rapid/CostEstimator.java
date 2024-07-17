package org.irian.rapid;

import org.irian.rapid.core.ArmorType;
import org.irian.rapid.core.EngineType;
import org.irian.rapid.core.InternalStructureType;
import org.irian.rapid.core.rules.FusionEngine;
import org.irian.rapid.core.rules.GyroType;
import org.irian.rapid.defs.MechDef;
import org.irian.rapid.defs.ReaderWriter;
import org.irian.rapid.defs.item.BasicItemDef;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CostEstimator {

    private static final Map<String, Long> hardcodedEquipment = Map.of(
            "Gear_AP", 670000L,
            "Gear_AP_CLAN", 670000L
    );

    private static final Map<String, BasicItemDef> equipmentTable = new HashMap<>();

    private final ResourceScanner fileScanner;

    public CostEstimator(ResourceScanner fileScanner) {
        this.fileScanner = fileScanner;
    }


    public void calculateCost(Mech mech) {
        int mechTonnage = (int)mech.chasisDef.Tonnage;
        double cockpit = estimateCockpit(mechTonnage);
        double engine = estimateEngine(mechTonnage, mech.engineRating, mech.engineType);
        double gyro = estimateGyroCost(mech.engineRating, GyroType.Standard);
        double internalStructure = estimateInternalStructure(mechTonnage, mech.internalType);

        int armorValue = mech.mechDef.armorValue();
        double armor = estimateArmorValue(armorValue, mech.armorType);

        double chassisPriceTag = cockpit + engine + gyro + internalStructure + armor;
        double mechPriceTag = chassisPriceTag + estimateEquipment(mech.mechDef);

        // round prices to the nearest 1,000 C-Bills
        chassisPriceTag = Math.round(chassisPriceTag / 1000) * 1000;
        mechPriceTag = Math.round(mechPriceTag / 1000) * 1000;

        System.out.printf("Updating price for %s, Chassis [%s], Fully Outfitted Mech [%s]\n", mech.mechDef.Description.Name, formatCost(chassisPriceTag), formatCost(mechPriceTag));

        mech.chasisDef.Description.Cost = (long) (chassisPriceTag);
        mech.mechDef.Description.Cost = (long) mechPriceTag;
        // Atlas AS7-D worth 12.98 million C-Bills, the mechPartCost is 1.3 million C-Bills
        mech.mechDef.simGameMechPartCost = (long) (mechPriceTag / 10); // Use simple math for now
    }

    private String formatCost(double value) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        var cost = formatter.format((long)value);
        return String.format("%s C-Bills", cost);
    }

    public double estimateEquipment(MechDef mechDef) {
        double equipmentCost = 0f;
        for (var item : mechDef.inventory) {
            var itemCost = lookupEquipment(item.ComponentDefID);
            equipmentCost += itemCost;
        }

        return equipmentCost;
    }

    private long lookupEquipment(String componentDefID) {
        if (hardcodedEquipment.containsKey(componentDefID)) {
            return hardcodedEquipment.get(componentDefID);
        }

        if (equipmentTable.containsKey(componentDefID)) {
            var itemDef = equipmentTable.get(componentDefID);
            return itemDef.Description.Cost;
        }

        var itemDef = loadEquipment(componentDefID);
        if (itemDef != null) {
            equipmentTable.put(componentDefID, itemDef);
            return itemDef.Description.Cost;
        }

        return 0;
    }

    private BasicItemDef loadEquipment( String componentDefID) {
        String filename = String.format("%s.json", componentDefID);
        File file = fileScanner.scanDirectories(filename);
        if (file != null) {
//            System.out.printf("Loading equipment file: %s\n", file.getPath());
            try (var inputStream = new FileInputStream(file)) {
                return ReaderWriter.readItem(inputStream);
            } catch (IOException e) {
                System.out.printf("Failed to read file: %s\n", file.getPath());
                System.out.print(e.getMessage());
            }
        }
        System.out.printf("Unable to load equipment Description for ID: %s\n", componentDefID);
        return null;
    }

    private double estimateCockpit(int mechTonnage) {
        return 250000.0f + ( 2000.0f * mechTonnage );
    }

    private double estimateEngine(int mechTonnage, int engineRating, EngineType engineType) {
        double baseCost = engineBaseCost(engineType);
        if ( engineRating > 400 ) baseCost *= 2;
        return ( baseCost * ((double) mechTonnage) * ((double) engineRating )) / 75.0f;
    }

    private double engineBaseCost(EngineType engineType) {
        return switch (engineType) {
            case Fusion_Standard -> 5000.0f;
            case Fusion_Compact -> 10000.0f;
            case Fusion_Light -> 15000.0f;
            case Fusion_XL -> 20000.0f;
            case Fusion_XXL -> 100000.0f;
        };
    }

    private double estimateGyroCost(int engineRating, GyroType gyroType) {
        double baseCost = gyroBaseCost(gyroType);
        FusionEngine fusionEngine = FusionEngine.find(engineRating);
        if (fusionEngine == null) throw new IllegalStateException(String.format("FusionEngine was not found for engineRating %d", engineRating));
        return baseCost * fusionEngine.Gyro;
    }

    private double gyroBaseCost(GyroType gyroType) {
        return switch (gyroType) {
            case Standard -> 300000.0f;
            case Compact -> 400000.0f;
            case XL -> 750000.0f;
            case HeavyDuty -> 500000.0f;
            case SuperHeavy -> 600000.0f;
        };
    }

    private double estimateInternalStructure(int mechTonnage, InternalStructureType type) {
        return switch (type) {
            case Standard -> 1000.0f * mechTonnage;
            case EndoSteel -> 1600.0f * mechTonnage;
        };
    }

    private double estimateArmor(int armorTonnage, ArmorType type) {
        return switch (type) {
            case Standard -> 10000.0f * armorTonnage;
            case FerroFibrous -> 20000.0f * armorTonnage;
        };
    }

    private double estimateArmorValue(int armorPoints, ArmorType type) {
        return switch (type) {
            case Standard -> 625.0f * armorPoints;
            case FerroFibrous -> 1250.0f * armorPoints;
        };
    }

}
