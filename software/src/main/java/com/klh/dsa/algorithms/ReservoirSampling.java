package com.klh.dsa.algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Uniform k-item sampling from a stream of unknown length. */
public final class ReservoirSampling {
    private ReservoirSampling() {
    }

    /** Returns up to k uniformly selected items from stream. */
    public static <T> List<T> sample(Iterator<T> stream, int k) {
        if (k < 0) {
            throw new IllegalArgumentException("Sample size cannot be negative");
        }
        List<T> result = new ArrayList<>(k);
        if (stream == null || k == 0) {
            return result;
        }
        long seen = 0;
        while (stream.hasNext()) {
            T item = stream.next();
            seen++;
            if (result.size() < k) {
                result.add(item);
            } else {
                long selected = ThreadLocalRandom.current().nextLong(seen);
                if (selected < k) {
                    result.set((int) selected, item);
                }
            }
        }
        return result;
    }
}
