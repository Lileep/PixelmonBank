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
import com.github.lileep.pixelmonbank.lib.PixelmonReference;
import com.github.lileep.pixelmonbank.lib.Reference;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Mod(
        modid = Reference.MOD_ID,
        name = Reference.NAME,
        version = Reference.VERSION,
        acceptableRemoteVersions = "*",
        dependencies = Reference.DEPENDENCIES
)
public class PixelmonBank {

    @Mod.Instance(Reference.MOD_ID)
    public static PixelmonBank instance;

    public Configuration config;
    public static File configDirectory;

    private final ForgePlayerManager playerManager = new ForgePlayerManager();
    private final ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private Database database;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        GuiFactory.setPlatformFactory(new ForgeGuiFactory());
        PixelmonBank.instance = this;
        PixelmonBank.configDirectory = event.getModConfigurationDirectory();
        (this.config = new Configuration(event.getSuggestedConfigurationFile())).load();
        PixelmonBankConfig.loadConfig(this.config);

    }

    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        EnumSpecies.legendaries.forEach(species -> PixelmonReference.LEGENDARIES.add(species.getPokemonName()));

        UtilConcurrency.runAsync(() -> {
            this.database = new PixelmonBankDatabase();

            try (Connection connection = this.database.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(PixelmonBankQueries.CREATE_TABLE)) {
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        PixelmonBankDBManager.getInstance();
        SyncHandler.getInstance().register(new PixelmonSerializer());
    }

    @Mod.EventHandler
    public void onServerStart(final FMLServerStartingEvent event) {
//        event.registerServerCommand(new PixelmonBankCmd());
        this.commandFactory.registerCommand(event.getServer(), new PixelmonBankCmd());
    }

    public ForgePlayerManager getPlayerManager() {
        return playerManager;
    }

    public Database getDatabase() {
        return database;
    }
}
