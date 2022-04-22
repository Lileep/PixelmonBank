package com.github.lileep.pixelmonbank.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class PixelmonBankConfig {
    public static Configuration config;

    public static boolean ALLOW_LEGENDARY = true;
    public static boolean ALLOW_ULTRABEAST = true;

    public static String[] BLACK_LIST = new String[0];

    public static String SERVER_NAME = "default_server";
    public static String LOCALE = "en_us";
    public static String LOCALE_OLD = LOCALE;

    public static String DB_IP = "127.0.0.1";
    public static int DB_PORT = 3306;
    public static String DB_DBNAME = "pixelbank";
    public static String DB_USERNAME = "root";
    public static String DB_PASSWD = "root";

    //pre load
    public static void loadLanguage(){
        LOCALE = config.get("general", "locale", LOCALE).getString();
    }

    public static void loadConfig(final String configurationPath) {
        Configuration configuration = new Configuration(new File(configurationPath, "config.cfg"));
        configuration.load();
        config = configuration;

        loadLanguage();
        LOCALE_OLD  = LOCALE;

        //Load locale texts
        Configuration localeConfig = new Configuration(new File(configurationPath + "/locale", LOCALE + ".cfg"));
        localeConfig.load();
        PixelmonBankLocaleConfig.loadConfig(localeConfig);

        loadConfig();
    }

    //post load
    public static void loadConfig(){
        String category = "general";
        config.addCustomCategoryComment(category, "General settings for Pixelmon Bank");

        ALLOW_LEGENDARY = config.get(category, "allow_legendary", ALLOW_LEGENDARY, "Whether allow legendaries to be sent to Pixelmon Bank").getBoolean();
        ALLOW_ULTRABEAST = config.get(category, "allow_ultrabeast", ALLOW_ULTRABEAST, "Whether allow ultra beasts to be sent to Pixelmon Bank").getBoolean();
        BLACK_LIST = config.get(category, "black_list", BLACK_LIST, "Listed pixelmons will be prohibited from being sent to Pixelmon Bank").getStringList();


        category = "database";
        config.addCustomCategoryComment(category, "Database settings for Pixelmon Bank. Restart server to apply instead of reload");
        SERVER_NAME = config.get(category, "server_name", SERVER_NAME, "Name of your server. Make this as a database field").getString();

        DB_IP = config.get(category, "database_ip", DB_IP, "IP address of database").getString();
        DB_PORT = config.get(category, "database_port", DB_PORT, "Port of database").getInt();
        DB_DBNAME = config.get(category, "database_name", DB_DBNAME, "Name of database").getString();
        DB_USERNAME = config.get(category, "database_username", DB_USERNAME, "User name of database").getString();
        DB_PASSWD = config.get(category, "database_passwd", DB_PASSWD, "Password of database").getString();


        if (config.hasChanged()) {
            config.save();
        }
    }
}
