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
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

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
        return "&c/pixelmonbank send <" + PixelmonBank.getInstance().getLocale().getArgSlot() + ">";
    }

    @CommandProcessor
    public void run(@Sender ServerPlayerEntity sender, @Argument String[] args) {

        if (args.length < 1) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(this.getUsage()), sender.getGameProfile().getId());
            return;
        }

        //Send

        //Slot must in 1-6
        final int slot;
        try {
            slot = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getSlotNumInvalid()), sender.getGameProfile().getId());
            return;
        }

        if (slot < 1 || slot > 6) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getSlotNumLimited()), sender.getGameProfile().getId());
            return;
        }

        //Get player storage
        final PlayerPartyStorage sStorage;
        if (Optional.ofNullable(StorageProxy.getParty(sender)).isPresent()) {
            sStorage = StorageProxy.getParty(sender);
        } else {
            return;
        }

        //Get pokemon
        final Pokemon pokemon;
        if (Optional.ofNullable(sStorage.get(slot - 1)).isPresent()) {
            pokemon = sStorage.get(slot - 1);
        } else {
            //Nothing in the slot
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getNothing()), sender.getGameProfile().getId());
            return;
        }
        assert pokemon != null;


        //Check part
        //Judge admin bypass
        final boolean bypass = Lists.newArrayList(args).contains("-f") && sender.hasPermissions(4);
        //Get global config
        PixelmonBankConfig pbkConfig = PixelmonBank.getInstance().getConfig();

        //Check max count
        int maxCount = pbkConfig.getMaxCount();
        if (maxCount > 0 && maxCount <= SyncHandler.getInstance().getTotal(sender.getGameProfile().getId().toString())) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getReachMax(), maxCount), sender.getGameProfile().getId());
            return;
        }

        //Check untradeable
        if (!pbkConfig.isAllowUntradeable() && pokemon.hasFlag("untradeable") && !bypass) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getNoUntradeable()), sender.getGameProfile().getId());
            return;
        }

        //Check whether the last pokemon in team is an egg
        if (sStorage.getTeam().size() == 1 && !pokemon.isEgg() && !bypass) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getPartyLastOne()), sender.getGameProfile().getId());
            return;
        }

        //Check other things
        if (!validatePixelmon(pbkConfig, pokemon, sender)) {
            return;
        }

        //retrieve all pixelmons
        sStorage.retrieveAll("Command");

        //Send logic
        EnvyPlayer<ServerPlayerEntity> player = PixelmonBank.getInstance().getPlayerManager().getPlayer(sender);
        String uuid = player.getUuid().toString();
        if (SyncHandler.getInstance().sendOne(uuid, pokemon) &&
                SyncHandler.getInstance().updateTotal(1, uuid)) {
            //Delete player's pixelmon
            sStorage.set(slot - 1, null);

            //Restrict Amount sync
            if (isRestrict(pbkConfig, pokemon)) {
                if (!SyncHandler.getInstance().updateRestrictCount(1, uuid)) {
                    return;
                }
            }

            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getSuccessSendMsg(), pokemon.getFormattedDisplayName()), sender.getGameProfile().getId());
        }
    }

    private boolean validatePixelmon(PixelmonBankConfig pbkConfig, Pokemon pokemon, ServerPlayerEntity sender) {
        //Check egg
        if (pokemon.isEgg() && !pbkConfig.isAllowEgg()) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getNoEgg()), sender.getGameProfile().getId());
            return false;
        }

        //Check black lists
        if (!pbkConfig.isAllowLegendary() && pokemon.isLegendary()) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getNoLegendary()), sender.getGameProfile().getId());
            return false;
        } else if (!pbkConfig.isAllowUltrabeast() && pokemon.isUltraBeast()) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getNoUltrabeast()), sender.getGameProfile().getId());
            return false;
        } else if (pbkConfig.getBlackList().length > 0) {
            List<String> blackList = Arrays.asList(pbkConfig.getBlackList());
            if (blackList.contains(pokemon.getLocalizedName().toLowerCase()) || blackList.contains(pokemon.getSpecies().getName().toLowerCase())) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getNoBlackList(), pokemon.getLocalizedName()), sender.getGameProfile().getId());
                return false;
            }
        } else if (isRestrict(pbkConfig, pokemon)) {
            int restrictCount = SyncHandler.getInstance().getRestrictCount(sender.getGameProfile().getId().toString());
            if (restrictCount >= pbkConfig.getRestrictCount()) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getNoRestrictList(), pokemon.getLocalizedName()), sender.getGameProfile().getId());
                return false;
            }
        }

        //Check ivs
        if (pbkConfig.getMaxIvs() < 6 && pbkConfig.getMaxIvs() >= 0) {
            int maxIVCount = 0;
            //Count hyper trained
            if (pbkConfig.isCountHyperTrained()) {
                for (BattleStatsType type : BattleStatsType.getEVIVStatValues()) {
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
            if (maxIVCount > pbkConfig.getMaxIvs()) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getNoMaxIVs(), pbkConfig.getMaxIvs()), sender.getGameProfile().getId());
                return false;
            }
        }

        //Check held item
        if (!pbkConfig.isAllowItem() && !pokemon.getHeldItem().isEmpty()) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getNoHeldItem()), sender.getGameProfile().getId());
            return false;
        }
        if (pbkConfig.getBlackListItem().length > 0) {
            List<String> blackList = Arrays.asList(pbkConfig.getBlackListItem());
            if (blackList.contains(Objects.requireNonNull(pokemon.getHeldItem().getItem().getRegistryName()).toString())) {
                sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().getNoBlackListItem(), pokemon.getHeldItem().getDisplayName()), sender.getGameProfile().getId());
                return false;
            }
        }

//            sender.sendMessage(MsgHandler.prefixedColorMsg("has move? "+pokemon.getMoveset().hasAttack(pbkConfig.BLACK_LIST_MOVE)));
        //Check moves
//            if (pbkConfig.BLACK_LIST_MOVE.length > 0) {
//                if (pokemon.getMoveset().hasAttack(pbkConfig.BLACK_LIST_MOVE)) {
//                    sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBank.getInstance().getLocale().noBlackList, pokemon.getLocalizedName()));
//                    return;
//                }
//            }

        return true;
    }

    private boolean isRestrict(PixelmonBankConfig pbkConfig, Pokemon pokemon) {
        if (pbkConfig.getRestrictList().length > 0) {
            List<String> restrictList = Arrays.asList(pbkConfig.getRestrictList());
            return restrictList.contains(pokemon.getLocalizedName().toLowerCase()) || restrictList.contains(pokemon.getSpecies().getName().toLowerCase());
        }
        return false;
    }
}
