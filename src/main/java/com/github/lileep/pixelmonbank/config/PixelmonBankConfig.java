package com.github.lileep.pixelmonbank.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigPath("config/pixelmonbank/config.yml")
@ConfigSerializable
public class PixelmonBankConfig extends AbstractYamlConfig {

    private SQLDatabaseDetails database = new SQLDatabaseDetails("pixelmon-bank-pool", "127.0.0.1", 3306,
            "root", "root", "pixelbank");

    private String serverName = "default_server";
//    public static Configuration config;

    private boolean allowLegendary = true;
    private boolean allowUltrabeast = true;
    private boolean allowEgg = false;
    private boolean allowUntradeable = false;
    private boolean sterilizeWhenWithdraw = false;
    private boolean untradifyWhenWithdraw = false;
    private String[] restrictList = new String[0];
    private int restrictCount = 1;
    private String[] blackList = new String[0];
    private boolean allowItem = true;
    private String[] blackListItem = new String[0];
    //    private String[] BLACK_LIST_MOVE = new String[0];
    private int maxCount = 0;
    private int maxIvs = 6;
    private boolean countHyperTrained = false;
//    private String locale = "en_us";
//    private String localeOld = locale;

    public PixelmonBankConfig() {
        super();
    }

    public SQLDatabaseDetails getDatabase() {
        return database;
    }

    public String getServerName() {
        return serverName;
    }

    public boolean isAllowLegendary() {
        return allowLegendary;
    }

    public boolean isAllowUltrabeast() {
        return allowUltrabeast;
    }

    public boolean isAllowEgg() {
        return allowEgg;
    }

    public boolean isAllowUntradeable() {
        return allowUntradeable;
    }

    public boolean isSterilizeWhenWithdraw() {
        return sterilizeWhenWithdraw;
    }

    public boolean isUntradifyWhenWithdraw() {
        return untradifyWhenWithdraw;
    }

    public String[] getRestrictList() {
        return restrictList;
    }

    public int getRestrictCount() {
        return restrictCount;
    }

    public String[] getBlackList() {
        return blackList;
    }

    public boolean isAllowItem() {
        return allowItem;
    }

    public String[] getBlackListItem() {
        return blackListItem;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public int getMaxIvs() {
        return maxIvs;
    }

    public boolean isCountHyperTrained() {
        return countHyperTrained;
    }
}
