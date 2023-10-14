package com.github.lileep.pixelmonbank.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigPath("config/pixelmonbank/locale.yml")
@ConfigSerializable
public class PixelmonBankLocaleConfig extends AbstractYamlConfig {

    private String prefix = "&e[Pixelmon Bank]";

    private String argOperation = "operation";
    private String argParam = "parameter";
    private String argSlot = "slot";

    private String configLoadFail = "&cFail to load config.";
    private String configLoadSuccess = "&aSuccessfully load config.";
    private String configReloading = "&7Reloading config...";

    private String cfgCategoryGeneral = "General settings for Pixelmon Bank";
    private String cfgAllowLegendary = "Whether allow legendaries to be sent to Pixelmon Bank";
    private String cfgAllowUltraBeast = "Whether allow ultra beasts to be sent to Pixelmon Bank";
    private String cfgAllowEgg = "Whether allow eggs to be sent to Pixelmon Bank";
    private String cfgAllowUntradeable = "Whether allow untradeable pixelmons to be sent to Pixelmon Bank";
    private String cfgBlackList = "Listed pixelmons will be prohibited from being sent to Pixelmon Bank(Comma separate)";
    private String cfgBlackListItem = "Pixelmons hold listed items will be prohibited from being sent to Pixelmon Bank";
    private String cfgBlackListMove = "Pixelmons have listed moves will be prohibited from being sent to Pixelmon Bank";
    private String cfgMaxIvs = "Pixelmons whose max iv(31) amount above this value will be prohibited from being sent to Pixelmon Bank";
    private String cfgCountHyperTrained = "Take hyper trained(used bottle caps) ivs into account for max ivs checking";

    private String cfgCategoryDB = "Database settings for Pixelmon Bank. Restart server to apply instead of reload";
    private String cfgServerName = "Name of your server. Pixelmon Bank will make this a database field";
    private String cfgDBIP = "IP address of database";
    private String cfgDBPort = "Port of database";
    private String cfgDBName = "Name of database. Please manually create this database before using Pixelmon Bank";
    private String cfgDBUsername = "User name of database";
    private String cfgDBPasswd = "Password of database";

    private String cfgCategoryLocale = "Language file for Pixelmon Bank";

    private String playerOnly = "&cOnly players can use this command.";
    private String slotNumInvalid = "&cInvalid slot number given.";
    private String slotNumLimited = "&cSlot number must be between 1 and 6.";
    private String nothing = "&cNothing is in that slot.";
    private String partyLastOne = "&cYou must have more than one no egg pixelmon in your party to do this.";
    private String noLegendary = "&cYou can't send legendary pixelmons to Pixelmon Bank.";
    private String noUltrabeast = "&cYou can't send ultra beasts to Pixelmon Bank.";
    private String noEgg = "&cYou can't send pixelmon eggs to Pixelmon Bank.";
    private String noUntradeable = "&cYou can't send untradeable pixelmons to Pixelmon Bank.";
    private String noBlackList = "&c%s is in black list, cannot be sent to Pixelmon Bank.";
    private String noRestrictList = "&cThe amount of %s has reached the maximum value in the restrict list, cannot be sent to Pixelmon Bank.";
    private String noHeldItem = "&cPixelmons holding items cannot be sent to Pixelmon Bank.";
    private String noBlackListItem = "&cItem %s is in black list, pixelmons holding this cannot be sent to Pixelmon Bank.";
    private String reachMax = "&cPixelmon Bank has already stored %d of your pixelmons, which reaching the max value.";
    private String noMaxIVs = "&cPixelmon Bank only allow pixelmons that have no more than %d max iv to be sent to.";
    private String findNone = "&cThere's no such pixelmon in your Pixelmon Bank.";
    private String haveNone = "&cYou don't have any pixelmons in your Pixelmon Bank.";
    private String pageInvalid = "&cPlease enter an integer as the page number.";

    private String successSendMsg = "&aSuccessfully send your %s to Pixelmon Bank!";
    private String successGetMsg = "&aSuccessfully get your %s from Pixelmon Bank!";
    private String successGetAllMsg = "&aSuccessfully get all of your pixelmons from Pixelmon Bank!";
    private String successDeleteMsg = "&aSuccessfully delete %s's pixelmons in Pixelmon Bank.";

    private String pixelmonLevel = "lv";
    private String pixelmonDynamaxLevel = "dynamax lv";
    private String pixelmonCanGigantamax = "can gigantamax";
    private String pixelmonHeld = "held";
    private String pixelmonIv = "iv";
    private String pixelmonEv = "ev";
    private String pbankGuiPrev = "&aPrevious Page";
    private String pbankGuiNext = "&aNext Page";
    private String pbankGuiGet = "&eClick here to retrieve this pixelmon";
    private String pbankGuiInfo1 = "&eYou have a total of %d pixelmons in Pixelmon Bank";
    private String pbankGuiInfo2 = "&eClick here to retrieve all pixelmons from Bank";

    private String title = "&ePixelmon Bank";

    public PixelmonBankLocaleConfig(){
        super();
    }

    public String getPrefix() {
        return prefix;
    }

    public String getArgOperation() {
        return argOperation;
    }

    public String getArgParam() {
        return argParam;
    }

    public String getArgSlot() {
        return argSlot;
    }

    public String getConfigLoadFail() {
        return configLoadFail;
    }

    public String getConfigLoadSuccess() {
        return configLoadSuccess;
    }

    public String getConfigReloading() {
        return configReloading;
    }

    public String getCfgCategoryGeneral() {
        return cfgCategoryGeneral;
    }

    public String getCfgAllowLegendary() {
        return cfgAllowLegendary;
    }

    public String getCfgAllowUltraBeast() {
        return cfgAllowUltraBeast;
    }

    public String getCfgAllowEgg() {
        return cfgAllowEgg;
    }

    public String getCfgAllowUntradeable() {
        return cfgAllowUntradeable;
    }

    public String getCfgBlackList() {
        return cfgBlackList;
    }

    public String getCfgBlackListItem() {
        return cfgBlackListItem;
    }

    public String getCfgBlackListMove() {
        return cfgBlackListMove;
    }

    public String getCfgMaxIvs() {
        return cfgMaxIvs;
    }

    public String getCfgCountHyperTrained() {
        return cfgCountHyperTrained;
    }

    public String getCfgCategoryDB() {
        return cfgCategoryDB;
    }

    public String getCfgServerName() {
        return cfgServerName;
    }

    public String getCfgDBIP() {
        return cfgDBIP;
    }

    public String getCfgDBPort() {
        return cfgDBPort;
    }

    public String getCfgDBName() {
        return cfgDBName;
    }

    public String getCfgDBUsername() {
        return cfgDBUsername;
    }

    public String getCfgDBPasswd() {
        return cfgDBPasswd;
    }

    public String getCfgCategoryLocale() {
        return cfgCategoryLocale;
    }

    public String getPlayerOnly() {
        return playerOnly;
    }

    public String getSlotNumInvalid() {
        return slotNumInvalid;
    }

    public String getSlotNumLimited() {
        return slotNumLimited;
    }

    public String getNothing() {
        return nothing;
    }

    public String getPartyLastOne() {
        return partyLastOne;
    }

    public String getNoLegendary() {
        return noLegendary;
    }

    public String getNoUltrabeast() {
        return noUltrabeast;
    }

    public String getNoEgg() {
        return noEgg;
    }

    public String getNoUntradeable() {
        return noUntradeable;
    }

    public String getNoRestrictList() {
        return noRestrictList;
    }

    public String getNoBlackList() {
        return noBlackList;
    }

    public String getNoHeldItem() {
        return noHeldItem;
    }

    public String getNoBlackListItem() {
        return noBlackListItem;
    }

    public String getReachMax() {
        return reachMax;
    }

    public String getNoMaxIVs() {
        return noMaxIVs;
    }

    public String getFindNone() {
        return findNone;
    }

    public String getHaveNone() {
        return haveNone;
    }

    public String getPageInvalid() {
        return pageInvalid;
    }

    public String getSuccessSendMsg() {
        return successSendMsg;
    }

    public String getSuccessGetMsg() {
        return successGetMsg;
    }

    public String getSuccessGetAllMsg() {
        return successGetAllMsg;
    }

    public String getSuccessDeleteMsg() {
        return successDeleteMsg;
    }

    public String getPixelmonLevel() {
        return pixelmonLevel;
    }

    public String getPixelmonDynamaxLevel() {
        return pixelmonDynamaxLevel;
    }

    public String getPixelmonCanGigantamax() {
        return pixelmonCanGigantamax;
    }

    public String getPixelmonHeld() {
        return pixelmonHeld;
    }

    public String getPixelmonIv() {
        return pixelmonIv;
    }

    public String getPixelmonEv() {
        return pixelmonEv;
    }

    public String getPbankGuiPrev() {
        return pbankGuiPrev;
    }

    public String getPbankGuiNext() {
        return pbankGuiNext;
    }

    public String getPbankGuiGet() {
        return pbankGuiGet;
    }

    public String getPbankGuiInfo1() {
        return pbankGuiInfo1;
    }

    public String getPbankGuiInfo2() {
        return pbankGuiInfo2;
    }

    public String getTitle() {
        return title;
    }
}
