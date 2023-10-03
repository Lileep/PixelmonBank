package com.github.lileep.pixelmonbank.util;

import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.config.PixelmonBankConfig;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import java.util.List;
import java.util.Set;

public class PokemonOptUtil {
    public static Set<String> RESTRICT_POKEMONS;
    public static Set<String> BLACK_LIST_POKEMONS;

    public static boolean isRestrict(Pokemon pokemon) {
        return isRestrict(pokemon.getLocalizedName()) || isRestrict(pokemon.getSpecies().getName());
    }
    public static boolean isRestrict(String pokemonName) {
        if (RESTRICT_POKEMONS.size() > 0) {
            return RESTRICT_POKEMONS.contains(pokemonName.toLowerCase());
        }
        return false;
    }

    public static boolean isBlackList(Pokemon pokemon) {
        return isBlackList(pokemon.getLocalizedName()) || isBlackList(pokemon.getSpecies().getName());
    }
    public static boolean isBlackList(String pokemonName) {
        if (BLACK_LIST_POKEMONS.size() > 0) {
            return BLACK_LIST_POKEMONS.contains(pokemonName.toLowerCase());
        }
        return false;
    }

    public static void operatePokemon(Pokemon pokemon) {
        PixelmonBankConfig pbkConfig = PixelmonBank.getInstance().getConfig();
        if (pbkConfig.isSterilizeWhenWithdraw()) {
            pokemon.addFlag("unbreedable");
        }
        if (pbkConfig.isUntradifyWhenWithdraw()) {
            pokemon.addFlag("untradeable");
        }
        if (pbkConfig.isResetFriendshipWhenWithdraw()) {
            pokemon.setFriendship(pokemon.getForm().getSpawn().getBaseFriendship());
        }
    }

    public static void operatePokemons(List<Pokemon> pokemonList) {
        PixelmonBankConfig pbkConfig = PixelmonBank.getInstance().getConfig();
        if (pbkConfig.isSterilizeWhenWithdraw()) {
            pokemonList.forEach(p->p.addFlag("unbreedable"));
        }
        if (pbkConfig.isUntradifyWhenWithdraw()) {
            pokemonList.forEach(p->p.addFlag("untradeable"));
        }
        if (pbkConfig.isResetFriendshipWhenWithdraw()) {
            pokemonList.forEach(p->p.setFriendship(p.getForm().getSpawn().getBaseFriendship()));
        }
    }

}
