package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;

import java.util.Optional;

@Command(
        value = {
                "deleteall",
                "delall"
        }
)
@Permissible(PermNodeReference.DELETE_NODE)
public class DeleteAllCmd {

    @CommandProcessor
    public void run(@Sender ICommandSource sender, @Completable(PlayerTabCompleter.class) @Argument ServerPlayerEntity target) {
        ForgeEnvyPlayer targetPlayer = PixelmonBank.getInstance().getPlayerManager().getPlayer(target);
        if (Optional.ofNullable(targetPlayer).isEmpty()) {
            return;
        }
        String uuid = targetPlayer.getUuid().toString();
        if (SyncHandler.getInstance().delAll(uuid)) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getSuccessDeleteMsg(), targetPlayer.getName()), Util.NIL_UUID);
        }
    }
}
