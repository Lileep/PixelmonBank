package com.github.lileep.pixelmonbank.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class PixelmonBankConfig {
    public static Configuration config;

    public static boolean ALLOW_LEGENDARY = true;
    public static boolean ALLOW_ULTRABEAST = true;
    public static boolean ALLOW_EGG = false;
    public static String[] BLACK_LIST = new String[0];
    public static String[] BLACK_LIST_ITEM = new String[0];
//    public static String[] BLACK_LIST_MOVE = new String[0];
    public static int MAX_IVS = 6;
    public static boolean COUNT_HYPER_TRAINED = false;

    public static String SERVER_NAME = "default_server";
    public static String LOCALE = "en_us";
    public static String LOCALE_OLD = LOCALE;

    public static String DB_IP = "127.0.0.1";
    public static int DB_PORT = 3306;
    public static String DB_DBNAME = "pixelbank";
    public static String DB_USERNAME = "root";
    public static String DB_PASSWD = "root";

    //pre load
    public static void loadLanguage() {
        LOCALE = config.get("general", "server_language", LOCALE, "Language of your server").getString();
    }

    public static void loadConfig(final String configurationPath) {
        Configuration configuration = new Configuration(new File(configurationPath, "config.cfg"));
        configuration.load();
        config = configuration;

        loadLanguage();
        LOCALE_OLD = LOCALE;

        //Load locale texts
        Configuration localeConfig = new Configuration(new File(configurationPath + "/locale", LOCALE + ".cfg"));
        localeConfig.load();
        PixelmonBankLocaleConfig.loadConfig(localeConfig);

        loadConfig();
    }

    //post load
    public static void loadConfig() {
        String category = "general";
        config.addCustomCategoryComment(category, PixelmonBankLocaleConfig.cfgCategoryGeneral);

        ALLOW_LEGENDARY = config.get(category, "allow_legendary", ALLOW_LEGENDARY, PixelmonBankLocaleConfig.cfgAllowLegendary).getBoolean();
        ALLOW_ULTRABEAST = config.get(category, "allow_ultrabeast", ALLOW_ULTRABEAST, PixelmonBankLocaleConfig.cfgAllowUltraBeast).getBoolean();
        ALLOW_EGG = config.get(category, "allow_egg", ALLOW_EGG, PixelmonBankLocaleConfig.cfgAllowEgg).getBoolean();
        BLACK_LIST = config.get(category, "black_list", BLACK_LIST, PixelmonBankLocaleConfig.cfgBlackList).getStringList();
        for (int i = 0; i < BLACK_LIST.length; i++) {
            BLACK_LIST[i] = BLACK_LIST[i].toLowerCase();
        }
        BLACK_LIST_ITEM = config.get(category, "black_list_item", BLACK_LIST_ITEM, PixelmonBankLocaleConfig.cfgBlackListItem).getStringList();
        for (int i = 0; i < BLACK_LIST_ITEM.length; i++) {
            BLACK_LIST_ITEM[i] = BLACK_LIST_ITEM[i].toLowerCase();
        }
//        BLACK_LIST_MOVE = config.get(category, "black_list_move", BLACK_LIST_MOVE, PixelmonBankLocaleConfig.cfgBlackListMove).getStringList();
//        for (int i = 0; i < BLACK_LIST_MOVE.length; i++) {
//            BLACK_LIST_MOVE[i] = BLACK_LIST_MOVE[i].toLowerCase().replaceAll("\\s*", "");
//        }

        Property maxIvs = config.get(category, "max_ivs", MAX_IVS, PixelmonBankLocaleConfig.cfgMaxIvs);
        if (maxIvs.getInt() > 6) {
            maxIvs.set(6);
        } else if (maxIvs.getInt() < 0) {
            maxIvs.set(0);
        }
        MAX_IVS = maxIvs.getInt();
        COUNT_HYPER_TRAINED = config.get(category, "count_hyper_trained", COUNT_HYPER_TRAINED, PixelmonBankLocaleConfig.cfgCountHyperTrained).getBoolean();



        category = "database";
        config.addCustomCategoryComment(category, PixelmonBankLocaleConfig.cfgCategoryDB);
        SERVER_NAME = config.get(category, "server_name", SERVER_NAME, PixelmonBankLocaleConfig.cfgServerName).getString();

        DB_IP = config.get(category, "database_ip", DB_IP, PixelmonBankLocaleConfig.cfgDBIP).getString();
        DB_PORT = config.get(category, "database_port", DB_PORT, PixelmonBankLocaleConfig.cfgDBPort).getInt();
        DB_DBNAME = config.get(category, "database_name", DB_DBNAME, PixelmonBankLocaleConfig.cfgDBName).getString();
        DB_USERNAME = config.get(category, "database_username", DB_USERNAME, PixelmonBankLocaleConfig.cfgDBUsername).getString();
        DB_PASSWD = config.get(category, "database_passwd", DB_PASSWD, PixelmonBankLocaleConfig.cfgDBPasswd).getString();


        if (config.hasChanged()) {
            config.save();
        }
    }
}
