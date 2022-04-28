package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.envyful.api.player.EnvyPlayer;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.config.PixelmonBankLocaleConfig;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Optional;

@Command(
        value = "deleteall",
        aliases = {
                "delall"
        }
)
@Permissible(PermNodeReference.DELETE_NODE)
@Child
public class DeleteAllCmd {

    @CommandProcessor
    public void run(@Sender ICommandSender sender, @Completable(PlayerTabCompleter.class) @Argument EntityPlayerMP target) {
        EnvyPlayer<EntityPlayerMP> targetPlayer = PixelmonBank.instance.getPlayerManager().getPlayer(target);
        if (!Optional.ofNullable(targetPlayer).isPresent()) {
            return;
        }
        if (SyncHandler.getInstance().delAll(targetPlayer.getUuid().toString())) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.successDeleteMsg));
        }

    }
}
