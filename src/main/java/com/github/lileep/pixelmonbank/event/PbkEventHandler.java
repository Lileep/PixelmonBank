package com.github.lileep.pixelmonbank.event;

import com.envyful.api.concurrency.UtilConcurrency;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.database.PixelmonBankQueries;
import com.github.lileep.pixelmonbank.lib.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class PbkEventHandler {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        UtilConcurrency.runAsync(() -> {
            try (Connection connection = PixelmonBank.getInstance().getDatabase().getConnection();
                 PreparedStatement getPlayerInfo = connection.prepareStatement(String.format(PixelmonBankQueries.SELECT_PLAYER_INFO, "*", PixelmonBank.getInstance().getConfig().getDatabase().getDatabase()))
            ) {
                getPlayerInfo.setString(1, player.getStringUUID());
                ResultSet resultSet = getPlayerInfo.executeQuery();
                if (!resultSet.next()) {
                    try(PreparedStatement initPlayerInfo = connection.prepareStatement(String.format(PixelmonBankQueries.INIT_PLAYER_INFO, PixelmonBank.getInstance().getConfig().getDatabase().getDatabase()))) {
                        initPlayerInfo.setString(1, player.getStringUUID());
                        if (initPlayerInfo.executeUpdate() > 0) {
                            PixelmonBank.LOGGER.info(String.format("Player %s doesn't have Pixelmon Bank records before, inited", player.getDisplayName().getString()));
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
