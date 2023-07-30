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
        value = "get"
)
@Permissible(PermNodeReference.GET_NODE)
@Child
public class GetCmd {

    private String getUsage() {
        return "&c/pixelmonbank get <pokemon_uuid>";
    }

    @CommandProcessor
    public void run(@Sender EntityPlayerMP sender, @Argument String[] args) {

        if (args.length < 1) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(this.getUsage()));
            return;
        }

        //Get

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
        int id = Integer.parseInt(args[0]);
        Pokemon pokemon = SyncHandler.getInstance().getOne(id, uuid);

        //whether found this pixelmon
        if (!Optional.ofNullable(pokemon).isPresent()) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.findNone));
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
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.successGetMsg, pokemon.getDisplayName()));
        }

        //Judge online and send success msg
//        if (Optional.ofNullable(server.getPlayerList().getPlayerByUsername(sender.getName())).isPresent()) {
//        }

    }

    private void operatePokemon(Pokemon pokemon) {
        if (PixelmonBankConfig.STERILIZE_WHEN_WITHDRAW) {
            pokemon.addSpecFlag("unbreedable");
        }
        if (PixelmonBankConfig.UNTRADIFY_WHEN_WITHDRAW) {
            pokemon.addSpecFlag("untradeable");
        }
    }

    private boolean checkAndUpdateRestrict(Pokemon pokemon, String playerUUID) {
        if (PixelmonBankConfig.RESTRICT_LIST.length > 0) {
            List<String> restrictList = Arrays.asList(PixelmonBankConfig.RESTRICT_LIST);
            if (restrictList.contains(pokemon.getLocalizedName().toLowerCase()) || restrictList.contains(pokemon.getSpecies().getPokemonName().toLowerCase())) {
                return SyncHandler.getInstance().updateRestrictCount(-1, playerUUID);
            }
        }
        return true;
    }
}
