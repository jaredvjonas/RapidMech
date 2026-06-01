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
            // XXL masses 1/3 of a standard fusion engine, rounded up to the half-ton
            // (the table's XL column is likewise Standard/2). Computed instead of adding a
            // 6th column to every row of the engine table.
            case Fusion_XXL -> roundUpToHalfTon(engine.Standard / 3.0f);
        };
    }

    private static float roundUpToHalfTon(float tons) {
        return (float) (Math.ceil(tons * 2.0) / 2.0);
    }
}
