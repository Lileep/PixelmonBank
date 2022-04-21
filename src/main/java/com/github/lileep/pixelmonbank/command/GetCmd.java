package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.player.EnvyPlayer;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;

import java.util.Optional;

@Command(
        value = "get"
)
@Permissible(PermNodeReference.GET_NODE)
@Child
public class GetCmd {

    private String getUsage() {
        return "/pixelmonbank get <pokemon_uuid>";
    }

    @CommandProcessor
    public void run(@Sender EntityPlayerMP sender, String[] args) {

        if (args.length < 1) {
            CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, this.getUsage());
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
        Pokemon pokemon = SyncHandler.getInstance().getOne(player.getUuid().toString(), args[0]);

        //whether found this pixelmon
        if (!Optional.ofNullable(pokemon).isPresent()) {
            CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "There's no such pixelmon in your Pixelmon Bank.");
            return;
        }

        //Judge whether can trigger receive event
//        if (Optional.ofNullable(server.getPlayerList().getPlayerByUsername(sender.getName())).isPresent()) {
//            if (Pixelmon.EVENT_BUS.post(new PixelmonReceivedEvent(sender, ReceiveType.Command, pokemon))) {
//                return;
//            }
//        }

        //Remove success
        if (SyncHandler.getInstance().delOne(player.getUuid().toString(), args[0])) {
            sStorage.add(pokemon);
            CommandChatHandler.sendFormattedChat(sender, TextFormatting.GREEN, "Successfully get your " + pokemon.getDisplayName() + " from Pixelmon Bank!");
        }

        //Judge online and send success msg
//        if (Optional.ofNullable(server.getPlayerList().getPlayerByUsername(sender.getName())).isPresent()) {
//            CommandChatHandler.sendFormattedChat(sender, TextFormatting.GREEN, "You successfully send your " + pokemon.getDisplayName() + " to Pixelmon Bank!");
//        }

    }
}
