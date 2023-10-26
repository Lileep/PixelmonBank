package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;

@Command(
        value = "reload"
)
@Permissible(PermNodeReference.RELOAD_NODE)
public class ReloadCmd {

    @CommandProcessor
    public void run(@Sender ICommandSource sender) {
        sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getConfigReloading()), Util.NIL_UUID);
        PixelmonBank.getInstance().loadConfig();
        sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getConfigLoadSuccess()), Util.NIL_UUID);
    }

}

