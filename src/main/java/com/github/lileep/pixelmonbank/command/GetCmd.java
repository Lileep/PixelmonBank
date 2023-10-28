package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.Argument;
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

import java.util.Optional;

@Command(
        value = "get"
)
@Permissible(PermNodeReference.GET_NODE)
public class GetCmd {

    private String getUsage() {
        return "&c/pixelmonbank get <pokemon_id>";
    }

    @CommandProcessor
    public void run(@Sender ServerPlayer sender, @Argument int id) {
//
//        if (args.length < 1) {
//            sender.sendSystemMessage(MsgHandler.prefixedColorMsg(this.getUsage()));
//            return;
//        }

        //Get

        //Test party
        PlayerPartyStorage sStorage = PixelmonCommandUtils.getPlayerStorage(sender);
        if (Optional.ofNullable(sStorage).isEmpty()) {
            return;
        }

        //Get logic
        ForgeEnvyPlayer player = PixelmonBank.getInstance().getPlayerManager().getPlayer(sender);
        String uuid = player.getUuid().toString();
//        int id;
//        try {
//            id = Integer.parseInt(args[0]);
//        } catch (NumberFormatException e) {
//            sender.sendSystemMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getSlotNumInvalid()));
//            return;
//        }

        Pokemon pokemon = SyncHandler.getInstance().getOne(id, uuid);

        //whether found this pixelmon
        if (Optional.ofNullable(pokemon).isEmpty()) {
            sender.sendSystemMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getFindNone()));
            return;
        }

        //Remove success
        if (!Pixelmon.EVENT_BUS.post(new PokemonReceivedEvent(sender, pokemon, "PixelmonBankCommand"))) {
            if (SyncHandler.getInstance().delOne(id, uuid)) {
                PokemonOptUtil.operatePokemon(pokemon);
                sStorage.add(pokemon);
//            selfStorage.add(pokemon);
                sender.sendSystemMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getSuccessGetMsg(), pokemon.getFormattedDisplayName().getString()));
            }
        }

        //Judge online and send success msg
//        if (Optional.ofNullable(server.getPlayerList().getPlayerByUsername(sender.getName())).isPresent()) {
//        }

    }
}
