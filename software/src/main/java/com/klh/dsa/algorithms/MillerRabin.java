package com.klh.dsa.algorithms;

import java.math.BigInteger;

/** Miller-Rabin primality test using BigInteger modular exponentiation. */
public final class MillerRabin {
    private static final long[] WITNESSES = {2L, 325L, 9_375L, 28_178L,
            450_775L, 9_780_504L, 1_795_265_022L};

    private MillerRabin() {
    }

    /** Tests primality using up to k deterministic witnesses for 64-bit inputs. */
    public static boolean isPrime(long number, int k) {
        if (number < 2) {
            return false;
        }
        if (number == 2 || number == 3) {
            return true;
        }
        if ((number & 1L) == 0 || k <= 0) {
            return false;
        }
        long oddPart = number - 1;
        int powersOfTwo = Long.numberOfTrailingZeros(oddPart);
        oddPart >>= powersOfTwo;
        BigInteger modulus = BigInteger.valueOf(number);
        BigInteger minusOne = modulus.subtract(BigInteger.ONE);
        BigInteger exponent = BigInteger.valueOf(oddPart);
        int checks = Math.min(k, WITNESSES.length);
        for (int i = 0; i < checks; i++) {
            BigInteger base = BigInteger.valueOf(WITNESSES[i]).mod(modulus);
            if (base.signum() == 0) {
                continue;
            }
            BigInteger result = base.modPow(exponent, modulus);
            if (result.equals(BigInteger.ONE) || result.equals(minusOne)) {
                continue;
            }
            boolean probablyPrime = false;
            for (int round = 1; round < powersOfTwo; round++) {
                result = result.multiply(result).mod(modulus);
                if (result.equals(minusOne)) {
                    probablyPrime = true;
                    break;
                }
            }
            if (!probablyPrime) {
                return false;
            }
        }
        return true;
    }
}
