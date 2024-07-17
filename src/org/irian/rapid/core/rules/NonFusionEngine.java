package org.irian.rapid.core.rules;

public class NonFusionEngine {
    public static NonFusionEngine[] table = {
            new NonFusionEngine(100, 6.0f, 4.0f, 5.5f),
            new NonFusionEngine(105, 7.0f, 4.5f, 6.5f),
            new NonFusionEngine(110, 7.0f, 4.f, 6.5f),
            new NonFusionEngine(115, 8.0f, 5.0f, 7.0f),
            new NonFusionEngine(120, 8.0f, 5.0f, 7.0f),
            new NonFusionEngine(125, 8.0f, 5.0f, 7.0f),
            new NonFusionEngine(130, 9.0f, 5.5f, 8.0f),
            new NonFusionEngine(135, 9.0f, 5.5f, 8.0f),
            new NonFusionEngine(140, 10.0f, 6.0f, 9.0f),
            new NonFusionEngine(145, 10.0f, 6.0f, 9.0f),
            new NonFusionEngine(150, 11.0f, 7.0f, 10.0f),
            new NonFusionEngine(155, 11.0f, 7.0f, 10.0f),
            new NonFusionEngine(160, 12.0f, 7.5f, 10.5f),
            new NonFusionEngine(165, 12.0f, 7.5f, 10.5f),
            new NonFusionEngine(170, 12.0f, 7.5f, 10.5f),
            new NonFusionEngine(175, 14.0f, 8.5f, 12.5f),
            new NonFusionEngine(180, 14.0f, 8.5f, 12.5f),
            new NonFusionEngine(185, 15.0f, 9.0f, 13.5f),
            new NonFusionEngine(190, 15.0f, 9.0f, 13.5f),
            new NonFusionEngine(195, 16.0f, 10.0f, 14.0f),
            new NonFusionEngine(200, 17.0f, 10.5f, 15.0f),
            new NonFusionEngine(205, 17.0f, 10.5f, 15.0f),
            new NonFusionEngine(210, 18.0f, 11.0f, 16.0f),
            new NonFusionEngine(215, 19.0f, 11.5f, 17.0f),
            new NonFusionEngine(220, 20.0f, 12.0f, 17.5f),
            new NonFusionEngine(225, 20.0f, 12.0f, 17.5f),
            new NonFusionEngine(230, 21.0f, 13.0f, 18.5f),
            new NonFusionEngine(235, 22.0f, 13.5f, 19.5f),
            new NonFusionEngine(240, 23.0f, 14.0f, 20.5f),
            new NonFusionEngine(245, 24.0f, 14.5f, 21.0f),
            new NonFusionEngine(250, 25.0f, 15.0f, 22.0f),
            new NonFusionEngine(255, 26.0f, 16.0f, 23.0f),
            new NonFusionEngine(260, 27.0f, 16.5f, 24.0f),
            new NonFusionEngine(265, 28.0f, 17.0f, 24.5f),
            new NonFusionEngine(270, 29.0f, 17.5f, 25.5f),
            new NonFusionEngine(275, 31.0f, 19.0f, 27.5f),
            new NonFusionEngine(280, 32.0f, 19.5f, 28.0f),
            new NonFusionEngine(285, 33.0f, 20.0f, 29.0f),
            new NonFusionEngine(290, 35.0f, 21.0f, 31.0f),
            new NonFusionEngine(295, 36.0f, 22.0f, 31.5f),
            new NonFusionEngine(300, 38.0f, 23.0f, 33.5f),
            new NonFusionEngine(305, 39.0f, 23.5f, 34.5f),
            new NonFusionEngine(310, 41.0f, 25.0f, 36.0f),
            new NonFusionEngine(315, 43.0f, 26.0f, 38.0f),
            new NonFusionEngine(320, 45.0f, 27.0f, 39.5f),
            new NonFusionEngine(325, 47.0f, 38.5f, 41.5f),
            new NonFusionEngine(330, 49.0f, 29.5f, 43.0f),
            new NonFusionEngine(335, 51.0f, 31.0f, 45.0f),
            new NonFusionEngine(340, 54.0f, 32.5f, 47.5f),
            new NonFusionEngine(345, 57.0f, 34.5f, 50.0f),
            new NonFusionEngine(350, 59.0f, 35.5f, 52.0f),
            new NonFusionEngine(355, 63.0f, 38.0f, 55.5f),
            new NonFusionEngine(360, 66.0f, 40.0f, 58.0f),
            new NonFusionEngine(365, 69.0f, 41.5f, 60.5f),
            new NonFusionEngine(370, 73.0f, 44.0f, 64.0f),
            new NonFusionEngine(375, 77.0f, 46.5f, 67.5f),
            new NonFusionEngine(380, 82.0f, 49.5f, 72.0f),
            new NonFusionEngine(385, 87.0f, 52.5f, 76.5f),
            new NonFusionEngine(390, 92.0f, 55.5f, 80.5f),
            new NonFusionEngine(395, 98.0f, 59.0f, 86.0f),
            new NonFusionEngine(400, 105.0f, 63.0f, 92.0f)
    };

    public int Rating;
    public float InternalCombustion;
    public float Cell;
    public float Fission;

    public NonFusionEngine(int rating, float ice, float cell, float fission) {
        this.Rating = rating;
        this.InternalCombustion = ice;
        this.Cell = cell;
        this.Fission = fission;
    }
}
