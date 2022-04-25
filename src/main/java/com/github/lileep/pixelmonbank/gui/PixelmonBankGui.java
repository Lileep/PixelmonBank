package com.github.lileep.pixelmonbank.gui;

import com.envyful.api.config.type.PositionableConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.config.PixelmonBankLocaleConfig;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class PixelmonBankGui {

    private static final PositionableConfigItem previousPageButton = new PositionableConfigItem(
            "pixelmon:trade_holder_left", 1, (byte) 0, PixelmonBankLocaleConfig.pbankGuiPrev,
            Lists.newArrayList(), 0, 5, Maps.newHashMap()
    );

    private static final PositionableConfigItem nextPageButton = new PositionableConfigItem(
            "pixelmon:trade_holder_right", 1, (byte) 0, PixelmonBankLocaleConfig.pbankGuiNext,
            Lists.newArrayList(), 8, 5, Maps.newHashMap()
    );

    public static void open(EnvyPlayer<EntityPlayerMP> player, List<Pokemon> pokemonList, int page, int count) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftY(0)
                .topLeftX(0)
                .height(6)
                .width(9)
                .build();

        for (Pokemon pokemon : pokemonList) {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                    .itemStack(new ItemBuilder(UtilSprite.getPixelmonSprite(pokemon))
                            .name(UtilChatColour.translateColourCodes('&', "&b" + pokemon.getLocalizedName() + (pokemon.isShiny() ? "&e★" : "")))
                            .addLore(
                                    UtilChatColour.translateColourCodes('&', "&d" + PixelmonBankLocaleConfig.pixelmonLevel + ": " + pokemon.getLevel() + " | " + PixelmonBankLocaleConfig.pixelmonDynamaxLevel + ":" + pokemon.getDynamaxLevel() + " " + (pokemon.hasGigantamaxFactor() ? (" | " + PixelmonBankLocaleConfig.pixelmonCanGigantamax) : "")),
                                    UtilChatColour.translateColourCodes('&', "&d" + pokemon.getGender().getLocalizedName() + " | " + pokemon.getCaughtBall().getLocalizedName()),
                                    UtilChatColour.translateColourCodes('&', (pokemon.getAbilitySlot() == 2 ? "&6" : "&d") + pokemon.getAbility().getLocalizedName() + "&d | " + pokemon.getNature().getLocalizedName() + (Optional.ofNullable(pokemon.getMintNature()).isPresent() ? ("&6 -> " + pokemon.getMintNature().getLocalizedName()) : "")),
                                    pokemon.getHeldItem().isEmpty() ? "" : (UtilChatColour.translateColourCodes('&', "&d" + PixelmonBankLocaleConfig.pixelmonHeld + " " + pokemon.getHeldItem().getDisplayName())),
                                    UtilChatColour.translateColourCodes('&', "&d" + PixelmonBankLocaleConfig.pixelmonIv + ": " + MsgHandler.formatStatusValue(pokemon.getIVs().getArray(), '|')),
                                    UtilChatColour.translateColourCodes('&', "&d" + PixelmonBankLocaleConfig.pixelmonEv + ": " + MsgHandler.formatStatusValue(pokemon.getEVs().getArray(), '|')),
                                    UtilChatColour.translateColourCodes('&', "&d" + MsgHandler.formatMoves(pokemon.getMoveset().attacks, '|'))
                            )
                            .build()
                    ).clickHandler((envyPlayer, clickType) -> UtilForgeConcurrency.runSync(() -> {
                        envyPlayer.executeCommands("pixelmonbank get " + pokemon.getUUID().toString());
                        (((EnvyPlayer<EntityPlayerMP>) envyPlayer).getParent()).closeScreen();
                    }))
                    .build());
        }

        if (page > 1) {
            UtilConfigItem.addConfigItem(pane, previousPageButton, (envyPlayer, clickType) -> {
                envyPlayer.executeCommands("pixelmonbank see " + (page - 1));
            });
        }

        if (page < (count / 45 + 1)) {
            UtilConfigItem.addConfigItem(pane, nextPageButton, (envyPlayer, clickType) -> {
                envyPlayer.executeCommands("pixelmonbank see " + (page + 1));
            });
        }

        pane.set(4, 5, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(new ItemBuilder(new ItemStack(Blocks.GOLD_BLOCK))
                        .name(UtilChatColour.translateColourCodes('&', String.format(PixelmonBankLocaleConfig.pbankGuiInfo1, count)))
                        .addLore(
                                UtilChatColour.translateColourCodes('&', PixelmonBankLocaleConfig.pbankGuiInfo2)
                        )
                        .build()
                ).clickHandler((envyPlayer, clickType) -> UtilForgeConcurrency.runSync(() -> envyPlayer.executeCommands("pixelmonbank getall")))
                .build());

        GuiFactory.guiBuilder()
                .addPane(pane)
                .height(6)
                .title(UtilChatColour.translateColourCodes('&', PixelmonBankLocaleConfig.title))
                .setPlayerManager(PixelmonBank.instance.getPlayerManager())
                .setCloseConsumer(envyPlayer -> {
                })
                .build().open(player);
    }
}
