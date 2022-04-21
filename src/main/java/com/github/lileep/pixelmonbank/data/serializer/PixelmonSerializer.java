package com.github.lileep.pixelmonbank.data.serializer;

import com.github.lileep.pixelmonbank.data.DataPack;
import com.github.lileep.pixelmonbank.data.ISerializer;
import com.github.lileep.pixelmonbank.lib.SerializerReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

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
        data.put(pokemon.getUUID().toString(), pokemon.writeToNBT(new NBTTagCompound()).toString());
        return GSON.toJson(data, TYPE);
    }

    @Override
    public List<Pokemon> deserialize(DataPack dataPack) {
        String jsonData;
        if (Optional.ofNullable(dataPack).isPresent()) {
            jsonData = dataPack.getData(getUniqueName());
        } else {
            System.out.println("dataPack is null.");
            return null;
        }

        Map<String, String> map;
        if (Optional.ofNullable(jsonData).isPresent()) {
            map = GSON.fromJson(jsonData, TYPE);
        } else {
            System.out.println("Analyzing json failed.");
            return null;
        }

        List<Pokemon> pokemonList = new ArrayList<>();

        for (String key : map.keySet()) {
            NBTTagCompound nbtTagCompound;
            try {
                nbtTagCompound = JsonToNBT.getTagFromJson(map.get(key));
                pokemonList.add(Pixelmon.pokemonFactory.create(nbtTagCompound));
            } catch (Exception e) {
                System.out.println("Failed to write nbt.");
                return null;
            }
        }
        return pokemonList;
    }

    @Override
    public String getUniqueName() {
        return SerializerReference.PIXELMON_SERIALIZER;
    }
}
