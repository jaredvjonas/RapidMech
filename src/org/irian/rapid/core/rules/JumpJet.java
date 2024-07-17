package org.irian.rapid.core.rules;

/**
 *
 * JUMP JET WEIGHT AND CRITICAL SPACES TABLE
 * MECH -------------- JUMP JET ---------------- CRITICAL
 * TONNAGE ----------- WEIGHT ----------------- SPACES
 * 10-55 ----------- 0.5 TONS/JUMP MP ----------- 1/JUMP JET
 * 60-85 ----------- 1.0 TONS/JUMP MP ----------- 1/JUMP JET
 * 90-100 ----------- 2.0 TONS/JUMP MP ----------- 2/JUMP JET
 *
 */
public class JumpJet {
    public static JumpJet LIGHT = new JumpJet(10, 55, 0.5f, 1);
    public static JumpJet MEDIUM = new JumpJet(60, 85, 1.0f, 1);
    public static JumpJet HEAVY = new JumpJet(90, 100, 2.0f, 2);

    public static JumpJet[] table = { LIGHT, MEDIUM, HEAVY };

    public int MinTonnage;
    public int MaxTonnage;
    public float Weight;
    public int CriticalSpace;

    public JumpJet(int min, int max, float weight, int space) {
        this.MinTonnage = min;
        this.MaxTonnage = max;
        this.Weight = weight;
        this.CriticalSpace = space;
    }
}
