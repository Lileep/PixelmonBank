package com.github.lileep.pixelmonbank.data.bean;

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
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.DeoxysStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.MeltanStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.MiniorStats;
import com.pixelmonmod.pixelmon.enums.*;
import com.pixelmonmod.pixelmon.enums.forms.*;
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

    public void readFromNBTNew(NBTTagCompound nbt) {
        if (nbt.hasKey("ndex")) {
            this.dsSpecies.set(this, nbt.getInteger("ndex"));
        } else if (nbt.hasKey(NbtKeys.NAME)) {
            this.dsSpecies.set(this, EnumSpecies.getFromName(nbt.getString(NbtKeys.NAME)).orElse(EnumSpecies.MissingNo).getNationalPokedexInteger());
        }

//        if (nbt.hasKey(NbtKeys.FORM)) {
//            this.dsForm.set(this, nbt.getByte(NbtKeys.FORM));
//        }
//        this.dsForm.set(this, (byte) 0);
        readFromNBTSpecialMons(nbt);

        if (nbt.hasKey(NbtKeys.GENDER)) {
            this.dsGender.set(this, nbt.getByte(NbtKeys.GENDER));
        }

        if (this.species == EnumSpecies.MissingNo) {
            EnumMissingNo.migrate(this);
        }

        int NBT_VERSION = nbt.getByte("NBT_VERSION");
        this.setUUID(nbt.getUniqueId(NbtKeys.UUID));

        //Shiny is done in readFromNBTSpecialMons

        //Compatible with previous versions
        byte special = nbt.getByte(NbtKeys.SPECIAL_TEXTURE);
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

        if (this.getExtraStats() != null) {
            if (!this.species.is(EnumSpecies.Deoxys) && !this.species.is(EnumSpecies.Minior) && !this.species.is(EnumSpecies.Meltan)) {
                this.extraStats.readFromNBT(nbt);
            }
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

    //1.16: com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats
    //1.12: com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats
    public NBTTagCompound writeToNBTNew(NBTTagCompound nbt) {
        nbt.setShort("ndex", (short) this.species.getNationalPokedexInteger());
        //1.16 compatible
        nbt.setString(NbtKeys.FORM, "");
        nbt.setByte(NbtKeys.GENDER, (byte) this.getGender().ordinal());
        nbt.setByte("NBT_VERSION", (byte) 1);
        nbt.setUniqueId(NbtKeys.UUID, this.uuid);

        nbt.setString("palette", this.isShiny ? "shiny" : "none");
        writeToNBTSpecialMons(nbt);

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

        //TODO: Need to change to name instead of id
        this.moveset.writeToNBT(nbt);
        this.stats.writeToNBT(nbt);
        this.bonusStats.writeToNBT(nbt);

        //Special processing: defence → defense
        defenceTo1165(nbt);

        nbt.setInteger(NbtKeys.HEALTH, this.health);

        //1.12 compatible
        if (getExtraStats() != null) {
            if (!this.species.is(EnumSpecies.Deoxys) && !this.species.is(EnumSpecies.Minior) && !this.species.is(EnumSpecies.Meltan)) {
                this.extraStats.writeToNBT(nbt);
            }
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
     * All judgement must be done since it need to read a 1.12 nbt first
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

    private void readFromNBTSpecialMons(NBTTagCompound nbt) {
        String form = nbt.getString(NbtKeys.FORM);
        String palette = nbt.getString("palette");
        byte formByte = 0;
        boolean isShiny = "shiny".equals(nbt.getString("palette"));
        if (this.species.is(EnumSpecies.Minior)) {
            //If there's no color in palette(like shiny case), by default give it a random one
            byte colorIndex = (byte) RandomHelper.getRandomNumberBetween(0, EnumMinior.values().length - 1);
            for (IEnumForm formElem : this.species.getPossibleForms(true)) {
                if (palette.equalsIgnoreCase(formElem.getName())) {
                    //In this case, palette is a certain color,
                    // so formByte must have a color, i.e., formByte > 0
                    colorIndex = formElem.getForm();
                    break;
                }
            }
            if (!"meteor".equals(form)) {
                formByte = colorIndex;
            }
            //Deal with extra stats
            if (this.getExtraStats() != null) {
                ((MiniorStats) this.extraStats).color = (byte) (colorIndex - 1);
            }
        } else if (this.species.is(EnumSpecies.Deoxys)) {
            for (IEnumForm formElem : this.species.getPossibleForms(true)) {
                if (form.equalsIgnoreCase(formElem.getName())) {
                    formByte = formElem.getForm();
                    break;
                }
            }
            //Deal with extra stats
            if (this.getExtraStats() != null) {
                ((DeoxysStats) this.extraStats).setSus(formByte == EnumDeoxys.Sus.getForm());
            }
        } else if (this.species.is(EnumSpecies.Pikachu, EnumSpecies.Ponyta, EnumSpecies.Tauros, EnumSpecies.Dragonite, EnumSpecies.Pichu, EnumSpecies.Natu, EnumSpecies.Mareep, EnumSpecies.Heracross, EnumSpecies.Lunatone, EnumSpecies.Castform, EnumSpecies.Bidoof, EnumSpecies.Burmy, EnumSpecies.Wormadam, EnumSpecies.Cherrim, EnumSpecies.Gastrodon, EnumSpecies.Rotom, EnumSpecies.Dialga, EnumSpecies.Palkia, EnumSpecies.Giratina, EnumSpecies.Shaymin, EnumSpecies.Arceus, EnumSpecies.Unfezant, EnumSpecies.Basculin, EnumSpecies.Darmanitan, EnumSpecies.Deerling, EnumSpecies.Sawsbuck, EnumSpecies.Tornadus, EnumSpecies.Thundurus, EnumSpecies.Landorus, EnumSpecies.Kyurem, EnumSpecies.Keldeo, EnumSpecies.Meloetta, EnumSpecies.Genesect, EnumSpecies.Greninja, EnumSpecies.Floette, EnumSpecies.Furfrou, EnumSpecies.Aegislash, EnumSpecies.Xerneas, EnumSpecies.Zygarde, EnumSpecies.Hoopa, EnumSpecies.Oricorio, EnumSpecies.Lycanroc, EnumSpecies.Wishiwashi, EnumSpecies.Silvally, EnumSpecies.Minior, EnumSpecies.Mimikyu, EnumSpecies.Lunala, EnumSpecies.Necrozma, EnumSpecies.Magearna, EnumSpecies.Marshadow, EnumSpecies.Wooloo, EnumSpecies.Dubwool, EnumSpecies.Cramorant, EnumSpecies.Toxtricity, EnumSpecies.Sinistea, EnumSpecies.Polteageist, EnumSpecies.Eiscue, EnumSpecies.Morpeko, EnumSpecies.Zacian, EnumSpecies.Zamazenta, EnumSpecies.Eternatus, EnumSpecies.Urshifu, EnumSpecies.Zarude, EnumSpecies.Calyrex, EnumSpecies.Enamorus)) {
            //No Deoxys here
            for (IEnumForm formElem : this.species.getPossibleForms(true)) {
                if (form.equalsIgnoreCase(formElem.getName())) {
                    formByte = formElem.getForm();
                    break;
                }
            }
        } else if (this.species.is(EnumSpecies.Mareep, EnumSpecies.Wooloo, EnumSpecies.Dubwool)) {
            //Not 'none' in 1.12
            String formStr = "normal";
            if ("shorn".equals(form)) {
                formStr = form;
            } else {
                if (!"shiny".equals(palette)) {
                    formStr = palette;
                }
            }
            for (IEnumForm formElem : this.species.getPossibleForms(true)) {
                if (formStr.equalsIgnoreCase(formElem.getName())) {
                    formByte = formElem.getForm();
                    break;
                }
            }
        } else if (this.species.is(EnumSpecies.Floette)) {
            //Shiny Floette has a shiny suffix instead of a whole shiny palette, e.g. 'yellowshiny'
            isShiny = palette.endsWith("shiny");
            String formStr = "az".equals(form) ? form : (isShiny ? palette.replace("shiny", "") : palette);
            for (IEnumForm formElem : this.species.getPossibleForms(true)) {
                if (formStr.equalsIgnoreCase(formElem.getName())) {
                    formByte = formElem.getForm();
                    break;
                }
            }
        } else {
            if (this.species.is(EnumSpecies.Meltan)) {
                //Deal with extra stats
                if (this.getExtraStats() != null) {
                    ((MeltanStats) this.extraStats).oresSmelted = nbt.getInteger("NuggetsFed");
                }
            }

            //Process form name to 1.12 format

            //Xerneas and regional forms don't need to be special judged here.

            String paletteAndForm = palette;
            //Some mons have their form go first
            String formAndPalette = palette;
            if (!form.isEmpty()) {
                if ("none".equals(palette) || "normal".equals(palette)) {
                    paletteAndForm = formAndPalette = form;
                } else {
                    paletteAndForm += ("_" + form);
                    formAndPalette = form + "_" + formAndPalette;
                }
            }
            //Judge simple one
            for (IEnumForm formElem : this.species.getPossibleForms(true)) {
                String formElemName = formElem.getName();
                if (form.equalsIgnoreCase(formElemName) || palette.equalsIgnoreCase(formElemName)) {
                    formByte = formElem.getForm();
                    break;
                }
            }
            //Judge combined one
            for (IEnumForm formElem : this.species.getPossibleForms(true)) {
                String formElemName = formElem.getName();
                if (paletteAndForm.equalsIgnoreCase(formElemName) || formAndPalette.equalsIgnoreCase(formElemName)) {
                    formByte = formElem.getForm();
                    break;
                }
            }
        }
        this.dsForm.set(this, formByte);
        //1.16 shiny compatible
        this.setShiny(isShiny);
    }

    private void writeToNBTSpecialMons(NBTTagCompound nbt) {
        IEnumForm pokemonForm = this.getFormEnum();
        if (pokemonForm.isTemporary()) {
            if (pokemonForm != EnumSpecial.Zombie) {
                pokemonForm = pokemonForm.getDefaultFromTemporary(this);
            }
        }
        //Some mons need to be judged
        if (this.species.is(EnumSpecies.Minior)) {
            byte colorIndex = ((MiniorStats) this.getExtraStats()).color;
            if (!"shiny".equals(nbt.getString("palette"))) {
                nbt.setString("palette", EnumMinior.values()[colorIndex + 1].getName().toLowerCase());
            }
            nbt.setString(NbtKeys.FORM, pokemonForm == EnumMinior.METEOR ? "meteor" : "core");
        } else if (this.species.is(EnumSpecies.Pikachu, EnumSpecies.Ponyta, EnumSpecies.Tauros, EnumSpecies.Dragonite, EnumSpecies.Pichu, EnumSpecies.Natu, EnumSpecies.Mareep, EnumSpecies.Heracross, EnumSpecies.Lunatone, EnumSpecies.Castform, EnumSpecies.Deoxys, EnumSpecies.Bidoof, EnumSpecies.Burmy, EnumSpecies.Wormadam, EnumSpecies.Cherrim, EnumSpecies.Gastrodon, EnumSpecies.Rotom, EnumSpecies.Dialga, EnumSpecies.Palkia, EnumSpecies.Giratina, EnumSpecies.Shaymin, EnumSpecies.Arceus, EnumSpecies.Unfezant, EnumSpecies.Basculin, EnumSpecies.Darmanitan, EnumSpecies.Deerling, EnumSpecies.Sawsbuck, EnumSpecies.Tornadus, EnumSpecies.Thundurus, EnumSpecies.Landorus, EnumSpecies.Kyurem, EnumSpecies.Keldeo, EnumSpecies.Meloetta, EnumSpecies.Genesect, EnumSpecies.Greninja, EnumSpecies.Floette, EnumSpecies.Furfrou, EnumSpecies.Aegislash, EnumSpecies.Xerneas, EnumSpecies.Zygarde, EnumSpecies.Hoopa, EnumSpecies.Oricorio, EnumSpecies.Lycanroc, EnumSpecies.Wishiwashi, EnumSpecies.Silvally, EnumSpecies.Minior, EnumSpecies.Mimikyu, EnumSpecies.Lunala, EnumSpecies.Necrozma, EnumSpecies.Magearna, EnumSpecies.Marshadow, EnumSpecies.Wooloo, EnumSpecies.Dubwool, EnumSpecies.Cramorant, EnumSpecies.Toxtricity, EnumSpecies.Sinistea, EnumSpecies.Polteageist, EnumSpecies.Eiscue, EnumSpecies.Morpeko, EnumSpecies.Zacian, EnumSpecies.Zamazenta, EnumSpecies.Eternatus, EnumSpecies.Urshifu, EnumSpecies.Zarude, EnumSpecies.Calyrex, EnumSpecies.Enamorus)) {
            //Rapidash, Porygon, Kecleon, Weavile, Zoroark, Solgaleo don't need to be judged
            nbt.setString(NbtKeys.FORM, pokemonForm.getName().toLowerCase());
        } else if (this.species.is(EnumSpecies.Xerneas)) {
            if (pokemonForm.getName().toLowerCase().endsWith("creator")) {
                nbt.setString("palette", "creator");
            }
            nbt.setString(NbtKeys.FORM, pokemonForm.getSpriteSuffix("shiny".equals(nbt.getString("palette"))).toLowerCase().substring(1));
        } else if (this.species.is(EnumSpecies.Aegislash)) {
            if (pokemonForm.getName().toLowerCase().endsWith("alter")) {
                nbt.setString("palette", "alter");
            }
            nbt.setString(NbtKeys.FORM, pokemonForm.getName().toLowerCase().startsWith("shield") ? "sheild" : "blade");
        } else if (this.species.is(EnumSpecies.Mareep, EnumSpecies.Wooloo, EnumSpecies.Dubwool)) {
            String lowerFormName = pokemonForm.getName().toLowerCase();
            if ("shorn".equals(lowerFormName)) {
                //By default, nbt.setString("palette", "none"); is done
                nbt.setString(NbtKeys.FORM, lowerFormName);
            } else {
                if (!"normal".equals(lowerFormName) && !"shiny".equals(nbt.getString("palette"))) {
                    nbt.setString("palette", lowerFormName);
                }
            }
        } else if (this.species.is(EnumSpecies.Floette)) {
            String lowerFormName = pokemonForm.getName().toLowerCase();
            if ("az".equals(lowerFormName)) {
                nbt.setString(NbtKeys.FORM, lowerFormName);
            } else {
                nbt.setString("palette", lowerFormName + ("shiny".equals(nbt.getString("palette")) ? "shiny" : ""));
            }
        } else {
            if (this.species.is(EnumSpecies.Meltan)) {
                if (this.getExtraStats() != null) {
                    nbt.setInteger("NuggetsFed", ((MeltanStats) this.getExtraStats()).oresSmelted);
                }
            }

            String palette = pokemonForm.getFormSuffix().toLowerCase();
            if (palette.startsWith("-")) {
                palette = palette.substring(1);
            }

            //regional pokemons have special suffix
            switch (palette) {
                case "alola":
                    nbt.setString(NbtKeys.FORM, "alolan");
                    return;
                case "galar":
                    nbt.setString(NbtKeys.FORM, "galarian");
                    return;
                case "hisuian":
                case "paldean":
                    nbt.setString(NbtKeys.FORM, palette);
                    return;
            }

            //Intelligent override shiny
            if (!"normal".equals(palette) && !"none".equals(palette)) {
                nbt.setString("palette", palette);
                //Process form name, trim '_' and '-'
                String variant = pokemonForm.getName().toLowerCase().replace(palette, "").replaceAll("^_+|_+$", "").replaceAll("^-+|-+$", "");
                nbt.setString(NbtKeys.FORM, variant);
            }
        }
    }
}
