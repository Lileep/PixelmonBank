package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import net.minecraft.commands.CommandSource;

@Command(
        value = "reload"
)
@Permissible(PermNodeReference.RELOAD_NODE)
public class ReloadCmd {

    @CommandProcessor
    public void run(@Sender CommandSource sender) {
        sender.sendSystemMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getConfigReloading()));
        PixelmonBank.getInstance().loadConfig();
        sender.sendSystemMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getConfigLoadSuccess()));
    }

}

