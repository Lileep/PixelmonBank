package com.github.lileep.pixelmonbank.config;

import net.minecraftforge.common.config.Configuration;

public class PixelmonBankLocaleConfig {

    public static Configuration config;

    public static String prefix = "&e[Pixelmon Bank]";

    public static String argOperation = "operation";
    public static String argSlot = "slot";

    public static String playerOnly = "&cOnly players can use this command.";
    public static String slotNumInvalid = "&cInvalid slot number given.";
    public static String slotNumLimited = "&cSlot number must be between 1 and 6.";
    public static String nothing = "&cNothing is in that slot.";
    public static String partyLastOne = "&cYou must have more than one none egg Pokemon in your party to do this.";
    public static String findNone = "&cThere's no such pixelmon in your Pixelmon Bank.";
    public static String haveNone = "&cYou don't have any pixelmons in your Pixelmon Bank.";

    public static String configLoadFail = "&cFail to load config.";
    public static String configLoadSuccess = "&aSuccessfully load config.";
    public static String configReloading = "&7Reloading config...";

    public static String successSendMsg = "&aSuccessfully send your %s to Pixelmon Bank!";
    public static String successGetMsg = "&aSuccessfully get your %s from Pixelmon Bank!";
    public static String successGetAllMsg = "&aSuccessfully get all of your pixelmons from Pixelmon Bank!";
    public static String successDeleteMsg = "&aSuccessfully delete %s's pixelmons in Pixelmon Bank.";

    public static String title = "&ePixelmon Bank";

    public static void loadConfig(final Configuration configuration) {
        String category = "locale";
        (config = configuration).addCustomCategoryComment(category, "Language file for Pixelmon Bank");

        prefix = config.get(category, "prefix", prefix).getString();

        argOperation = config.get(category, "arg-operation", argOperation).getString();
        argSlot = config.get(category, "arg-slot", argSlot).getString();

        playerOnly = config.get(category, "err-player-only", playerOnly).getString();
        slotNumInvalid = config.get(category, "err-slot-number-invalid", slotNumInvalid).getString();
        slotNumLimited = config.get(category, "err-slot-number-limited", slotNumLimited).getString();
        nothing = config.get(category, "err-nothing", nothing).getString();
        partyLastOne = config.get(category, "err-party-last-one", partyLastOne).getString();
        findNone = config.get(category, "err-find-none", findNone).getString();
        haveNone = config.get(category, "err-have-none", haveNone).getString();

        configLoadFail = config.get(category, "config-load-fail", configLoadFail).getString();
        configLoadSuccess = config.get(category, "config-load-success", configLoadSuccess).getString();
        configReloading = config.get(category, "config-reloading", configReloading).getString();

        successSendMsg = config.get(category, "success-send", successSendMsg).getString();
        successGetMsg = config.get(category, "success-get", successGetMsg).getString();
        successGetAllMsg = config.get(category, "success-get-all", successGetAllMsg).getString();
        successDeleteMsg = config.get(category, "success-delete", successDeleteMsg).getString();

        title = config.get(category, "title", title).getString();

        if (config.hasChanged()) {
            config.save();
        }
    }
}
