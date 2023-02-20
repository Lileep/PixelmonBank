package com.github.lileep.pixelmonbank.data.bean;

import com.github.lileep.pixelmonbank.config.PixelmonBankConfig;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.api.moveskills.MoveSkill;
import com.pixelmonmod.pixelmon.api.pokemon.ISpecType;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.api.pokemon.SpecFlag;
import com.pixelmonmod.pixelmon.battles.status.StatusPersist;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.config.RemapHandler;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Pokerus;
import com.pixelmonmod.pixelmon.enums.*;
import com.pixelmonmod.pixelmon.enums.forms.EnumMagikarp;
import com.pixelmonmod.pixelmon.enums.forms.EnumMissingNo;
import com.pixelmonmod.pixelmon.enums.forms.EnumSolgaleo;
import com.pixelmonmod.pixelmon.enums.forms.EnumSpecial;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PokemonBean extends Pokemon {

    public PokemonBean(UUID uuid) {
        super(uuid);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("ndex")) {
            this.dsSpecies.set(this, nbt.getInteger("ndex"));
        } else if (nbt.hasKey(NbtKeys.NAME)) {
            this.dsSpecies.set(this, EnumSpecies.getFromName(nbt.getString(NbtKeys.NAME)).orElse(EnumSpecies.MissingNo).getNationalPokedexInteger());
        }

        if (nbt.hasKey(NbtKeys.FORM)) {
            this.dsForm.set(this, nbt.getByte(NbtKeys.FORM));
        }

        if (nbt.hasKey(NbtKeys.GENDER)) {
            this.dsGender.set(this, nbt.getByte(NbtKeys.GENDER));
        }

        if (this.species == EnumSpecies.MissingNo) {
            EnumMissingNo.migrate(this);
        }

        int NBT_VERSION = nbt.getByte("NBT_VERSION");
        this.setUUID(nbt.getUniqueId(NbtKeys.UUID));
        this.setShiny(nbt.getBoolean(NbtKeys.IS_SHINY));

        //Compatible with previous versions
        byte special = nbt.getByte("Form");
        switch (special) {
            case 1:
                this.dsForm.set(this, EnumMagikarp.ROASTED.getForm());
                break;
            case 2:
                this.dsForm.set(this, EnumSpecial.Zombie.getForm());
                break;
            case 3:
                this.dsForm.set(this, EnumSpecial.Online.getForm());
                break;
            case 4:
                this.dsForm.set(this, EnumSpecial.Drowned.getForm());
                break;
            case 5:
                this.dsForm.set(this, EnumSpecial.Valentine.getForm());
                break;
            case 6:
                this.dsForm.set(this, EnumSpecial.Rainbow.getForm());
                break;
            case 7:
                this.dsForm.set(this, EnumSpecial.Alien.getForm());
                break;
            case 8:
                this.dsForm.set(this, EnumSolgaleo.Real.getForm());
                break;
            case 9:
                this.dsForm.set(this, EnumSpecial.Alter.getForm());
                break;
            case 10:
                this.dsForm.set(this, EnumSpecial.Pink.getForm());
                break;
            case 11:
                this.dsForm.set(this, EnumSpecial.Summer.getForm());
                break;
            case 12:
                this.dsForm.set(this, EnumSpecial.Crystal.getForm());
                break;
        }

        this.setCustomTexture(nbt.getString(PixelmonBankConfig.OVERRIDE_PALETTE_WITH_CT ?
                "palette" : NbtKeys.CUSTOM_TEXTURE));
        this.setNickname(nbt.getString(NbtKeys.NICKNAME));
        this.setCaughtBall(nbt.hasKey(NbtKeys.CAUGHT_BALL) ? EnumPokeballs.getFromIndex(nbt.getByte(NbtKeys.CAUGHT_BALL)) : EnumPokeballs.PokeBall);
        this.setNature(EnumNature.getNatureFromIndex(nbt.getByte(NbtKeys.NATURE)));
        byte mintNature = nbt.hasKey(NbtKeys.MINT_NATURE) ? nbt.getByte(NbtKeys.MINT_NATURE) : -1;
        this.mintNature = mintNature == -1 ? null : EnumNature.getNatureFromIndex(mintNature);
        if (NBT_VERSION == 0 && this.mintNature == EnumNature.Hardy) {
            this.mintNature = null;
        }

        this.setGrowth(EnumGrowth.getGrowthFromIndex(nbt.getByte(NbtKeys.GROWTH)));
        this.eggCycles = nbt.hasKey(NbtKeys.EGG_CYCLES) ? nbt.getInteger(NbtKeys.EGG_CYCLES) : null;
        this.eggSteps = this.eggCycles != null && nbt.hasKey(NbtKeys.STEPS) ? nbt.getInteger(NbtKeys.STEPS) : null;
        this.originalTrainerName = nbt.getString(NbtKeys.ORIGINAL_TRAINER);
        if (nbt.hasUniqueId((NbtKeys.ORIGINAL_TRAINER_UUID))) {
            this.originalTrainerUUID = nbt.getUniqueId(NbtKeys.ORIGINAL_TRAINER_UUID);
        }

        //restoreOT()
        if (FMLCommonHandler.instance().getSide().isServer() && this.originalTrainerName != null && !this.originalTrainerName.isEmpty() && this.originalTrainerUUID == null) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server != null) {
                EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(this.originalTrainerName);
                if (player != null) {
                    this.originalTrainerUUID = player.getUniqueID();
                } else if (this.storage != null) {
                    this.originalTrainerName = this.getOwnerName();
                    this.originalTrainerUUID = this.getOwnerPlayerUUID();
                    if (this.originalTrainerUUID == null) {
                        this.originalTrainerUUID = this.getOwnerTrainerUUID();
                    }
                }
            }
        }

        this.levelContainer.setLevel(nbt.getInteger(NbtKeys.LEVEL));
        this.dynamaxLevel = nbt.getInteger(NbtKeys.DYNAMAX_LEVEL);
        if (EnumGigantamaxPokemon.hasGigantamaxForm(this, true)) {
            this.gigantamaxFactor = nbt.getBoolean(NbtKeys.GIGANTAMAX_FACTOR);
        } else {
            this.gigantamaxFactor = false;
        }

        this.experience = nbt.getInteger(NbtKeys.EXP);
        this.setDoesLevel(nbt.getBoolean(NbtKeys.DOES_LEVEL));
        this.setFriendship(nbt.getShort(NbtKeys.FRIENDSHIP));
        //TODO: Temporarily don't need to be changed
        this.moveset.readFromNBT(nbt);

        //Special processing of defense → defence
        this.defenseTo1122(nbt);

        this.stats.readFromNBT(nbt);
        this.bonusStats.readFromNBT(nbt);
        this.health = nbt.getInteger(NbtKeys.HEALTH);

        //TODO: Check things here 2
        if (this.getExtraStats() != null) {
            this.extraStats.readFromNBT(nbt);
        }

        if (nbt.hasKey(NbtKeys.ABILITY_SLOT)) {
            this.setAbilitySlot(nbt.getByte(NbtKeys.ABILITY_SLOT));
        } else if (nbt.hasKey(NbtKeys.ABILITY)) {
            try {
                String abilityName = nbt.getString(NbtKeys.ABILITY);

                try {
                    AbilityBase.getAbility(abilityName).get();
                    this.setAbility(abilityName);
                } catch (Exception var10) {
                    if (abilityName.equals("ComingSoon")) {
                        if (nbt.hasKey(NbtKeys.ABILITY_SLOT)) {
                            int tempAbilitySlot = nbt.getInteger(NbtKeys.ABILITY_SLOT);
                            if (getBaseStats().getAbilitiesArray() != null && getBaseStats().getAbilitiesArray()[tempAbilitySlot] != null) {
                                setAbility(AbilityBase.getAbility(getBaseStats().getAbilitiesArray()[tempAbilitySlot]).get());
                            }
                        } else if (RandomHelper.getRandomChance(1.0F / PixelmonConfig.getHiddenAbilityRate(this.dimension))) {
                            this.setAbilitySlot(2);
                        } else {
                            this.setAbilitySlot(RandomHelper.getRandomNumberBetween(0, this.getBaseStats().getAbilitiesArray()[1] == null ? 0 : 1));
                        }
                    } else if (RandomHelper.getRandomChance(1.0F / PixelmonConfig.getHiddenAbilityRate(this.dimension))) {
                        this.setAbilitySlot(2);
                    } else {
                        this.setAbilitySlot(RandomHelper.getRandomNumberBetween(0, this.getBaseStats().getAbilitiesArray()[1] == null ? 0 : 1));
                    }
                }
            } catch (Exception var11) {
                Pixelmon.LOGGER.info("Didn't have an Ability; giving it one.");
                if (RandomHelper.getRandomChance(1.0F / PixelmonConfig.getHiddenAbilityRate(this.dimension))) {
                    this.setAbilitySlot(2);
                } else {
                    this.setAbilitySlot(RandomHelper.getRandomNumberBetween(0, this.getBaseStats().getAbilitiesArray()[1] == null ? 0 : 1));
                }
            }
        } else if (RandomHelper.getRandomChance(1.0F / PixelmonConfig.getHiddenAbilityRate(this.dimension))) {
            this.setAbilitySlot(2);
        } else {
            this.setAbilitySlot(RandomHelper.getRandomNumberBetween(0, this.getBaseStats().getAbilitiesArray()[1] == null ? 0 : 1));
        }

        if (nbt.hasKey(NbtKeys.POKERUS)) {
            this.pokerus = Pokerus.deserializeFromNBT(nbt.getCompoundTag(NbtKeys.POKERUS));
        }

        this.inRanch = false;
        this.relearnableMoves = new ArrayList<>();

        if (nbt.hasKey(NbtKeys.EGG_MOVES)) {
            for (int moveID : nbt.getIntArray(NbtKeys.EGG_MOVES)) {
                this.relearnableMoves.add(moveID);
            }
        }
        if (nbt.hasKey(NbtKeys.RELEARNABLE_MOVES)) {
            for (int moveID2 : nbt.getIntArray(NbtKeys.RELEARNABLE_MOVES)) {
                if (!this.relearnableMoves.contains(moveID2)) {
                    this.relearnableMoves.add(moveID2);
                }
            }
        }

        this.status = StatusPersist.readStatusFromNBT(nbt);
        if (nbt.hasKey(NbtKeys.HELD_ITEM_STACK)) {
            NBTTagCompound compound = nbt.getCompoundTag(NbtKeys.HELD_ITEM_STACK);
            if (RemapHandler.modfix != null) {
                compound = FMLCommonHandler.instance().getDataFixer().process(FixTypes.ITEM_INSTANCE, compound);
            }
            ItemStack stack = new ItemStack(compound);
            this.heldItem = stack.isEmpty() ? ItemStack.EMPTY : stack;
        }

        this.persistentData = nbt.getCompoundTag("PersistentData");
        if (nbt.hasKey(NbtKeys.PIXELMON_ID_1)) {
            int[] id = {nbt.getInteger(NbtKeys.PIXELMON_ID_1), nbt.getInteger(NbtKeys.PIXELMON_ID_2)};
            this.persistentData.setIntArray("OLD_pixelmonID", id);
        }
        if (nbt.hasKey(NbtKeys.FORGE_DATA) && nbt.hasKey(NbtKeys.PIXELMON_ID_1)) {
            NBTTagCompound customEntityData = nbt.getCompoundTag(NbtKeys.FORGE_DATA);
            this.persistentData.merge(customEntityData);
        }
        if (nbt.hasKey(NbtKeys.IS_EGG) && !nbt.getBoolean(NbtKeys.IS_EGG)) {
            this.eggCycles = null;
        }
        if (nbt.hasKey(NbtKeys.MOVE_SKILL_COOLDOWNS)) {
            NBTTagCompound moveSkillCooldowns = nbt.getCompoundTag(NbtKeys.MOVE_SKILL_COOLDOWNS);
            //TODO: There're differences between them
            for (String moveSkillID : moveSkillCooldowns.getKeySet()) {
                if (moveSkillID.endsWith("Most")) {
                    moveSkillID = moveSkillID.substring(0, moveSkillID.length() - 4);
                }
                MoveSkill moveSkill = MoveSkill.getMoveSkillByID(moveSkillID);
                if (moveSkill != null) {
                    UUID cooldown = moveSkillCooldowns.getUniqueId(moveSkill.id);
                    this.moveSkillCooldownData.put(moveSkill.id, new Tuple<>(cooldown.getMostSignificantBits(), cooldown.getLeastSignificantBits()));
                }
            }
        }

        if (nbt.hasKey(NbtKeys.SPEC_FLAGS)) {
            for (NBTBase nbtBase : nbt.getTagList(NbtKeys.SPEC_FLAGS, 8)) {
                String s = ((NBTTagString) nbtBase).getString();
                this.specFlags.add(s);
            }
        } else {
            for (ISpecType specType : PokemonSpec.extraSpecTypes) {
                if (specType instanceof SpecFlag) {
                    String key = ((SpecFlag) specType).key;
                    if (this.persistentData.getBoolean(key)) {
                        this.specFlags.add(((SpecFlag) specType).key);
                        this.persistentData.removeTag(key);
                    }
                }
            }
        }
        if (nbt.hasKey(NbtKeys.DISPLAY_RIBBON)) {
            String disp = nbt.getString(NbtKeys.DISPLAY_RIBBON);
            this.displayedRibbon = disp == "" ? EnumRibbonType.NONE : EnumRibbonType.valueOf(disp);
        }
        if (nbt.hasKey(NbtKeys.RIBBONS)) {
            this.ribbons.clear();
            for (NBTBase nbtBase : nbt.getTagList(NbtKeys.RIBBONS, 8)) {
                this.ribbons.add(EnumRibbonType.valueOf(((NBTTagString) nbtBase).getString()));
            }
        }
    }

    //TODO: Deal with Minior and Deoxys.
    //1.16: com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats
    //1.12: com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setShort("ndex", (short) this.species.getNationalPokedexInteger());
        //TODO: 1.16.5 compatible
        int form = this.getFormEnum().isTemporary() ? this.getFormEnum().getDefaultFromTemporary(this).getForm() : this.getForm();
        nbt.setByte(NbtKeys.FORM, (byte) form);
        nbt.setByte(NbtKeys.GENDER, (byte) this.getGender().ordinal());
        nbt.setByte("NBT_VERSION", (byte) 1);
        nbt.setUniqueId(NbtKeys.UUID, this.uuid);
        nbt.setBoolean(NbtKeys.IS_SHINY, this.isShiny);
        if (!this.customTexture.isEmpty()) {
            nbt.setString(PixelmonBankConfig.OVERRIDE_PALETTE_WITH_CT ?
                            "palette" : NbtKeys.CUSTOM_TEXTURE,
                    this.customTexture);
        } else {
            nbt.removeTag(NbtKeys.CUSTOM_TEXTURE);
        }
        if (this.nickname != null && !this.nickname.isEmpty() && !Objects.equals(this.nickname, this.species.name)) {
            nbt.setString(NbtKeys.NICKNAME, this.nickname);
        } else {
            nbt.removeTag(NbtKeys.NICKNAME);
        }
        if (this.caughtBall != null) {
            nbt.setByte(NbtKeys.CAUGHT_BALL, (byte) this.caughtBall.ordinal());
        } else {
            nbt.removeTag(NbtKeys.CAUGHT_BALL);
        }
        nbt.setByte(NbtKeys.NATURE, (byte) this.nature.index);
        if (this.mintNature != null) {
            nbt.setByte(NbtKeys.MINT_NATURE, (byte) this.mintNature.index);
        }
        nbt.setByte(NbtKeys.GROWTH, (byte) this.growth.index);
        if (this.eggCycles != null) {
            nbt.setInteger(NbtKeys.EGG_CYCLES, this.eggCycles);
            nbt.setInteger(NbtKeys.STEPS, getEggSteps());
        } else {
            nbt.removeTag(NbtKeys.EGG_CYCLES);
        }
        if (this.originalTrainerName != null && !this.originalTrainerName.isEmpty()) {
            nbt.setString(NbtKeys.ORIGINAL_TRAINER, this.originalTrainerName);
        } else {
            nbt.removeTag(NbtKeys.ORIGINAL_TRAINER);
        }
        if (this.originalTrainerUUID != null) {
            nbt.setUniqueId(NbtKeys.ORIGINAL_TRAINER_UUID, this.originalTrainerUUID);
        } else {
            nbt.removeTag(NbtKeys.ORIGINAL_TRAINER_UUID);
        }
        nbt.setInteger(NbtKeys.LEVEL, this.level);
        nbt.setInteger(NbtKeys.DYNAMAX_LEVEL, this.dynamaxLevel);
        nbt.setBoolean(NbtKeys.GIGANTAMAX_FACTOR, this.gigantamaxFactor);
        nbt.setInteger(NbtKeys.EXP, this.experience);
        nbt.setBoolean(NbtKeys.DOES_LEVEL, this.doesLevel);
        nbt.setBoolean(NbtKeys.IS_IN_RANCH, this.inRanch);
        nbt.setShort(NbtKeys.FRIENDSHIP, (short) this.friendship);

        this.moveset.writeToNBT(nbt);
        this.stats.writeToNBT(nbt);
        this.bonusStats.writeToNBT(nbt);

        //Special processing of defence → defense
        defenceTo1165(nbt);

        nbt.setInteger(NbtKeys.HEALTH, this.health);
        if (getExtraStats() != null) {
            this.extraStats.writeToNBT(nbt);
        }
        if (this.abilitySlot != -1) {
            nbt.setByte(NbtKeys.ABILITY_SLOT, (byte) this.abilitySlot);
        } else if (this.ability != null) {
            String abilityName = getAbilityName();
            nbt.setString("Ability", abilityName);
        }
        if (this.pokerus != null) {
            nbt.setTag(NbtKeys.POKERUS, this.pokerus.serializeToNBT());
        }
        this.status.writeToNBT(nbt);
        if (this.heldItem != null && !this.heldItem.isEmpty()) {
            nbt.setTag(NbtKeys.HELD_ITEM_STACK, this.heldItem.writeToNBT(new NBTTagCompound()));
        } else {
            nbt.removeTag(NbtKeys.HELD_ITEM_STACK);
        }
        nbt.setTag("PersistentData", this.persistentData);
        if (!this.relearnableMoves.isEmpty()) {
            int[] relearnableMoves = new int[this.relearnableMoves.size()];
            for (int i = 0; i < this.relearnableMoves.size(); i++) {
                relearnableMoves[i] = this.relearnableMoves.get(i);
            }
            nbt.setIntArray(NbtKeys.RELEARNABLE_MOVES, relearnableMoves);
        }
        NBTTagCompound moveSkillCooldowns = new NBTTagCompound();
        long cur = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0].getTotalWorldTime();
        for (Map.Entry<String, Tuple<Long, Long>> entry : this.moveSkillCooldownData.entrySet()) {
            if (entry.getValue().getSecond() >= cur) {
                moveSkillCooldowns.setUniqueId(entry.getKey(), new UUID(entry.getValue().getFirst(), entry.getValue().getSecond()));
            }
        }
        if (moveSkillCooldowns.getSize() != 0) {
            nbt.setTag(NbtKeys.MOVE_SKILL_COOLDOWNS, moveSkillCooldowns);
        }
        NBTTagList specList = new NBTTagList();
        for (String specFlag : this.specFlags) {
            specList.appendTag(new NBTTagString(specFlag));
        }
        nbt.setTag(NbtKeys.SPEC_FLAGS, specList);
        if (this.displayedRibbon != null) {
            nbt.setString(NbtKeys.DISPLAY_RIBBON, this.displayedRibbon.toString());
        }
        NBTTagList ribbonList = new NBTTagList();
        for (EnumRibbonType ribbon : this.ribbons) {
            ribbonList.appendTag(new NBTTagString(ribbon.toString()));
        }
        nbt.setTag(NbtKeys.RIBBONS, ribbonList);
        return nbt;
    }

    /**
     * Change "defense" to "defence" in 1.12.2 version
     *
     * @param nbt
     */
    private void defenseTo1122(NBTTagCompound nbt) {
        //Stat
        nbt.setShort("StatsDefence", nbt.getShort("StatsDefense"));
        nbt.removeTag("StatsDefense");

        nbt.setShort("StatsSpecialDefence", nbt.getShort("StatsSpecialDefense"));
        nbt.removeTag("StatsSpecialDefense");

        //IV and HT
        nbt.setByte("IVDefence", nbt.getByte("IVDefense"));
        nbt.removeTag("IVDefense");

        if (nbt.hasKey("IV_HTDefense")) {
            nbt.setBoolean("IV_HTDefence", nbt.getBoolean("IV_HTDefense"));
            nbt.removeTag("IV_HTDefense");
        }

        if (nbt.hasKey("IV_HTSpecialDefense")) {
            nbt.setBoolean("IV_HTSpecialDefence", nbt.getBoolean("IV_HTSpecialDefense"));
            nbt.removeTag("IV_HTSpecialDefense");
        }

        //EV
        nbt.setShort("EVDefence", nbt.getShort("EVDefense"));
        nbt.removeTag("EVDefense");

        nbt.setShort("EVSpecialDefence", nbt.getShort("EVSpecialDefense"));
        nbt.removeTag("EVSpecialDefense");

        //Bonus
        if (nbt.hasKey("BonusDefense")) {
            nbt.setShort("BonusDefence", nbt.getShort("BonusDefense"));
            nbt.removeTag("BonusDefense");
        }

        if (nbt.hasKey("BonusSpDefense")) {
            nbt.setShort("BonusSpDefence", nbt.getShort("BonusSpDefense"));
            nbt.removeTag("BonusSpDefense");
        }
    }

    /**
     * Change "defence" to "defense" in 1.16.5 version
     *
     * @param nbt
     */
    private void defenceTo1165(NBTTagCompound nbt) {
        //Stat
        nbt.setShort("StatsDefense", nbt.getShort("StatsDefence"));
        nbt.removeTag("StatsDefence");

        nbt.setShort("StatsSpecialDefense", nbt.getShort("StatsSpecialDefence"));
        nbt.removeTag("StatsSpecialDefence");

        //IV and HT
        nbt.setByte("IVDefense", nbt.getByte("IVDefence"));
        nbt.removeTag("IVDefence");

        if (nbt.hasKey("IV_HTDefence")) {
            nbt.setBoolean("IV_HTDefense", nbt.getBoolean("IV_HTDefence"));
            nbt.removeTag("IV_HTDefence");
        }

        if (nbt.hasKey("IV_HTSpecialDefence")) {
            nbt.setBoolean("IV_HTSpecialDefense", nbt.getBoolean("IV_HTSpecialDefence"));
            nbt.removeTag("IV_HTSpecialDefence");
        }

        //EV
        nbt.setShort("EVDefense", nbt.getShort("EVDefence"));
        nbt.removeTag("EVDefence");

        nbt.setShort("EVSpecialDefense", nbt.getShort("EVSpecialDefence"));
        nbt.removeTag("EVSpecialDefence");

        //Bonus
        if (nbt.hasKey("BonusDefence")) {
            nbt.setShort("BonusDefense", nbt.getShort("BonusDefence"));
            nbt.removeTag("BonusDefence");
        }

        if (nbt.hasKey("BonusSpDefence")) {
            nbt.setShort("BonusSpDefense", nbt.getShort("BonusSpDefence"));
            nbt.removeTag("BonusSpDefence");
        }
    }
}
