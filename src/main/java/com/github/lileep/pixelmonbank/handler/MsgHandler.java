package com.github.lileep.pixelmonbank.handler;

import com.envyful.api.forge.chat.UtilChatColour;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.IVStore;
import com.pixelmonmod.pixelmon.api.pokemon.stats.Moveset;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class MsgHandler {

    /**
     * Add prefix to the given msg,
     * translate color char to real color,
     * process the args
     *
     * @param msg  Message to process
     * @param args arguments
     * @return processed message
     */
    public static Component prefixedColorMsg(String msg, Object... args) {
        return UtilChatColour.colour(PixelmonBank.getInstance().getLocale().getPrefix() + String.format(msg, args));
    }

    /**
     * Format an IVStore to a separated string
     *
     * @param ivs       IVStore that needs to be formatted
     * @param separator iv separator
     * @return Formatted string
     */
    public static String formatIV(IVStore ivs, char separator) {
        if (Optional.ofNullable(ivs).isEmpty()) {
            return "(!)";
        }
        StringBuilder b = new StringBuilder();
        int i = 0;
        for (BattleStatsType type : BattleStatsType.getEVIVStatValues()) {
            if (ivs.isHyperTrained(type)) {
                b.append(String.format("&6%3d", 31));
            } else {
                b.append(String.format("%3d", ivs.getStat(type)));
            }
            if (i == 5) {
                return b.toString();
            }
            b.append(" &b").append(separator).append("&d ");
            i++;
        }
        return b.toString();
    }

    /**
     * Format an array to a separated string
     *
     * @param array     Array that needs to be formatted
     * @param separator Array separator
     * @return Formatted string
     */
    public static String formatStatusValue(int[] array, char separator) {
        if (Optional.ofNullable(array).isEmpty()) {
            return "(!)";
        }
        int iMax = array.length - 1;
        if (iMax == -1) {
            return "";
        }

        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(String.format("%3d", array[i]));
            if (i == iMax) {
                return b.toString();
            }
            b.append(" &b").append(separator).append("&d ");
        }
    }

    /**
     * Format a move set to a separated string
     *
     * @param moveset   Move set that needs to be formatted
     * @param separator Array separator
     * @return Formatted move names
     */
    public static String formatMoves(Moveset moveset, char separator) {
        if (Optional.ofNullable(moveset).isEmpty()) {
            return "(!)";
        }
        int iMax = moveset.size() - 1;
        if (iMax == -1) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(moveset.attacks[i].getActualMove().getTranslatedName());
            if (i == iMax) {
                return b.toString();
            }
            b.append(" &b").append(separator).append("&d ");
        }
    }
}
