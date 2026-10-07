package com.klh.dsa.algorithms;

/** Minimum-cost rectangular assignment using the Hungarian algorithm. */
public final class HungarianAlgorithm {
    private HungarianAlgorithm() {
    }

    /**
     * Assigns each row to a distinct column at minimum cost. Returns an empty
     * array for no rows; the matrix must have at least as many columns as rows.
     */
    public static int[] solve(double[][] cost) {
        if (cost == null || cost.length == 0) {
            return new int[0];
        }
        int rows = cost.length;
        if (cost[0] == null) {
            throw new IllegalArgumentException("Cost matrix cannot contain null rows");
        }
        int columns = cost[0].length;
        if (columns < rows) {
            throw new IllegalArgumentException("Cost matrix must have at least as many columns as rows");
        }
        for (double[] row : cost) {
            if (row == null || row.length != columns) {
                throw new IllegalArgumentException("Cost matrix must be rectangular");
            }
            for (double value : row) {
                if (!Double.isFinite(value)) {
                    throw new IllegalArgumentException("Costs must be finite numbers");
                }
            }
        }
        double[] u = new double[rows + 1];
        double[] v = new double[columns + 1];
        int[] matchedRow = new int[columns + 1];
        int[] path = new int[columns + 1];
        for (int row = 1; row <= rows; row++) {
            matchedRow[0] = row;
            double[] minimum = new double[columns + 1];
            boolean[] used = new boolean[columns + 1];
            java.util.Arrays.fill(minimum, Double.POSITIVE_INFINITY);
            int column0 = 0;
            do {
                used[column0] = true;
                int row0 = matchedRow[column0];
                double delta = Double.POSITIVE_INFINITY;
                int column1 = 0;
                for (int column = 1; column <= columns; column++) {
                    if (!used[column]) {
                        double reduced = cost[row0 - 1][column - 1] - u[row0] - v[column];
                        if (reduced < minimum[column]) {
                            minimum[column] = reduced;
                            path[column] = column0;
                        }
                        if (minimum[column] < delta) {
                            delta = minimum[column];
                            column1 = column;
                        }
                    }
                }
                for (int column = 0; column <= columns; column++) {
                    if (used[column]) {
                        u[matchedRow[column]] += delta;
                        v[column] -= delta;
                    } else {
                        minimum[column] -= delta;
                    }
                }
                column0 = column1;
            } while (matchedRow[column0] != 0);
            do {
                int column1 = path[column0];
                matchedRow[column0] = matchedRow[column1];
                column0 = column1;
            } while (column0 != 0);
        }
        int[] assignment = new int[rows];
        for (int column = 1; column <= columns; column++) {
            if (matchedRow[column] != 0) {
                assignment[matchedRow[column] - 1] = column - 1;
            }
        }
        return assignment;
    }
}
