package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.player.EnvyPlayer;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import com.github.lileep.pixelmonbank.util.PokemonOptUtil;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.entity.player.ServerPlayerEntity;

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
        if (SyncHandler.getInstance().delOne(id, uuid)) {
            PokemonOptUtil.operatePokemon(pokemon);
            sStorage.add(pokemon);
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getSuccessGetMsg(), pokemon.getFormattedDisplayName()), sender.getGameProfile().getId());
        }

        //Judge online and send success msg
//        if (Optional.ofNullable(server.getPlayerList().getPlayerByUsername(sender.getName())).isPresent()) {
//        }

    }
}
