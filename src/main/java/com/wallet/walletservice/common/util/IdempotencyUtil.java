package com.wallet.walletservice.common.util;

import java.util.HashSet;
import java.util.Set;

public class IdempotencyUtil {

    // In-memory set (for demo purposes; use Redis for production)
    private static final Set<String> processedKeys = new HashSet<>();

    public static boolean isProcessed(String key) {
        return processedKeys.contains(key);
    }

    public static void markProcessed(String key) {
        processedKeys.add(key);
    }
}
