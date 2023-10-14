package com.github.lileep.pixelmonbank.util;

import java.util.UUID;

public final class UUIDCodec {
    public static UUID uuidFromIntArray(int[] uuidArray) {
        return new UUID((long)uuidArray[0] << 32 | (long)uuidArray[1] & 4294967295L, (long)uuidArray[2] << 32 | (long)uuidArray[3] & 4294967295L);
    }

    public static int[] uuidToIntArray(UUID uuid) {
        long i = uuid.getMostSignificantBits();
        long j = uuid.getLeastSignificantBits();
        return leastMostToIntArray(i, j);
    }

    private static int[] leastMostToIntArray(long most, long least) {
        return new int[]{(int)(most >> 32), (int)most, (int)(least >> 32), (int)least};
    }
}
