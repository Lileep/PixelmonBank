package com.github.lileep.pixelmonbank;


import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.database.Database;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.github.lileep.pixelmonbank.command.PixelmonBankCmd;
import com.github.lileep.pixelmonbank.config.PixelmonBankConfig;
import com.github.lileep.pixelmonbank.data.serializer.PixelmonSerializer;
import com.github.lileep.pixelmonbank.database.PixelmonBankDBManager;
import com.github.lileep.pixelmonbank.database.PixelmonBankQueries;
import com.github.lileep.pixelmonbank.database.impl.PixelmonBankDatabase;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Mod(
        modid = Reference.MOD_ID,
        name = Reference.NAME,
        version = Reference.VERSION,
        acceptableRemoteVersions = "*",
        dependencies = Reference.DEPENDENCIES
)
public class PixelmonBank {
    public static final Logger LOGGER = LoggerFactory.getLogger(Reference.MOD_ID);

    @Mod.Instance(Reference.MOD_ID)
    public static PixelmonBank instance;

    private final ForgePlayerManager playerManager = new ForgePlayerManager();
    private final ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private Database database;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        GuiFactory.setPlatformFactory(new ForgeGuiFactory());
        PixelmonBank.instance = this;

        PixelmonBankConfig.loadConfig(event.getModConfigurationDirectory() + "/" + Reference.MOD_ID);

    }

    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {

        UtilConcurrency.runAsync(() -> {
            this.database = new PixelmonBankDatabase();

            try (Connection connection = this.database.getConnection();
                 PreparedStatement createDB = connection.prepareStatement(String.format(PixelmonBankQueries.CREATE_DB, PixelmonBankConfig.DB_DBNAME));
                 PreparedStatement createPbkTable = connection.prepareStatement(String.format(PixelmonBankQueries.CREATE_PBK_TABLE, PixelmonBankConfig.DB_DBNAME));
                 PreparedStatement createPlayerInfoTable = connection.prepareStatement(String.format(PixelmonBankQueries.CREATE_PLAYER_TABLE, PixelmonBankConfig.DB_DBNAME))
            ) {
                createDB.executeUpdate();
                createPbkTable.executeUpdate();
                createPlayerInfoTable.executeUpdate();
                checkAndUpdateDB(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        PixelmonBankDBManager.getInstance();
        SyncHandler.getInstance().register(new PixelmonSerializer());
    }

    private void checkAndUpdateDB(Connection connection) {
        LOGGER.info("Checking for DB updating...");
        try (PreparedStatement checkColOrder = connection.prepareStatement(String.format(PixelmonBankQueries.CHECK_COL_ORDER, PixelmonBankConfig.DB_DBNAME))
        ) {
            //Check columns order
            ResultSet rs = checkColOrder.executeQuery();
            int pixelmonUuidIndex = -1;
            int timePointIndex = -1;
            int visibleIndex = -1;
            int withdrawTimeIndex = -1;
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
                }
                index++;
            }
            //Remove the pixelmon_uuid column and add id column
            if (pixelmonUuidIndex != -1) {
                try(PreparedStatement removePUuid = connection.prepareStatement(String.format(PixelmonBankQueries.REMOVE_P_UUID, PixelmonBankConfig.DB_DBNAME));
                    PreparedStatement addId = connection.prepareStatement(String.format(PixelmonBankQueries.ADD_ID, PixelmonBankConfig.DB_DBNAME))
                ) {
                    removePUuid.executeUpdate();
                    addId.executeUpdate();
                }
            }
            //Rename time_point 2 send_time
            if (timePointIndex != -1) {
                try(PreparedStatement removeTimePointIndex = connection.prepareStatement(String.format(PixelmonBankQueries.REMOVE_TIME_POINT_INDEX, PixelmonBankConfig.DB_DBNAME));
                    PreparedStatement renameTimePoint = connection.prepareStatement(String.format(PixelmonBankQueries.RENAME_TIME_POINT, PixelmonBankConfig.DB_DBNAME))
                ) {
                    removeTimePointIndex.executeUpdate();
                    renameTimePoint.executeUpdate();
                }
            }
            //Remove the visible column
            if (visibleIndex != -1) {
                try(PreparedStatement removeOldData = connection.prepareStatement(String.format(PixelmonBankQueries.REMOVE_OLD_DATA, PixelmonBankConfig.DB_DBNAME));
                    PreparedStatement removeVisible = connection.prepareStatement(String.format(PixelmonBankQueries.REMOVE_VISIBLE, PixelmonBankConfig.DB_DBNAME))
                ) {
                    removeOldData.executeUpdate();
                    removeVisible.executeUpdate();
                }
            }
            //Add withdraw time column
            if (withdrawTimeIndex == -1) {
                try(PreparedStatement addWithdrawTime = connection.prepareStatement(String.format(PixelmonBankQueries.ADD_WITHDRAW_TIME, PixelmonBankConfig.DB_DBNAME))
                ) {
                    addWithdrawTime.executeUpdate();
                }
            }
            LOGGER.info("DB checking done!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Mod.EventHandler
    public void onServerStart(final FMLServerStartingEvent event) {
        this.commandFactory.registerCommand(event.getServer(), new PixelmonBankCmd());
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        UtilConcurrency.runAsync(() -> {
            try (Connection connection = this.database.getConnection();
                 PreparedStatement getPlayerInfo = connection.prepareStatement(String.format(PixelmonBankQueries.SELECT_PLAYER_INFO, PixelmonBankConfig.DB_DBNAME))
            ) {
                getPlayerInfo.setString(1, player.getUniqueID().toString());
                ResultSet resultSet = getPlayerInfo.executeQuery();
                if (!resultSet.next()) {
                    try(PreparedStatement initPlayerInfo = connection.prepareStatement(String.format(PixelmonBankQueries.INIT_PLAYER_INFO, PixelmonBankConfig.DB_DBNAME))) {
                        initPlayerInfo.setString(1, player.getUniqueID().toString());
                        if (initPlayerInfo.executeUpdate() > 0) {
                            LOGGER.info(String.format("Player %s doesn't have Pixelmon Bank records before, inited", player.getDisplayName().getFormattedText()));
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public ForgePlayerManager getPlayerManager() {
        return playerManager;
    }

    public Database getDatabase() {
        return database;
    }
}
