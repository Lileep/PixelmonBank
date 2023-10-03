package com.github.lileep.pixelmonbank.config;

import com.github.lileep.pixelmonbank.util.PokemonOptUtil;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

public class PixelmonBankConfig {
    public static Configuration config;

    public static boolean ALLOW_LEGENDARY = true;
    public static boolean ALLOW_ULTRABEAST = true;
    public static boolean ALLOW_EGG = false;
    public static boolean ALLOW_UNTRADEABLE = false;
    public static boolean STERILIZE_WHEN_WITHDRAW = false;
    public static boolean UNTRADIFY_WHEN_WITHDRAW = false;
    public static boolean RESET_FRIENDSHIP_WHEN_WITHDRAW = false;
    public static String[] RESTRICT_LIST = new String[0];
    public static int RESTRICT_COUNT = 1;
    public static String[] BLACK_LIST = new String[0];
    public static boolean ALLOW_ITEM = true;
    public static String[] BLACK_LIST_ITEM = new String[0];
    //    public static String[] BLACK_LIST_MOVE = new String[0];
    public static int MAX_COUNT = 0;
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
        ALLOW_UNTRADEABLE = config.get(category, "allow_untradeable", ALLOW_UNTRADEABLE, PixelmonBankLocaleConfig.cfgAllowUntradeable).getBoolean();
        STERILIZE_WHEN_WITHDRAW = config.get(category, "sterialize_when_withdraw", STERILIZE_WHEN_WITHDRAW, PixelmonBankLocaleConfig.cfgSterializeWhenWithDraw).getBoolean();;
        UNTRADIFY_WHEN_WITHDRAW = config.get(category, "untradify_when_withdraw", UNTRADIFY_WHEN_WITHDRAW, PixelmonBankLocaleConfig.cfgUntradifyWhenWithdraw).getBoolean();;
        RESET_FRIENDSHIP_WHEN_WITHDRAW = config.get(category, "reset_friendship_when_withdraw", RESET_FRIENDSHIP_WHEN_WITHDRAW, PixelmonBankLocaleConfig.cfgResetFriendshipWhenWithdraw).getBoolean();;
        RESTRICT_LIST = config.get(category, "restrict_list", RESTRICT_LIST, PixelmonBankLocaleConfig.cfgRestrictList).getStringList();
        for (int i = 0; i < RESTRICT_LIST.length; i++) {
            RESTRICT_LIST[i] = RESTRICT_LIST[i].toLowerCase();
        }
        PokemonOptUtil.RESTRICT_POKEMONS = new HashSet<>(Arrays.asList(RESTRICT_LIST));
        RESTRICT_COUNT = config.get(category, "restrict_count", RESTRICT_COUNT, PixelmonBankLocaleConfig.cfgRestrictCount).getInt();

        BLACK_LIST = config.get(category, "black_list", BLACK_LIST, PixelmonBankLocaleConfig.cfgBlackList).getStringList();
        for (int i = 0; i < BLACK_LIST.length; i++) {
            BLACK_LIST[i] = BLACK_LIST[i].toLowerCase();
        }
        PokemonOptUtil.BLACK_LIST_POKEMONS = new HashSet<>(Arrays.asList(BLACK_LIST));

        ALLOW_ITEM = config.get(category, "allow_item", ALLOW_ITEM, PixelmonBankLocaleConfig.cfgAllowItem).getBoolean();;
        BLACK_LIST_ITEM = config.get(category, "black_list_item", BLACK_LIST_ITEM, PixelmonBankLocaleConfig.cfgBlackListItem).getStringList();
        for (int i = 0; i < BLACK_LIST_ITEM.length; i++) {
            BLACK_LIST_ITEM[i] = BLACK_LIST_ITEM[i].toLowerCase();
        }

        MAX_COUNT = config.get(category, "max_count", MAX_COUNT, PixelmonBankLocaleConfig.cfgMaxCount).getInt();
//        BLACK_LIST_MOVE = config.get(category, "black_list_move", BLACK_LIST_MOVE, PixelmonBankLocaleConfig.cfgBlackListMove).getStringList();
//        for (int i = 0; i < BLACK_LIST_MOVE.length; i++) {
//            BLACK_LIST_MOVE[i] = BLACK_LIST_MOVE[i].toLowerCase().replaceAll("\\s*", "");
//        }

        MAX_IVS = Math.min((Math.max(config.get(category, "max_ivs", MAX_IVS, PixelmonBankLocaleConfig.cfgMaxIvs).getInt(), 0)), 6);
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
