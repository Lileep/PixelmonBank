package com.github.lileep.pixelmonbank.gui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.config.PixelmonBankLocaleConfig;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.List;

public class PixelmonBankGui {

    public static void open(EnvyPlayer<EntityPlayerMP> player, List<Pokemon> pokemonList) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftY(0)
                .topLeftX(0)
                .height(6)
                .width(9)
                .build();

        for (Pokemon pokemon : pokemonList) {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                    .itemStack(new ItemBuilder(UtilSprite.getPixelmonSprite(pokemon)).build())
                    .clickHandler((envyPlayer, clickType) -> UtilForgeConcurrency.runSync(()-> {
                        envyPlayer.executeCommands("pixelmonbank get " + pokemon.getUUID().toString());
                        (((EnvyPlayer<EntityPlayerMP>)envyPlayer).getParent()).closeScreen();
                    }))
                    .build());
        }

        GuiFactory.guiBuilder()
                .addPane(pane)
                .height(6)
                .title(UtilChatColour.translateColourCodes('&',PixelmonBankLocaleConfig.title))
                .setPlayerManager(PixelmonBank.instance.getPlayerManager())
                .setCloseConsumer(envyPlayer -> {
                })
                .build().open(player);
    }
}
