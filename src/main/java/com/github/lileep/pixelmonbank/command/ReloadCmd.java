package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextFormatting;

@Command(
        value = "reload"
)
@Permissible(PermNodeReference.RELOAD_NODE)
@Child
public class ReloadCmd {

    @CommandProcessor
    public void run(@Sender ICommandSender sender, String[] args) {
        CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "Reloading...");
        PixelmonBank.instance.reloadConfig();
        CommandChatHandler.sendFormattedChat(sender, TextFormatting.GREEN, "Reload successful!");
    }

}

