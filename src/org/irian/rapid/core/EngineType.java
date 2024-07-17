package org.irian.rapid.core;

import org.irian.rapid.core.rules.FusionEngine;

public enum EngineType {
    Fusion_Compact, Fusion_Standard, Fusion_Light, Fusion_XL, Fusion_XXL;

    public float getWeight(FusionEngine engine) {
        return switch(this) {
            case Fusion_Compact -> engine.Compact;
            case Fusion_Standard -> engine.Standard;
            case Fusion_Light -> engine.Light;
            case Fusion_XL -> engine.XL;
            case Fusion_XXL -> throw new IllegalStateException("Not Implemented");
        };
    }
}
