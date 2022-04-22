package com.github.lileep.pixelmonbank.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.github.lileep.pixelmonbank.config.PixelmonBankConfig;
import com.github.lileep.pixelmonbank.config.PixelmonBankLocaleConfig;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.github.lileep.pixelmonbank.lib.PermNodeReference;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

@Command(
        value = "reload"
)
@Permissible(PermNodeReference.RELOAD_NODE)
@Child
public class ReloadCmd {

    @CommandProcessor
    public void run(@Sender ICommandSender sender, String[] args) {
        sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.configReloading));
        //load config
        final Configuration cfg = PixelmonBankConfig.config;
        try {
            cfg.load();
        } catch (Exception e) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.configLoadFail));
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
        //pre reload
        PixelmonBankConfig.loadLanguage();


        final Configuration locale;
        //Whether changed language file
        if (!PixelmonBankConfig.LOCALE.equals(PixelmonBankConfig.LOCALE_OLD)) {
            locale = new Configuration(new File(PixelmonBankLocaleConfig.config.getConfigFile().getParent(), PixelmonBankConfig.LOCALE + ".cfg"));
            PixelmonBankConfig.LOCALE_OLD = PixelmonBankConfig.LOCALE;
        } else {
            locale = PixelmonBankLocaleConfig.config;
        }

        //reload language
        try {
            locale.load();
        } catch (Exception e) {
            sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.configLoadFail));
        } finally {
            if (locale.hasChanged()) {
                locale.save();
            }
        }
        PixelmonBankLocaleConfig.loadConfig(locale);


        //post reload
        PixelmonBankConfig.loadConfig();


        sender.sendMessage(MsgHandler.prefixedColorMsg(PixelmonBankLocaleConfig.configLoadSuccess));
    }

}

