package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.config.PixelmonBankConfig;
import com.github.lileep.pixelmonbank.config.PixelmonBankLocaleConfig;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.github.lileep.pixelmonbank.handler.SyncHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import com.github.lileep.pixelmonbank.util.PokemonOptUtil;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
    public void run(@Sender EntityPlayerMP sender, @Argument String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(this.getUsage()));
            return;
        }

        //Send

        //Slot must in 1-6
        final int slot;
        try {
            slot = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.slotNumInvalid));
            return;
        }

        if (slot < 1 || slot > 6) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.slotNumLimited));
            return;
        }

        //Get player storage
        final PlayerPartyStorage sStorage = Pixelmon.storageManager.getParty(sender);
        if (!Optional.ofNullable(sStorage).isPresent()) {
            return;
        }

        //Get pokemon
        final Pokemon pokemon = sStorage.get(slot - 1);
        if (!Optional.ofNullable(pokemon).isPresent()) {
            //Nothing in the slot
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.nothing));
            return;
        }


        //Check part
        //Judge admin bypass
        boolean bypass = Lists.newArrayList(args).contains("-f") && sender.canUseCommand(4, PermNodeReference.BYPASS_NODE);

        if (!bypass) {
            //Check max count
            int maxCount = PixelmonBankConfig.MAX_COUNT;
            if (maxCount > 0 && maxCount <= SyncHandler.getInstance().getTotal(sender.getGameProfile().getId().toString())) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.reachMax, maxCount));
                return;
            }
            //Check untradeable
            if (!PixelmonBankConfig.ALLOW_UNTRADEABLE && pokemon.hasSpecFlag("untradeable")) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noUntradeable));
                return;
            }

            //Check whether the last pokemon in team is an egg
            if (sStorage.getTeam().size() == 1 && !pokemon.isEgg()) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.partyLastOne));
                return;
            }

            //Check other things
            if (!validatePixelmon(pokemon, sender)) {
                return;
            }
        }

        //retrieve all pixelmons
        sStorage.retrieveAll();

        //Send logic
        ForgeEnvyPlayer player = PixelmonBank.instance.getPlayerManager().getPlayer(sender);
        String uuid = player.getUuid().toString();
        if (SyncHandler.getInstance().sendOne(uuid, pokemon)) {
            //Delete player's pixelmon
            sStorage.set(slot - 1, null);
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.successSendMsg, pokemon.getDisplayName()));
        }
    }

    private boolean validatePixelmon(Pokemon pokemon, EntityPlayerMP sender) {

        //Check egg
        if (pokemon.isEgg() && !PixelmonBankConfig.ALLOW_EGG) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noEgg));
            return false;
        }

        //Check black lists
        if (!PixelmonBankConfig.ALLOW_LEGENDARY && pokemon.isLegendary()) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noLegendary));
            return false;
        } else if (!PixelmonBankConfig.ALLOW_ULTRABEAST && pokemon.getSpecies().isUltraBeast()) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noUltrabeast));
            return false;
        } else if (PokemonOptUtil.isBlackList(pokemon)) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noBlackList, pokemon.getLocalizedName()));
            return false;
        } else if (PokemonOptUtil.isRestrict(pokemon)) {
            int restrictCount = SyncHandler.getInstance().getRestrictCount(sender.getGameProfile().getId().toString());
            if (restrictCount >= PixelmonBankConfig.RESTRICT_COUNT) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noRestrictList, pokemon.getLocalizedName()));
                return false;
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
                return false;
            }
        }

        //Check held item
        if (!PixelmonBankConfig.ALLOW_ITEM && !pokemon.getHeldItem().isEmpty()) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noHeldItem));
            return false;
        }
        if (PixelmonBankConfig.BLACK_LIST_ITEM.length > 0) {
            List<String> blackList = Arrays.asList(PixelmonBankConfig.BLACK_LIST_ITEM);
            if (blackList.contains(Objects.requireNonNull(pokemon.getHeldItem().getItem().getRegistryName()).toString())) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noBlackListItem, pokemon.getHeldItem().getDisplayName()));
                return false;
            }
        }

        return true;
    }
}
