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
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Command(
        value = "send"
)
@Permissible(PermNodeReference.SEND_NODE)
@Child
public class SendCmd {

    private String getUsage() {
        return "&c/pixelmonbank send <" + PixelmonBankLocaleConfig.argSlot + ">";
    }

    @CommandProcessor
    public void run(@Sender EntityPlayerMP sender, String[] args) {

        if (args.length < 1) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(this.getUsage()));
            return;
        }

        //Send
        try {

            //Slot must in 1-6
            final int slot = Integer.parseInt(args[0].replaceAll("[^0-9]", ""));
            if (slot < 1 || slot > 6) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.slotNumLimited));
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
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.nothing));
                return;
            }
            assert pokemon != null;

            //Judge admin bypass and whether the last pokemon in team is an egg
            final boolean bypass = Lists.newArrayList(args).contains("-f") && sender.canUseCommand(4, PermNodeReference.BYPASS_NODE);

            if (sStorage.getTeam().size() == 1 && !pokemon.isEgg() && !bypass) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.partyLastOne));
                return;
            }

            //Check egg
            if (pokemon.isEgg() && !PixelmonBankConfig.ALLOW_EGG) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noEgg));
                return;
            }

            //Check black lists
            if (!PixelmonBankConfig.ALLOW_LEGENDARY && EnumSpecies.legendaries.contains(pokemon.getSpecies())) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noLegendary));
                return;
            } else if (!PixelmonBankConfig.ALLOW_ULTRABEAST && EnumSpecies.ultrabeasts.contains(pokemon.getSpecies())) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noUltrabeast));
                return;
            } else if (PixelmonBankConfig.BLACK_LIST.length > 0) {
                List<String> blackList = Arrays.asList(PixelmonBankConfig.BLACK_LIST);
                if (blackList.contains(pokemon.getLocalizedName().toLowerCase()) || blackList.contains(pokemon.getSpecies().getPokemonName().toLowerCase())) {
                    sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noBlackList, pokemon.getLocalizedName()));
                    return;
                }
            }

            //Check ivs
            if (PixelmonBankConfig.MAX_IVS < 6 && PixelmonBankConfig.MAX_IVS >= 0) {
                int maxIVCount = 0;
                //Count hyper trained
                if (PixelmonBankConfig.COUNT_HYPER_TRAINED) {
                    for (StatsType type : StatsType.getStatValues()) {
                        if (pokemon.getIVs().isHyperTrained(type) || pokemon.getIVs().getStat(type) >= 31) {
                            maxIVCount++;
                        }
                    }
                } else {
                    for (int iv : pokemon.getIVs().getArray()) {
                        if (iv >= 31) {
                            maxIVCount++;
                        }
                    }
                }
                if (maxIVCount > PixelmonBankConfig.MAX_IVS) {
                    sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noMaxIVs, PixelmonBankConfig.MAX_IVS));
                    return;
                }
            }

            //retrieve all pixelmons
            sStorage.retrieveAll();

            //Send logic
            EnvyPlayer<EntityPlayerMP> player = PixelmonBank.instance.getPlayerManager().getPlayer(sender);
            if (SyncHandler.getInstance().sendOne(player.getUuid().toString(), pokemon)) {
                //Delete player's pixelmon
                sStorage.set(slot - 1, null);
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.successSendMsg, pokemon.getDisplayName()));
            }

        } catch (NumberFormatException e) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.slotNumInvalid));
        }
    }
}
