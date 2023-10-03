package com.github.lileep.pixelmonbank.util;

import com.github.lileep.pixelmonbank.config.PixelmonBankConfig;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import java.util.List;
import java.util.Set;

public class PokemonOptUtil {
    public static Set<String> RESTRICT_POKEMONS;
    public static Set<String> BLACK_LIST_POKEMONS;

    public static boolean isRestrict(Pokemon pokemon) {
        return isRestrict(pokemon.getLocalizedName()) || isRestrict(pokemon.getSpecies().getPokemonName());
    }
    public static boolean isRestrict(String pokemonName) {
        if (RESTRICT_POKEMONS.size() > 0) {
            return RESTRICT_POKEMONS.contains(pokemonName.toLowerCase());
        }
        return false;
    }

    public static boolean isBlackList(Pokemon pokemon) {
        return isBlackList(pokemon.getLocalizedName()) || isBlackList(pokemon.getSpecies().getPokemonName());
    }
    public static boolean isBlackList(String pokemonName) {
        if (BLACK_LIST_POKEMONS.size() > 0) {
            return BLACK_LIST_POKEMONS.contains(pokemonName.toLowerCase());
        }
        return false;
    }

    public static void operatePokemon(Pokemon pokemon) {
        if (PixelmonBankConfig.STERILIZE_WHEN_WITHDRAW) {
            pokemon.addSpecFlag("unbreedable");
        }
        if (PixelmonBankConfig.UNTRADIFY_WHEN_WITHDRAW) {
            pokemon.addSpecFlag("untradeable");
        }
        if (PixelmonBankConfig.RESET_FRIENDSHIP_WHEN_WITHDRAW) {
            pokemon.setFriendship(pokemon.getBaseStats().getBaseFriendship());
        }
    }

    public static void operatePokemons(List<Pokemon> pokemonList) {
        if (PixelmonBankConfig.STERILIZE_WHEN_WITHDRAW) {
            pokemonList.forEach(p->p.addSpecFlag("unbreedable"));
        }
        if (PixelmonBankConfig.UNTRADIFY_WHEN_WITHDRAW) {
            pokemonList.forEach(p->p.addSpecFlag("untradeable"));
        }
        if (PixelmonBankConfig.RESET_FRIENDSHIP_WHEN_WITHDRAW) {
            pokemonList.forEach(p->p.setFriendship(p.getBaseStats().getBaseFriendship()));
        }
    }

}
