package com.github.lileep.pixelmonbank.data.serializer;

import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.data.DataPack;
import com.github.lileep.pixelmonbank.data.ISerializer;
import com.github.lileep.pixelmonbank.lib.Reference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Only interact with pixelmons
 */
public class PixelmonSerializer implements ISerializer {

    private static final Gson GSON = new Gson();
    private static final Type TYPE = new TypeToken<HashMap<String, String>>() {
    }.getType();

    @Override
    public String serialize(Pokemon pokemon) {
        Map<String, String> data = new HashMap<>(1);
//        Pokemon pokemonBean = new PokemonBean(pokemon.getUUID());
//        pokemonBean.readFromNBT(pokemon.writeToNBT(new CompoundTag()));
        //TODO: Deal with nbt compound without considering pokemon uuid. Abstract it.
        data.put(pokemon.getUUID().toString(), pokemon.writeToNBT(new CompoundTag()).toString());
        return GSON.toJson(data, TYPE);
    }

    @Override
    public List<Pokemon> deserialize(DataPack dataPack) {
        String jsonData;
        if (Optional.ofNullable(dataPack).isPresent()) {
            jsonData = dataPack.getData(getUniqueName());
        } else {
            PixelmonBank.LOGGER.warn("dataPack is null.");
            return null;
        }

        Map<String, String> map;
        if (Optional.ofNullable(jsonData).isPresent()) {
            map = GSON.fromJson(jsonData, TYPE);
        } else {
            PixelmonBank.LOGGER.warn("Analyzing json failed.");
            return null;
        }

        List<Pokemon> pokemonList = new ArrayList<>();

        for (String key : map.keySet()) {
            CompoundTag compoundTag;
            try {
                compoundTag = TagParser.parseTag(map.get(key));

//                PokemonBean pokemonBean = new PokemonBean((compoundTag.getUUID("UUID")));
//                pokemonBean.readFromNBT(compoundTag);
//                pokemonList.add(pokemonBean);
                pokemonList.add(PokemonFactory.create(compoundTag));
            } catch (Exception e) {
                PixelmonBank.LOGGER.warn("Failed to write nbt.");
                return null;
            }
        }
        return pokemonList;
    }

    @Override
    public String getUniqueName() {
        return Reference.PIXELMON_SERIALIZER;
    }
}
