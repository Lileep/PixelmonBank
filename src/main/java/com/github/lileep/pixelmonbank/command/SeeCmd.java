package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.player.EnvyPlayer;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.gui.PixelmonBankGui;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

@Command(
        value = "see"
)
@Permissible(PermNodeReference.SEE_NODE)
@Child
public class SeeCmd {

    @CommandProcessor
    public void run(@Sender EntityPlayerMP sender, String[] args) {
        //See

        EnvyPlayer<EntityPlayerMP> player = PixelmonBank.instance.getPlayerManager().getPlayer(sender);
        //See logic
        List<Pokemon> pokemonList = SyncHandler.getInstance().getAll(player.getUuid().toString());
        PixelmonBankGui.open(player, pokemonList);
    }
}
