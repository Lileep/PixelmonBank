package com.github.lileep.pixelmonbank.bean;

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
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.UUID;

public class PokemonBean extends Pokemon {

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("ndex")) {
            this.dsSpecies.set(this, nbt.getInteger("ndex"));
        } else if (nbt.hasKey("Name")) {
            this.dsSpecies.set(this, EnumSpecies.getFromName(nbt.getString("Name")).orElse(EnumSpecies.MissingNo).getNationalPokedexInteger());
        }

        if (nbt.hasKey("Variant")) {
            this.dsForm.set(this, nbt.getByte("Variant"));
        }

        if (nbt.hasKey("Gender")) {
            this.dsGender.set(this, nbt.getByte("Gender"));
        }

        if (this.species == EnumSpecies.MissingNo) {
            EnumMissingNo.migrate(this);
        }

        int NBT_VERSION = nbt.getByte("NBT_VERSION");
        this.setUUID(nbt.getUniqueId(NbtKeys.UUID));
        this.setShiny(nbt.getBoolean(NbtKeys.IS_SHINY));
//        byte special = nbt.getByte("Form");

        String special = nbt.getString(NbtKeys.SPECIAL_TEXTURE);
        //TODO: Test ashen and strike forms
        switch (special) {
            case "roasted":
                this.dsForm.set(this, EnumMagikarp.ROASTED.getForm());
                break;
            case "zombie":
                this.dsForm.set(this, EnumSpecial.Zombie.getForm());
                break;
            case "online":
                this.dsForm.set(this, EnumSpecial.Online.getForm());
                break;
            case "drowned":
                this.dsForm.set(this, EnumSpecial.Drowned.getForm());
                break;
            case "valentine":
                this.dsForm.set(this, EnumSpecial.Valentine.getForm());
                break;
            case "rainbow":
                this.dsForm.set(this, EnumSpecial.Rainbow.getForm());
                break;
            case "alien":
                this.dsForm.set(this, EnumSpecial.Alien.getForm());
                break;
            case "real":
                this.dsForm.set(this, EnumSolgaleo.Real.getForm());
                break;
            case "alter":
                this.dsForm.set(this, EnumSpecial.Alter.getForm());
                break;
            case "pink":
                this.dsForm.set(this, EnumSpecial.Pink.getForm());
                break;
            case "summer":
                this.dsForm.set(this, EnumSpecial.Summer.getForm());
                break;
            case "crystal":
                this.dsForm.set(this, EnumSpecial.Crystal.getForm());
        }

        this.setCustomTexture(nbt.getString(NbtKeys.CUSTOM_TEXTURE));
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
        return super.writeToNBT(nbt);
    }

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

    private void defenceTo1165(NBTTagCompound nbt) {

    }
}
