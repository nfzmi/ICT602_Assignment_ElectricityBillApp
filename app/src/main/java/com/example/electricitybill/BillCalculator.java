package com.example.electricitybill;

public class BillCalculator {

    private static final double RATE_TIER1 = 0.218; // RM per kWh (1-200)
    private static final double RATE_TIER2 = 0.334; // RM per kWh (201-300)
    private static final double RATE_TIER3 = 0.516; // RM per kWh (301-600)
    private static final double RATE_TIER4 = 0.546; // RM per kWh (601-1000)

    private static final int TIER1_LIMIT = 200;
    private static final int TIER2_LIMIT = 300;
    private static final int TIER3_LIMIT = 600;
    private static final int TIER4_LIMIT = 1000;


    public static double calculateTotalCharges(double units) {
        double total = 0;
        double remaining = units;

        // Tier 1: first 200 kWh
        double tier1Units = Math.min(remaining, TIER1_LIMIT);
        total += tier1Units * RATE_TIER1;
        remaining -= tier1Units;

        if (remaining <= 0) return roundToTwoDecimals(total);

        // Tier 2: next 100 kWh (201-300)
        double tier2Units = Math.min(remaining, TIER2_LIMIT - TIER1_LIMIT);
        total += tier2Units * RATE_TIER2;
        remaining -= tier2Units;

        if (remaining <= 0) return roundToTwoDecimals(total);

        // Tier 3: next 300 kWh (301-600)
        double tier3Units = Math.min(remaining, TIER3_LIMIT - TIER2_LIMIT);
        total += tier3Units * RATE_TIER3;
        remaining -= tier3Units;

        if (remaining <= 0) return roundToTwoDecimals(total);

        // Tier 4: next 300 kWh (601-1000)
        double tier4Units = Math.min(remaining, TIER4_LIMIT - TIER3_LIMIT);
        total += tier4Units * RATE_TIER4;
        remaining -= tier4Units;

        return roundToTwoDecimals(total);
    }


    public static double calculateFinalCost(double totalCharges, double rebatePercent) {
        double finalCost = totalCharges - (totalCharges * (rebatePercent / 100.0));
        return roundToTwoDecimals(finalCost);
    }

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
