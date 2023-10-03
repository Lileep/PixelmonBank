package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.player.EnvyPlayer;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.config.PixelmonBankLocaleConfig;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import com.github.lileep.pixelmonbank.util.PokemonOptUtil;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;
import java.util.Optional;

@Command(
        value = "getall"
)
@Permissible(PermNodeReference.GET_NODE)
@Child
public class GetAllCmd {

    @CommandProcessor
    public void run(@Sender EntityPlayerMP sender) {

        //Test party
        PlayerPartyStorage sStorage;
        if (Optional.ofNullable(Pixelmon.storageManager.getParty(sender)).isPresent()) {
            sStorage = Pixelmon.storageManager.getParty(sender);
        } else {
            return;
        }

        //Get logic
        EnvyPlayer<EntityPlayerMP> player = PixelmonBank.instance.getPlayerManager().getPlayer(sender);
        String uuid = player.getUuid().toString();
        List<Pokemon> pokemonList = SyncHandler.getInstance().getAll(player.getUuid().toString());

        //whether player have pixelmons in bank
        if (pokemonList.size() < 1) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.haveNone));
            return;
        }

        //Remove success
        if (SyncHandler.getInstance().delAll(uuid)) {
            PokemonOptUtil.operatePokemons(pokemonList);
            pokemonList.forEach(sStorage::add);
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.successGetAllMsg));
        }
    }
}
