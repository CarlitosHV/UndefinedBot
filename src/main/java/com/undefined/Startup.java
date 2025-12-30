package com.undefined;

import com.undefined.config.BotConfiguration;

public class Startup {
    public static void main(String[] args) {
        BotConfiguration config = new BotConfiguration();

        if (!config.isValid()) {
            return;
        }
    }

    private static String maskToken(String token) {
        if (token == null || token.length() < 10) return "***";
        return token.substring(0, 8) + "..." + token.substring(token.length() - 4);
    }
}
