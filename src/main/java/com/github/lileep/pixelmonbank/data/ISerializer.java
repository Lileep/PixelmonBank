package com.github.lileep.pixelmonbank.data;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import java.util.List;

public interface ISerializer {

    String serialize(Pokemon pokemon);

    List<Pokemon> deserialize(DataPack dataPack);

    String getUniqueName();

}
