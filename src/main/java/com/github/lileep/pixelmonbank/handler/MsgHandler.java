package com.github.lileep.pixelmonbank.handler;

import com.envyful.api.forge.chat.UtilChatColour;
import com.github.lileep.pixelmonbank.config.PixelmonBankLocaleConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;

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

    public static TextComponentString prefixedColorMsg(String msg, Object... args) {
        return prefixedColorMsg(String.format(msg, args));
    }
}
