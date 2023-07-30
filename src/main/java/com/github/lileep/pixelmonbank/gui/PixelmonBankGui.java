package com.github.lileep.pixelmonbank.gui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.config.PixelmonBankLocaleConfig;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PixelmonBankGui {

    private static final Displayable.Builder<ItemStack> PREV_BUTTON = GuiFactory.displayableBuilder(ItemStack.class);
    private static final ItemBuilder PREV_BUTTON_ITEM = new ItemBuilder(new ItemStack(PixelmonItems.LtradeHolderLeft));
    private static final Displayable.Builder<ItemStack> NEXT_BUTTON = GuiFactory.displayableBuilder(ItemStack.class);
    private static final ItemBuilder NEXT_BUTTON_ITEM = new ItemBuilder(new ItemStack(PixelmonItems.tradeHolderRight));

    private static final Displayable.Builder<ItemStack> INFO_BUTTON = GuiFactory.displayableBuilder(ItemStack.class)
            .clickHandler((envyPlayer, clickType) -> UtilForgeConcurrency.runSync(() -> envyPlayer.executeCommands("pixelmonbank getall")));
    private static final ItemBuilder INFO_BUTTON_ITEM = new ItemBuilder(new ItemStack(Blocks.GOLD_BLOCK));

    public static void open(EnvyPlayer<EntityPlayerMP> player, Map<Integer, Pokemon> pokemonMap, int page, int count) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftY(0)
                .topLeftX(0)
                .height(6)
                .width(9)
                .build();

        pokemonMap.forEach((id, pokemon) -> {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                    .itemStack(new ItemBuilder(UtilSprite.getPixelmonSprite(pokemon))
                            .name(UtilChatColour.translateColourCodes('&', "&b" + pokemon.getLocalizedName() + (pokemon.isShiny() ? "&e★" : "")))
                            .lore(
                                    UtilChatColour.translateColourCodes('&', "&d" + PixelmonBankLocaleConfig.pixelmonLevel + ": " + pokemon.getLevel() + " &b|&d " + PixelmonBankLocaleConfig.pixelmonDynamaxLevel + ": " + pokemon.getDynamaxLevel() + (pokemon.hasGigantamaxFactor() ? (" &b|&d " + PixelmonBankLocaleConfig.pixelmonCanGigantamax) : "")),
                                    UtilChatColour.translateColourCodes('&', "&d" + pokemon.getGender().getLocalizedName() + " &b|&d " + pokemon.getCaughtBall().getLocalizedName()),
                                    UtilChatColour.translateColourCodes('&', (pokemon.getAbilitySlot() == 2 ? "&6" : "&d") + pokemon.getAbility().getLocalizedName() + " &b|&d " + pokemon.getNature().getLocalizedName() + (Optional.ofNullable(pokemon.getMintNature()).isPresent() ? ("&6 -> " + pokemon.getMintNature().getLocalizedName()) : "")),
                                    pokemon.getHeldItem().isEmpty() ? "" : (UtilChatColour.translateColourCodes('&', "&d" + PixelmonBankLocaleConfig.pixelmonHeld + " " + pokemon.getHeldItem().getDisplayName())),
                                    UtilChatColour.translateColourCodes('&', "&d" + PixelmonBankLocaleConfig.pixelmonIv + ": " + MsgHandler.formatIV(pokemon.getIVs(), '|')),
                                    UtilChatColour.translateColourCodes('&', "&d" + PixelmonBankLocaleConfig.pixelmonEv + ": " + MsgHandler.formatStatusValue(pokemon.getEVs().getArray(), '|')),
                                    UtilChatColour.translateColourCodes('&', "&d" + MsgHandler.formatMoves(pokemon.getMoveset(), '|')),
                                    "",
                                    UtilChatColour.translateColourCodes('&', PixelmonBankLocaleConfig.pbankGuiGet)
                            ).build()
                    ).clickHandler((envyPlayer, clickType) -> UtilForgeConcurrency.runSync(() -> {
                                envyPlayer.executeCommands("pixelmonbank get " + pokemon.getUUID().toString());
                                (((EnvyPlayer<EntityPlayerMP>) envyPlayer).getParent()).closeScreen();
                            })
                    ).build());
        });

        if (page > 1) {
            pane.set(0, 5,
                    PREV_BUTTON.itemStack(PREV_BUTTON_ITEM
                            .name(UtilChatColour.translateColourCodes('&', PixelmonBankLocaleConfig.pbankGuiPrev))
                            .build()
                    ).clickHandler((envyPlayer, clickType) -> envyPlayer.executeCommands("pixelmonbank see " + (page - 1))
                    ).build());
        }

        if (page < (count / 45 + 1)) {
            pane.set(8, 5,
                    NEXT_BUTTON.itemStack(NEXT_BUTTON_ITEM
                            .name(UtilChatColour.translateColourCodes('&', PixelmonBankLocaleConfig.pbankGuiNext))
                            .build()
                    ).clickHandler((envyPlayer, clickType) -> envyPlayer.executeCommands("pixelmonbank see " + (page + 1))
                    ).build());
        }

        pane.set(4, 5,
                INFO_BUTTON.itemStack(INFO_BUTTON_ITEM
                        .name(UtilChatColour.translateColourCodes('&', String.format(PixelmonBankLocaleConfig.pbankGuiInfo1, count)))
                        .lore(
                                UtilChatColour.translateColourCodes('&', PixelmonBankLocaleConfig.pbankGuiInfo2)
                        )
                        .build()
                ).build());

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
