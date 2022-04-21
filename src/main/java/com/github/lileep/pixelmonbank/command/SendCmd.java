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
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;

import java.util.Optional;

@Command(
        value = "send"
)
@Permissible(PermNodeReference.SEND_NODE)
@Child
public class SendCmd {

    private String getUsage(){
        return "/pixelmonbank send <slot>";
    }

    @CommandProcessor
    public void run(@Sender EntityPlayerMP sender, String[] args) {

        if (args.length < 1) {
            CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, this.getUsage());
            return;
        }

        //Send
        try {

            //Slot must in 1-6
            final int slot = Integer.parseInt(args[0].replaceAll("[^0-9]", ""));
            if (slot < 1 || slot > 6) {
                CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "Slot number must be between 1 and 6.");
                return;
            }

            //Get player storage
            final PlayerPartyStorage sStorage;
            if (Optional.ofNullable(Pixelmon.storageManager.getParty(sender)).isPresent()) {
                sStorage = Pixelmon.storageManager.getParty(sender);
            } else {
                return;
            }

            //Get pokemon
            final Pokemon pokemon;
            if (Optional.ofNullable(sStorage.get(slot - 1)).isPresent()) {
                pokemon = sStorage.get(slot - 1);
            } else {
                //Nothing in the slot
                CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "Nothing is in that slot.");
                return;
            }

            //Judge admin bypass and whether the last pokemon in team is an egg
            final boolean bypass = Lists.newArrayList(args).contains("-f") && sender.canUseCommand(4, PermNodeReference.BYPASS_NODE);

            if (sStorage.getTeam().size() == 1 && !pokemon.isEgg() && !bypass) {
                CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "You must have more than one none egg Pokemon in your party to do this.");
                return;
            }

            //retrieve all pixelmons
            sStorage.retrieveAll();

            //Send logic
            EnvyPlayer<EntityPlayerMP> player = PixelmonBank.instance.getPlayerManager().getPlayer(sender);
            if (SyncHandler.getInstance().sendOne(player.getUuid().toString(), player.getName(), pokemon)){
                //Delete player's pixelmon
                sStorage.set(slot - 1, null);
                CommandChatHandler.sendFormattedChat(sender, TextFormatting.GREEN, "Successfully send your " + pokemon.getDisplayName() + " to Pixelmon Bank!");
            }

        } catch (NumberFormatException e) {
            CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "Invalid slot number given.");
        }
    }
}
