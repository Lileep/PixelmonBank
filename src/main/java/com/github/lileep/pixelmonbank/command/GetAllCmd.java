package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.player.EnvyPlayer;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.config.PixelmonBankConfig;
import com.github.lileep.pixelmonbank.config.PixelmonBankLocaleConfig;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Arrays;
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
        if (SyncHandler.getInstance().updateTotal(-pokemonList.size(), uuid) &&
                checkAndUpdateRestricts(pokemonList, uuid) &&
                SyncHandler.getInstance().delAll(uuid)) {
            operatePokemons(pokemonList);
            pokemonList.forEach(sStorage::add);
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.successGetAllMsg));
        }
    }

    private void operatePokemons(List<Pokemon> pokemonList) {
        if (PixelmonBankConfig.STERILIZE_WHEN_WITHDRAW) {
            pokemonList.forEach(p->p.addSpecFlag("unbreedable"));
        }
        if (PixelmonBankConfig.UNTRADIFY_WHEN_WITHDRAW) {
            pokemonList.forEach(p->p.addSpecFlag("untradeable"));
        }
        if (PixelmonBankConfig.RESET_FRIENDSHIP_WHEN_WITHDRAW) {
            pokemonList.forEach(p->p.setFriendship(p.getBaseStats().getBaseFriendship()));
        }
    }

    private boolean checkAndUpdateRestricts(List<Pokemon> pokemonList, String playerUUID) {
        if (PixelmonBankConfig.RESTRICT_LIST.length > 0) {
            List<String> restrictList = Arrays.asList(PixelmonBankConfig.RESTRICT_LIST);
            int restrictAmount = 0;
            for (Pokemon pokemon : pokemonList) {
                if (restrictList.contains(pokemon.getLocalizedName().toLowerCase()) || restrictList.contains(pokemon.getSpecies().getPokemonName().toLowerCase())) {
                    restrictAmount ++;
                }
            }
            if (restrictAmount > 0) {
                return SyncHandler.getInstance().updateRestrictCount(-restrictAmount, playerUUID);
            }
        }
        return true;
    }
}
