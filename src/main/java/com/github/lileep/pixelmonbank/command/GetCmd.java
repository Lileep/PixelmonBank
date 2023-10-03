package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.player.EnvyPlayer;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.config.PixelmonBankConfig;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Command(
        value = "get"
)
@Permissible(PermNodeReference.GET_NODE)
@Child
public class GetCmd {

    private String getUsage() {
        return "&c/pixelmonbank get <pokemon_id>";
    }

    @CommandProcessor
    public void run(@Sender ServerPlayerEntity sender, @Argument String[] args) {

        if (args.length < 1) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(this.getUsage()), sender.getGameProfile().getId());
            return;
        }

        //Get

        //Test party
        PlayerPartyStorage sStorage;
        if (Optional.ofNullable(StorageProxy.getParty(sender)).isPresent()) {
            sStorage = StorageProxy.getParty(sender);
        } else {
            return;
        }

        //Get logic
        EnvyPlayer<ServerPlayerEntity> player = PixelmonBank.getInstance().getPlayerManager().getPlayer(sender);
        String uuid = player.getUuid().toString();
        int id = Integer.parseInt(args[0]);
        Pokemon pokemon = SyncHandler.getInstance().getOne(id, uuid);

        //whether found this pixelmon
        if (Optional.ofNullable(pokemon).isEmpty()) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getFindNone()), sender.getGameProfile().getId());
            return;
        }

        //Judge whether can trigger receive event
//        if (Optional.ofNullable(server.getPlayerList().getPlayerByUsername(sender.getName())).isPresent()) {
//            if (Pixelmon.EVENT_BUS.post(new PixelmonReceivedEvent(sender, ReceiveType.Command, pokemon))) {
//                return;
//            }
//        }

        //Remove success
        if (SyncHandler.getInstance().updateTotal(-1, uuid) &&
                checkAndUpdateRestrict(pokemon, uuid) &&
                SyncHandler.getInstance().delOne(id, uuid)) {

            operatePokemon(pokemon);
            sStorage.add(pokemon);
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getSuccessGetMsg(), pokemon.getFormattedDisplayName()), sender.getGameProfile().getId());
        }

        //Judge online and send success msg
//        if (Optional.ofNullable(server.getPlayerList().getPlayerByUsername(sender.getName())).isPresent()) {
//        }

    }

    private void operatePokemon(Pokemon pokemon) {
        PixelmonBankConfig pbkConfig = PixelmonBank.getInstance().getConfig();
        if (pbkConfig.isSterilizeWhenWithdraw()) {
            pokemon.addFlag("unbreedable");
        }
        if (pbkConfig.isUntradifyWhenWithdraw()) {
            pokemon.addFlag("untradeable");
        }
        if (pbkConfig.isResetFriendshipWhenWithdraw()) {
            pokemon.setFriendship(pokemon.getForm().getSpawn().getBaseFriendship());
        }
    }

    private boolean checkAndUpdateRestrict(Pokemon pokemon, String playerUUID) {
        PixelmonBankConfig pbkConfig = PixelmonBank.getInstance().getConfig();
        if (pbkConfig.getRestrictList().length > 0) {
            List<String> restrictList = Arrays.asList(pbkConfig.getRestrictList());
            if (restrictList.contains(pokemon.getLocalizedName().toLowerCase()) || restrictList.contains(pokemon.getSpecies().getName().toLowerCase())) {
                return SyncHandler.getInstance().updateRestrictCount(-1, playerUUID);
            }
        }
        return true;
    }
}
