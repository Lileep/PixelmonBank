package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextFormatting;

@Command(
        value = "pixelmonbank",
        description = "/pixelmonbank <operation> [<slot>]",
        aliases = {
                "pixelbank",
                "pbank",
                "pbk"
        }
)
@SubCommands({
        SendCmd.class,
        GetCmd.class,
        SeeCmd.class,
        GetAllCmd.class
})
public class PixelmonBankCmd {

    private String getUsage() {
        return "/pixelmonbank <" + "operation" +
                ">";
    }
    @CommandProcessor
    public void run(@Sender ICommandSender sender, String[] args) {
        CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, this.getUsage());
    }
}
