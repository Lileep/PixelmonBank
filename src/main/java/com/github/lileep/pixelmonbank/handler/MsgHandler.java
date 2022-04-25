package com.github.lileep.pixelmonbank.handler;

import com.envyful.api.forge.chat.UtilChatColour;
import com.github.lileep.pixelmonbank.config.PixelmonBankLocaleConfig;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import net.minecraft.util.text.TextComponentString;

import java.util.Optional;

public class MsgHandler {

    /**
     * Add prefix to the given msg,
     * translate color char to real color
     *
     * @param msg Message to process
     * @return processed message
     */
    public static TextComponentString prefixedColorMsg(String msg) {
        return new TextComponentString(UtilChatColour.translateColourCodes('&', PixelmonBankLocaleConfig.prefix + msg));
    }

    /**
     * Add prefix to the given msg,
     * translate color char to real color,
     * process the args
     *
     * @param msg  Message to process
     * @param args arguments
     * @return processed message
     */
    public static TextComponentString prefixedColorMsg(String msg, Object... args) {
        return prefixedColorMsg(String.format(msg, args));
    }

    /**
     * Format an array to a separated string
     *
     * @param array     Array that needs to be formatted
     * @param separator Array separator
     * @return Formatted string
     */
    public static String formatStatusValue(int[] array, char separator) {
        if (!Optional.ofNullable(array).isPresent()) {
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
            b.append(' ').append(separator).append(' ');
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
        if (!Optional.ofNullable(moveset).isPresent()) {
            return "(!)";
        }
        int iMax = moveset.size() - 1;
        if (iMax == -1) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(moveset.attacks[i].getActualMove().getLocalizedName());
            if (i == iMax) {
                return b.toString();
            }
            b.append(' ').append(separator).append(' ');
        }
    }
}
