package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import com.github.lileep.pixelmonbank.util.PokemonOptUtil;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.command.PixelmonCommandUtils;
import com.pixelmonmod.pixelmon.api.events.PokemonReceivedEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;

@Command(
        value = "getall"
)
@Permissible(PermNodeReference.GET_NODE)
public class GetAllCmd {

    @CommandProcessor
    public void run(@Sender ServerPlayer sender) {

        //Test party
        PlayerPartyStorage sStorage = PixelmonCommandUtils.getPlayerStorage(sender);
        if (Optional.ofNullable(sStorage).isEmpty()) {
            return;
        }

        //Get logic
        ForgeEnvyPlayer player = PixelmonBank.getInstance().getPlayerManager().getPlayer(sender);
        String uuid = player.getUuid().toString();
        List<Pokemon> pokemonList = SyncHandler.getInstance().getAll(uuid);

        //whether player have pixelmons in bank
        if (pokemonList.size() < 1) {
            sender.sendSystemMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getHaveNone()));
            return;
        }

        //Remove success
        if (SyncHandler.getInstance().delAll(uuid)) {
            PokemonOptUtil.operatePokemons(pokemonList);
            for (Pokemon pokemon : pokemonList) {
                if (!Pixelmon.EVENT_BUS.post(new PokemonReceivedEvent(sender, pokemon, "GiftCommand"))) {
                    sStorage.add(pokemon);
                }
                //TODO: Maybe we need to judge situations that are cancelled after the event
            }
//            pokemonList.forEach(sStorage::add);
            sender.sendSystemMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getSuccessGetAllMsg()));
        }
    }
}
