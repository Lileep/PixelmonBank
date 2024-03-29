package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.gui.PixelmonBankGui;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Map;

@Command(
        value = "see"
)
@Permissible(PermNodeReference.SEE_NODE)
public class SeeCmd {

    @CommandProcessor
    public void run(@Sender ServerPlayerEntity sender, String[] args) {
        //See

        ForgeEnvyPlayer player = PixelmonBank.getInstance().getPlayerManager().getPlayer(sender);
        //See logic
        int pageNum = 1;
        if (args.length >= 1) {
            try {
                pageNum = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getPageInvalid()), sender.getGameProfile().getId());
                return;
            }
        }

        Map<Integer, Pokemon> pokemonMap = SyncHandler
                .getInstance()
                .getAllPageable(player.getUuid().toString(), pageNum, 45);
//        List<Pokemon> pokemonList = SyncHandler
//                .getInstance()
//                .getAllPageable(player.getUuid().toString(), pageNum, 45);

        int count = SyncHandler.getInstance().getTotal(player.getUuid().toString());
        PixelmonBankGui.open(player, pokemonMap, pageNum, count);
    }
}
