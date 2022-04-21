package com.github.lileep.pixelmonbank.config;

import net.minecraftforge.common.config.Configuration;

public class PixelmonBankConfig {
    public static Configuration config;

//    public static long hatchCooldown;
//    public static long breedCooldown;
//    public static boolean allowRandomLegendaries;
//    public static boolean allowRandomUltraBeasts;
//    public static boolean allowNotifications;
//    public static double expConversionRate;
//    public static boolean checkEggIVs;
//    public static boolean notifyOPs;
//    public static boolean allowOfflineCheck;

    public static String SERVER_NAME;

    public static String DB_URL;
    public static int DB_PORT;
    public static String DB_USERNAME;
    public static String DB_PASSWD;
    public static String DB_DBNAME;

    public static String SERVER_NAME_DEFAULT = "default_server";

    public static final String DB_URL_DEFAULT = "127.0.0.1";
    public static final int DB_PORT_DEFAULT = 3306;
    public static final String DB_USERNAME_DEFAULT = "root";
    public static final String DB_PASSWD_DEFAULT = "root";
    public static final String DB_DBNAME_DEFAULT = "pixelbank";

    public static void loadConfig(final Configuration configuration) {
        (PixelmonBankConfig.config = configuration).addCustomCategoryComment("general", "General settings for Pixelmon Bank");

//        final Property propHatchCooldown = PixelmonBankConfig.config.get("general", "Hatch Command Cooldown (in seconds)", 600);
//        PixelmonBankConfig.hatchCooldown = propHatchCooldown.getInt(600) * 1000;
//        propHatchCooldown.setComment("Sets how long the time will be until the user can use /hatch again");
//
//        final Property propBreedCooldown = PixelmonBankConfig.config.get("general", "Breed Command Cooldown (in seconds)", 600);
//        PixelmonBankConfig.breedCooldown = propBreedCooldown.getInt(600) * 1000;
//        propBreedCooldown.setComment("Sets how long the time will be until the user can use /breed again");
//
        PixelmonBankConfig.SERVER_NAME = PixelmonBankConfig.config.get("general", "Server name", SERVER_NAME_DEFAULT, "Name of your server").getString();

        PixelmonBankConfig.DB_URL = PixelmonBankConfig.config.get("general", "Database URL", DB_URL_DEFAULT, "URL of database").getString();
        PixelmonBankConfig.DB_PORT = PixelmonBankConfig.config.get("general", "Database port", DB_PORT_DEFAULT, "Port of database").getInt();
        PixelmonBankConfig.DB_USERNAME = PixelmonBankConfig.config.get("general", "Database user name", DB_USERNAME_DEFAULT, "User name of database").getString();
        PixelmonBankConfig.DB_PASSWD = PixelmonBankConfig.config.get("general", "Database password", DB_PASSWD_DEFAULT, "Password of database").getString();
        PixelmonBankConfig.DB_DBNAME = PixelmonBankConfig.config.get("general", "Database name", DB_DBNAME_DEFAULT, "Name of database").getString();

        if (PixelmonBankConfig.config.hasChanged()) {
            PixelmonBankConfig.config.save();
        }
    }

    public static void readConfig() {
        final Configuration cfg = PixelmonBankConfig.config;
        try {
            cfg.load();
        } catch (Exception e1) {
            System.out.println("Failed to load config file!");
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    static {

        PixelmonBankConfig.SERVER_NAME = PixelmonBankConfig.SERVER_NAME_DEFAULT;

        PixelmonBankConfig.DB_URL = PixelmonBankConfig.DB_URL_DEFAULT;
        PixelmonBankConfig.DB_PORT = PixelmonBankConfig.DB_PORT_DEFAULT;
        PixelmonBankConfig.DB_USERNAME = PixelmonBankConfig.DB_USERNAME_DEFAULT;
        PixelmonBankConfig.DB_PASSWD = PixelmonBankConfig.DB_PASSWD_DEFAULT;
        PixelmonBankConfig.DB_DBNAME = PixelmonBankConfig.DB_DBNAME_DEFAULT;
    }
}
