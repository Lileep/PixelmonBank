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
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
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


        //Check part
        //Judge admin bypass
        final boolean bypass = Lists.newArrayList(args).contains("-f") && sender.canUseCommand(4, PermNodeReference.BYPASS_NODE);

        //Check max count
        int maxCount = PixelmonBankConfig.MAX_COUNT;
        if (maxCount <= SyncHandler.getInstance().getTotal(sender.getGameProfile().getId().toString())) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.reachMax, maxCount));
            return;
        }

        //Check untradeable
        if (!PixelmonBankConfig.ALLOW_UNTRADEABLE && pokemon.hasSpecFlag("untradeable") && !bypass) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noUntradeable));
            return;
        }

        //Check whether the last pokemon in team is an egg
        if (sStorage.getTeam().size() == 1 && !pokemon.isEgg() && !bypass) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.partyLastOne));
            return;
        }

        //Check other things
        if (!validatePixelmon(pokemon, sender)) {
            return;
        }

        //retrieve all pixelmons
        sStorage.retrieveAll();

        //Send logic
        EnvyPlayer<EntityPlayerMP> player = PixelmonBank.instance.getPlayerManager().getPlayer(sender);
        String uuid = player.getUuid().toString();
        if (SyncHandler.getInstance().sendOne(uuid, pokemon) &&
                SyncHandler.getInstance().updateTotal(1, uuid)) {
            //Delete player's pixelmon
            sStorage.set(slot - 1, null);

            //Restrict Amount sync
            if (isRestrict(pokemon)) {
                if (!SyncHandler.getInstance().updateRestrictCount(1, uuid)) {
                    return;
                }
            }

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
        if (!PixelmonBankConfig.ALLOW_LEGENDARY && pokemon.getSpecies().isLegendary()) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noLegendary));
            return false;
        } else if (!PixelmonBankConfig.ALLOW_ULTRABEAST && pokemon.getSpecies().isUltraBeast()) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noUltrabeast));
            return false;
        } else if (PixelmonBankConfig.BLACK_LIST.length > 0) {
            List<String> blackList = Arrays.asList(PixelmonBankConfig.BLACK_LIST);
            if (blackList.contains(pokemon.getLocalizedName().toLowerCase()) || blackList.contains(pokemon.getSpecies().getPokemonName().toLowerCase())) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noBlackList, pokemon.getLocalizedName()));
                return false;
            }
        } else if (isRestrict(pokemon)) {
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

//            sender.sendMessage(MsgHandler.prefixedColorMsg("has move? "+pokemon.getMoveset().hasAttack(PixelmonBankConfig.BLACK_LIST_MOVE)));
        //Check moves
//            if (PixelmonBankConfig.BLACK_LIST_MOVE.length > 0) {
//                if (pokemon.getMoveset().hasAttack(PixelmonBankConfig.BLACK_LIST_MOVE)) {
//                    sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.noBlackList, pokemon.getLocalizedName()));
//                    return;
//                }
//            }

        return true;
    }

    private boolean isRestrict(Pokemon pokemon) {
        if (PixelmonBankConfig.RESTRICT_LIST.length > 0) {
            List<String> restrictList = Arrays.asList(PixelmonBankConfig.RESTRICT_LIST);
            return restrictList.contains(pokemon.getLocalizedName().toLowerCase()) || restrictList.contains(pokemon.getSpecies().getPokemonName().toLowerCase());
        }
        return false;
    }
}
