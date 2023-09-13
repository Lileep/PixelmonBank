package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.config.PixelmonBankConfig;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.server.level.ServerPlayer;

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
    public void run(@Sender ServerPlayer sender) {

        //Test party
        PlayerPartyStorage sStorage;
        if (Optional.ofNullable(StorageProxy.getParty(sender)).isPresent()) {
            sStorage = StorageProxy.getParty(sender);
        } else {
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
        if (SyncHandler.getInstance().updateTotal(-pokemonList.size(), uuid) &&
                checkAndUpdateRestricts(pokemonList, uuid) &&
                SyncHandler.getInstance().delAll(uuid)) {
            operatePokemons(pokemonList);
            pokemonList.forEach(sStorage::add);
            sender.sendSystemMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getSuccessGetAllMsg()));
        }
    }

    private void operatePokemons(List<Pokemon> pokemonList) {
        PixelmonBankConfig pbkConfig = PixelmonBank.getInstance().getConfig();
        if (pbkConfig.isSterilizeWhenWithdraw()) {
            pokemonList.forEach(p -> p.addFlag("unbreedable"));
        }
        if (pbkConfig.isUntradifyWhenWithdraw()) {
            pokemonList.forEach(p -> p.addFlag("untradeable"));
        }
    }

    private boolean checkAndUpdateRestricts(List<Pokemon> pokemonList, String playerUUID) {
        PixelmonBankConfig pbkConfig = PixelmonBank.getInstance().getConfig();
        if (pbkConfig.getRestrictList().length > 0) {
            List<String> restrictList = Arrays.asList(pbkConfig.getRestrictList());
            int restrictAmount = 0;
            for (Pokemon pokemon : pokemonList) {
                if (restrictList.contains(pokemon.getTranslatedName().toString().toLowerCase()) || restrictList.contains(pokemon.getSpecies().getName().toLowerCase())) {
                    restrictAmount++;
                }
            }
            if (restrictAmount > 0) {
                return SyncHandler.getInstance().updateRestrictCount(-restrictAmount, playerUUID);
            }
        }
        return true;
    }
}
