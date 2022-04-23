package com.github.lileep.pixelmonbank.handler;

import com.envyful.api.forge.chat.UtilChatColour;
import com.github.lileep.pixelmonbank.config.PixelmonBankLocaleConfig;
import net.minecraft.util.text.TextComponentString;

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
     *
     * Add prefix to the given msg,
     * translate color char to real color,
     * process the args
     *
     * @param msg Message to process
     * @param args arguments
     * @return processed message
     */
    public static TextComponentString prefixedColorMsg(String msg, Object... args) {
        return prefixedColorMsg(String.format(msg, args));
    }
}
