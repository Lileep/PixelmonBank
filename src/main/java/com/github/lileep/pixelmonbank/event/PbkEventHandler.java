package com.github.lileep.pixelmonbank.event;

import com.envyful.api.concurrency.UtilConcurrency;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.config.PixelmonBankConfig;
import com.github.lileep.pixelmonbank.database.PixelmonBankQueries;
import com.github.lileep.pixelmonbank.lib.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class PbkEventHandler {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        UtilConcurrency.runAsync(() -> {
            try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
                 PreparedStatement getPlayerInfo = connection.prepareStatement(String.format(PixelmonBankQueries.SELECT_PLAYER_INFO, "*", PixelmonBankConfig.DB_DBNAME))
            ) {
                getPlayerInfo.setString(1, player.getUniqueID().toString());
                ResultSet resultSet = getPlayerInfo.executeQuery();
                if (!resultSet.next()) {
                    try(PreparedStatement initPlayerInfo = connection.prepareStatement(String.format(PixelmonBankQueries.INIT_PLAYER_INFO, PixelmonBankConfig.DB_DBNAME))) {
                        initPlayerInfo.setString(1, player.getUniqueID().toString());
                        if (initPlayerInfo.executeUpdate() > 0) {
                            PixelmonBank.LOGGER.info(String.format("Player %s doesn't have Pixelmon Bank records before, inited", player.getDisplayName().getFormattedText()));
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
