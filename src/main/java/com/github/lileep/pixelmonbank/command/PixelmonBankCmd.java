package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.github.lileep.pixelmonbank.config.PixelmonBankLocaleConfig;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import net.minecraft.command.ICommandSender;

@Command(
        value = "pixelmonbank",
        aliases = {
                "pokemonbank",
                "pixelbank",
                "pokebank",
                "pbank",
                "pbk"
        }
)
@SubCommands({
        SendCmd.class,
        GetCmd.class,
        SeeCmd.class,
        GetAllCmd.class,
        ReloadCmd.class,
        DeleteAllCmd.class
})
public class PixelmonBankCmd {

    private String getUsage() {
        return "&c/pixelmonbank <" + PixelmonBankLocaleConfig.argOperation + "> [<" + PixelmonBankLocaleConfig.argParam + ">]";
    }

    @CommandProcessor
    public void run(@Sender ICommandSender sender, String[] args) {
        sender.sendMessage(MsgHandler.prefixedColorMsg(this.getUsage()));
    }
}
