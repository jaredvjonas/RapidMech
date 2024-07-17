package org.irian.rapid.defs.chassis;

/**
 *   "Custom": {
 *     "DropCostFactor": {
 *       "DropModifier": 1.08
 *     },
 *     "AssemblyVariant": {
 *       "PrefabID": "anvil",
 *       "Exclude": false,
 *       "Include": true
 *     }
 *   },
 */
public class CustomTags {
    public DropCost DropCostFactor;
    public Variant AssemblyVariant;

    public static class DropCost {
        public float DropModifier;
    }

    public static class Variant {
        public String PrefabID;
        public boolean Exclude;
        public boolean Include;
    }
}
