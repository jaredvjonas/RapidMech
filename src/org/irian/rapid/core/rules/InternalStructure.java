package org.irian.rapid.core.rules;

import org.irian.rapid.core.ArmorType;
import org.irian.rapid.core.TechnologyBase;

/**
 * MAXIMUM BATTLEMECH ARMOR
 * BATTLEMECH --------- MAXIMUM
 * TONNAGE ----------- ARMOR
 * 20 ------------------- 69
 * 25-------------------89
 * 30 ------------------- 105
 * 35 ------------------- 119
 * 40------------------137
 * 45 ------------------- 153
 * 50 ------------------- 169
 * 55------------------185
 * 60 ------------------- 201
 * 65 ------------------- 211
 * 70------------------217
 * 75 ------------------- 231
 * 80 ------------------- 247
 * 85------------------263
 * 90 ------------------- 279
 * 95 ------------------- 293
 * 100-----------------307
 */
public class InternalStructure {

    public static InternalStructure[] table = {
        new InternalStructure(20, 2.0f, 1.0f, 69),
        new InternalStructure(25, 2.5f, 1.5f, 89),
        new InternalStructure(30, 3.0f, 1.5f, 105),
        new InternalStructure(35, 3.5f, 2.0f, 119),
        new InternalStructure(40, 4.0f, 2.0f, 137),
        new InternalStructure(45, 4.5f, 2.5f, 153),
        new InternalStructure(50, 5.0f, 2.5f, 169),
        new InternalStructure(55, 5.5f, 3.0f, 185),
        new InternalStructure(60, 6.0f, 3.0f, 201),
        new InternalStructure(65, 6.5f, 3.5f, 211),
        new InternalStructure(70, 7.0f, 3.5f, 217),
        new InternalStructure(75, 7.5f, 4.0f, 231),
        new InternalStructure(80, 8.0f, 4.0f, 247),
        new InternalStructure(85, 8.5f, 4.5f, 263),
        new InternalStructure(90, 9.0f, 4.5f, 279),
        new InternalStructure(95, 9.5f, 5.0f, 293),
        new InternalStructure(100, 10.0f, 5.0f, 307)
    };

    public static InternalStructure find(int tonnage) {
        for (var item : table) {
            if (item.Tonnage == tonnage) {
                return item;
            }
        }
        return null;
    }

    public int Tonnage;
    public float Standard;
    public float EndoSteel;
    public int MaxArmorFactor;

    public InternalStructure(int weight, float standard, float endoSteel, int rating) {
        this.Tonnage = weight;
        this.Standard = standard;
        this.EndoSteel = endoSteel;
        this.MaxArmorFactor = rating * 5; // game engine multiplies by 5
    }

    public int headValue(ArmorType armor, TechnologyBase techBase) {
        var armorFactor = calcArmorFactor(armor, techBase);
        return Math.round(armorFactor * 0.0588f); // 5.88 percent
    }

    public int armValue(ArmorType armor, TechnologyBase techBase) {
        var armorFactor = calcArmorFactor(armor, techBase);
        return Math.round(armorFactor * 0.0915f); // 18.30 percent
    }

    public int torsoValue(ArmorType armor, TechnologyBase techBase) {
        var armorFactor = calcArmorFactor(armor, techBase);
        return Math.round(armorFactor * 0.1438f); // 28.76 percent
    }

    public int centerTorsoValue(ArmorType armor, TechnologyBase techBase) {
        var armorFactor = calcArmorFactor(armor, techBase);
        return Math.round(armorFactor * 0.1830f); // 18.30 percent
    }

    public int legValue(ArmorType armor, TechnologyBase techBase) {
        var armorFactor = calcArmorFactor(armor, techBase);
        return Math.round(armorFactor * 0.1438f); // 28.76 percent
    }

    private int calcArmorFactor(ArmorType armor, TechnologyBase techBase) {
        if (armor == ArmorType.FerroFibrous) {
            return (int) ((techBase == TechnologyBase.Clan) ? MaxArmorFactor * 1.2 : MaxArmorFactor * 1.16);
        }
        return MaxArmorFactor;
    }
}
// 5.88 + (9.15 * 2) + (14.38 * 4) + 18.30 = 100 percent
// 5.88 + 18.30 + 57.52 + 18.30 = 100 percent