package com.github.lileep.pixelmonbank.config;

import net.minecraftforge.common.config.Configuration;

public class PixelmonBankLocaleConfig {

    public static Configuration config;

    public static String prefix = "&e[Pixelmon Bank]";

    public static String argOperation = "operation";
    public static String argParam = "parameter";
    public static String argSlot = "slot";

    public static String configLoadFail = "&cFail to load config.";
    public static String configLoadSuccess = "&aSuccessfully load config.";
    public static String configReloading = "&7Reloading config...";

    public static String cfgCategoryGeneral = "General settings for Pixelmon Bank";
    public static String cfgAllowLegendary = "Whether allow legendaries to be sent to Pixelmon Bank";
    public static String cfgAllowUltraBeast = "Whether allow ultra beasts to be sent to Pixelmon Bank";
    public static String cfgBlackList = "Listed pixelmons will be prohibited from being sent to Pixelmon Bank(Comma separate)";
    public static String cfgBlackListItem = "Pixelmons hold listed items will be prohibited from being sent to Pixelmon Bank";
    public static String cfgBlackListMove = "Pixelmons have listed moves will be prohibited from being sent to Pixelmon Bank";
    public static String cfgMaxIvs = "Pixelmons whose max iv(31) amount above this value will be prohibited from being sent to Pixelmon Bank";
    public static String cfgCountHyperTrained = "Take hyper trained(used bottle caps) ivs into account for max ivs checking";

    public static String cfgCategoryDB = "Database settings for Pixelmon Bank. Restart server to apply instead of reload";
    public static String cfgServerName = "Name of your server. Pixelmon Bank will make this a database field";
    public static String cfgDBIP = "IP address of database";
    public static String cfgDBPort = "Port of database";
    public static String cfgDBName = "Name of database. Please manually create this database before using Pixelmon Bank";
    public static String cfgDBUsername = "User name of database";
    public static String cfgDBPasswd = "Password of database";

    public static String cfgCategoryLocale = "Language file for Pixelmon Bank";

    public static String playerOnly = "&cOnly players can use this command.";
    public static String slotNumInvalid = "&cInvalid slot number given.";
    public static String slotNumLimited = "&cSlot number must be between 1 and 6.";
    public static String nothing = "&cNothing is in that slot.";
    public static String partyLastOne = "&cYou must have more than one none egg Pokemon in your party to do this.";
    public static String noLegendary = "&cYou can't send legendary pixelmons to Pixelmon Bank.";
    public static String noUltrabeast = "&cYou can't send ultra beasts to Pixelmon Bank.";
    public static String noBlackList = "&c%s is in black list, cannot be sent to Pixelmon Bank.";
    public static String noMaxIVs = "&cPixelmon Bank only allow pixelmons that have no more than %d max iv to be sent to.";
    public static String findNone = "&cThere's no such pixelmon in your Pixelmon Bank.";
    public static String haveNone = "&cYou don't have any pixelmons in your Pixelmon Bank.";
    public static String pageInvalid = "&cPlease enter an integer as the page number.";

    public static String successSendMsg = "&aSuccessfully send your %s to Pixelmon Bank!";
    public static String successGetMsg = "&aSuccessfully get your %s from Pixelmon Bank!";
    public static String successGetAllMsg = "&aSuccessfully get all of your pixelmons from Pixelmon Bank!";
    public static String successDeleteMsg = "&aSuccessfully delete %s's pixelmons in Pixelmon Bank.";

    public static String pixelmonLevel = "lv";
    public static String pixelmonDynamaxLevel = "dynamax lv";
    public static String pixelmonCanGigantamax = "can gigantamax";
    public static String pixelmonHeld = "held";
    public static String pixelmonIv = "iv";
    public static String pixelmonEv = "ev";
    public static String pbankGuiPrev = "&aPrevious Page";
    public static String pbankGuiNext = "&aNext Page";
    public static String pbankGuiInfo1 = "&eYou have a total of %d pixelmons in Pixelmon Bank";
    public static String pbankGuiInfo2 = "&eClick here to retrieve all pixelmons from Bank";

    public static String title = "&ePixelmon Bank";

    public static void loadConfig(final Configuration configuration) {
        String category = "locale";
        config = configuration;
//        cfgCategoryLocale = config.get(category, "cfg-category-locale", cfgCategoryLocale).getString();
        config.addCustomCategoryComment(category, cfgCategoryLocale);

        prefix = config.get(category, "prefix", prefix).getString();

        argOperation = config.get(category, "arg-operation", argOperation).getString();
        argParam = config.get(category, "arg-param", argParam).getString();
        argSlot = config.get(category, "arg-slot", argSlot).getString();

//        cfgCategoryGeneral = config.get(category, "cfg-category-general", cfgCategoryGeneral).getString();
//        cfgAllowLegendary = config.get(category, "cfg-info-allow-legendary", cfgAllowLegendary).getString();
//        cfgAllowUltraBeast = config.get(category, "cfg-info-allow-ultrabeast", cfgAllowUltraBeast).getString();
//        cfgBlackList = config.get(category, "cfg-info-blacklist", cfgBlackList).getString();
//        cfgMaxIvs = config.get(category, "cfg-info-max-ivs", cfgMaxIvs).getString();
//        cfgCountHyperTrained = config.get(category, "cfg-info-count-hyper-trained", cfgCountHyperTrained).getString();
//
//        cfgCategoryDB = config.get(category, "cfg-category-db", cfgCategoryDB).getString();
//        cfgServerName = config.get(category, "cfg-info-server-name", cfgServerName).getString();
//        cfgDBIP = config.get(category, "cfg-info-db-ip", cfgDBIP).getString();
//        cfgDBPort = config.get(category, "cfg-info-db-port", cfgDBPort).getString();
//        cfgDBName = config.get(category, "cfg-info-db-name", cfgDBName).getString();
//        cfgDBUsername = config.get(category, "cfg-info-db-username", cfgDBUsername).getString();
//        cfgDBPasswd = config.get(category, "cfg-info-db-passwd", cfgDBPasswd).getString();

        playerOnly = config.get(category, "err-player-only", playerOnly).getString();
        slotNumInvalid = config.get(category, "err-slot-number-invalid", slotNumInvalid).getString();
        slotNumLimited = config.get(category, "err-slot-number-limited", slotNumLimited).getString();
        nothing = config.get(category, "err-nothing", nothing).getString();
        partyLastOne = config.get(category, "err-party-last-one", partyLastOne).getString();
        noLegendary = config.get(category, "err-no-legendary", noLegendary).getString();
        noUltrabeast = config.get(category, "err-no-ultrabeast", noUltrabeast).getString();
        noBlackList = config.get(category, "err-no-blacklist", noBlackList).getString();
        noMaxIVs = config.get(category, "err-no-max-ivs", noMaxIVs).getString();
        findNone = config.get(category, "err-find-none", findNone).getString();
        haveNone = config.get(category, "err-have-none", haveNone).getString();
        pageInvalid = config.get(category, "err-page-invalid", pageInvalid).getString();

        configLoadFail = config.get(category, "config-load-fail", configLoadFail).getString();
        configLoadSuccess = config.get(category, "config-load-success", configLoadSuccess).getString();
        configReloading = config.get(category, "config-reloading", configReloading).getString();

        successSendMsg = config.get(category, "success-send", successSendMsg).getString();
        successGetMsg = config.get(category, "success-get", successGetMsg).getString();
        successGetAllMsg = config.get(category, "success-get-all", successGetAllMsg).getString();
        successDeleteMsg = config.get(category, "success-delete", successDeleteMsg).getString();

        pixelmonLevel = config.get(category, "pixelmon-level", pixelmonLevel).getString();
        pixelmonDynamaxLevel = config.get(category, "pixelmon-dynamax-level", pixelmonDynamaxLevel).getString();
        pixelmonCanGigantamax = config.get(category, "pixelmon-can-gigantamax", pixelmonCanGigantamax).getString();
        pixelmonHeld = config.get(category, "pixelmon-held", pixelmonHeld).getString();
        pixelmonIv = config.get(category, "pixelmon-iv", pixelmonIv).getString();
        pixelmonEv = config.get(category, "pixelmon-ev", pixelmonEv).getString();

        pbankGuiPrev = config.get(category, "pbank-gui-prev", pbankGuiPrev).getString();
        pbankGuiNext = config.get(category, "pbank-gui-next", pbankGuiNext).getString();
        pbankGuiInfo1 = config.get(category, "pbank-gui-info-1", pbankGuiInfo1).getString();
        pbankGuiInfo2 = config.get(category, "pbank-gui-info-2", pbankGuiInfo2).getString();

        title = config.get(category, "title", title).getString();

        if (config.hasChanged()) {
            config.save();
        }
    }
}
