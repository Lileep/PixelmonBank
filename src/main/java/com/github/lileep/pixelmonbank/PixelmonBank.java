package com.github.lileep.pixelmonbank;


import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.SimpleHikariDatabase;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.command.parser.ForgeAnnotationCommandParser;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.github.lileep.pixelmonbank.command.PixelmonBankCmd;
import com.github.lileep.pixelmonbank.config.PixelmonBankConfig;
import com.github.lileep.pixelmonbank.config.PixelmonBankLocaleConfig;
import com.github.lileep.pixelmonbank.data.serializer.PixelmonSerializer;
import com.github.lileep.pixelmonbank.database.PixelmonBankDBManager;
import com.github.lileep.pixelmonbank.database.PixelmonBankQueries;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.Reference;
import com.github.lileep.pixelmonbank.util.PokemonOptUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;

@Mod(Reference.MOD_ID)
public class PixelmonBank {

    public static final Logger LOGGER = LoggerFactory.getLogger(Reference.MOD_ID);
    private static PixelmonBank instance;
    private final ForgePlayerManager playerManager = new ForgePlayerManager();
    private final ForgeCommandFactory commandFactory = new ForgeCommandFactory(ForgeAnnotationCommandParser::new, playerManager);
    private PixelmonBankConfig config;
    private PixelmonBankLocaleConfig locale;
    private Database database;

    public PixelmonBank() {
        // Register the setup method for modloading
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static PixelmonBank getInstance() {
        if (instance == null) {
            instance = new PixelmonBank();
        }
        return instance;
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(PixelmonBankConfig.class);
            this.locale = YamlConfigFactory.getInstance(PixelmonBankLocaleConfig.class);
            PokemonOptUtil.RESTRICT_POKEMONS = new HashSet<>(Arrays.asList(this.config.getRestrictList()));
            PokemonOptUtil.BLACK_LIST_POKEMONS = new HashSet<>(Arrays.asList(this.config.getBlackList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void preInit(final FMLServerAboutToStartEvent event) {
        PixelmonBank.instance = this;
        GuiFactory.setPlatformFactory(new ForgeGuiFactory());

//        PixelmonBank.getInstance().getConfig().loadConfig(event.getModConfigurationDirectory() + "/" + Reference.MOD_ID);
        loadConfig();

        UtilConcurrency.runAsync(() -> {
//            this.database = new PixelmonBankDB(getConfig().getDatabase());
            this.database = new SimpleHikariDatabase(getConfig().getDatabase());

            try (Connection connection = this.database.getConnection();
                 PreparedStatement createDB = connection.prepareStatement(String.format(PixelmonBankQueries.CREATE_DB, getConfig().getDatabase().getDatabase()));
                 PreparedStatement createPbkTable = connection.prepareStatement(String.format(PixelmonBankQueries.CREATE_PBK_TABLE, getConfig().getDatabase().getDatabase()))
            ) {
                createDB.executeUpdate();
                createPbkTable.executeUpdate();
                checkAndUpdateDB(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        PixelmonBankDBManager.getInstance();
    }

    private void checkAndUpdateDB(Connection connection) {
        LOGGER.info("Checking for DB updating...");
        try (PreparedStatement checkColOrder = connection.prepareStatement(String.format(PixelmonBankQueries.CHECK_COL_ORDER, getConfig().getDatabase().getDatabase()))
        ) {
            //Check columns order
            ResultSet rs = checkColOrder.executeQuery();
            int pixelmonUuidIndex = -1;
            int timePointIndex = -1;
            int visibleIndex = -1;
            int withdrawTimeIndex = -1;
            int pixelmonNameIndex = -1;
            int index = 0;
            while (rs.next()) {
                String columnName = rs.getString("Field");
                if ("pixelmon_uuid".equals(columnName)) {
                    pixelmonUuidIndex = index;
                } else if ("time_point".equals(columnName)) {
                    timePointIndex = index;
                } else if ("visible".equals(columnName)) {
                    visibleIndex = index;
                } else if ("withdraw_time".equals(columnName)) {
                    withdrawTimeIndex = index;
                } else if ("pixelmon_name".equals(columnName)) {
                    pixelmonNameIndex = index;
                }
                index++;
            }
            //Remove the pixelmon_uuid column and add id column
            if (pixelmonUuidIndex != -1) {
                try (PreparedStatement removePUuid = connection.prepareStatement(String.format(PixelmonBankQueries.REMOVE_P_UUID, getConfig().getDatabase().getDatabase()));
                     PreparedStatement addId = connection.prepareStatement(String.format(PixelmonBankQueries.ADD_ID, getConfig().getDatabase().getDatabase()))
                ) {
                    removePUuid.executeUpdate();
                    addId.executeUpdate();
                }
            }
            //Rename time_point 2 send_time
            if (timePointIndex != -1) {
                try (PreparedStatement removeTimePointIndex = connection.prepareStatement(String.format(PixelmonBankQueries.REMOVE_TIME_POINT_INDEX, getConfig().getDatabase().getDatabase()));
                     PreparedStatement renameTimePoint = connection.prepareStatement(String.format(PixelmonBankQueries.RENAME_TIME_POINT, getConfig().getDatabase().getDatabase()))
                ) {
                    removeTimePointIndex.executeUpdate();
                    renameTimePoint.executeUpdate();
                }
            }
            //Remove the visible column
            if (visibleIndex != -1) {
                try (PreparedStatement removeOldData = connection.prepareStatement(String.format(PixelmonBankQueries.REMOVE_OLD_DATA, getConfig().getDatabase().getDatabase()));
                     PreparedStatement removeVisible = connection.prepareStatement(String.format(PixelmonBankQueries.REMOVE_VISIBLE, getConfig().getDatabase().getDatabase()))
                ) {
                    removeOldData.executeUpdate();
                    removeVisible.executeUpdate();
                }
            }
            //Add withdraw time column
            if (withdrawTimeIndex == -1) {
                try (PreparedStatement addWithdrawTime = connection.prepareStatement(String.format(PixelmonBankQueries.ADD_WITHDRAW_TIME, getConfig().getDatabase().getDatabase()))
                ) {
                    addWithdrawTime.executeUpdate();
                }
            }

            //Add pixelmon name column and its index
            if (pixelmonNameIndex == -1) {
                try (PreparedStatement addPixelmonName = connection.prepareStatement(String.format(PixelmonBankQueries.ADD_PIXELMON_NAME, getConfig().getDatabase().getDatabase()));
                     PreparedStatement addPixelmonNameIndex = connection.prepareStatement(String.format(PixelmonBankQueries.ADD_PIXELMON_NAME_INDEX, getConfig().getDatabase().getDatabase()))
                ) {
                    addPixelmonName.executeUpdate();
                    addPixelmonNameIndex.executeUpdate();
                }
            }

            //Fix timestamp NPE
            try (PreparedStatement searchTimestamp = connection.prepareStatement(String.format(PixelmonBankQueries.SEARCH_TIMESTAMP, getConfig().getDatabase().getDatabase()))
            ) {
                rs = searchTimestamp.executeQuery();
                while (rs.next()) {
                    String columnName = rs.getString(1);
                    String type = rs.getString(2);
                    if ("TIMESTAMP".equalsIgnoreCase(type)) {
                        PreparedStatement updateTimestamp = connection.prepareStatement(String.format(PixelmonBankQueries.UPDATE_TIMESTAMP, getConfig().getDatabase().getDatabase(), columnName, "send_time".equals(columnName) ? "CURRENT_TIMESTAMP":"NULL"));
                        updateTimestamp.executeUpdate();
                    }
                }
            }

            LOGGER.info("DB checking done!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public void init(final FMLServerStartingEvent event) {
        SyncHandler.getInstance().register(new PixelmonSerializer());
    }

    @SubscribeEvent
    public void onCommandRegistration(RegisterCommandsEvent event) {
        this.commandFactory.registerCommand(event.getDispatcher(), this.commandFactory.parseCommand(new PixelmonBankCmd()));
    }

    public ForgePlayerManager getPlayerManager() {
        return playerManager;
    }

    public Database getDatabase() {
        return database;
    }

    public PixelmonBankConfig getConfig() {
        return config;
    }

    public PixelmonBankLocaleConfig getLocale() {
        return locale;
    }
}
