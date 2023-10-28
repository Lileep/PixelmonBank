package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import net.minecraft.commands.CommandSource;

@Command(
        value = {
                "pixelmonbank",
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
        return "&c/pixelmonbank <" + PixelmonBank.getInstance().getLocale().getArgOperation() + "> [<" + PixelmonBank.getInstance().getLocale().getArgParam() + ">]";
    }

    @CommandProcessor
    public void run(@Sender CommandSource sender) {
        sender.sendSystemMessage(MsgHandler.prefixedColorMsg(this.getUsage()));
    }
}
