package com.github.lileep.pixelmonbank.gui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.handler.MsgHandler;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.Optional;

public class PixelmonBankGui {

    private static final Displayable.Builder<ItemStack> PREV_BUTTON = GuiFactory.displayableBuilder(ItemStack.class);
    private static final ItemBuilder PREV_BUTTON_ITEM = new ItemBuilder(new ItemStack(PixelmonItems.trade_holder_left));
    private static final Displayable.Builder<ItemStack> NEXT_BUTTON = GuiFactory.displayableBuilder(ItemStack.class);
    private static final ItemBuilder NEXT_BUTTON_ITEM = new ItemBuilder(new ItemStack(PixelmonItems.trade_holder_right));

    private static final Displayable.Builder<ItemStack> INFO_BUTTON = GuiFactory.displayableBuilder(ItemStack.class)
            .clickHandler((envyPlayer, clickType) -> UtilForgeConcurrency.runSync(() -> envyPlayer.executeCommands("pixelmonbank getall")));
    private static final ItemBuilder INFO_BUTTON_ITEM = new ItemBuilder(new ItemStack(Blocks.GOLD_BLOCK));

    public static void open(ForgeEnvyPlayer player, Map<Integer, Pokemon> pokemonMap, int page, int count) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftY(0)
                .topLeftX(0)
                .height(6)
                .width(9)
                .build();

        pokemonMap.forEach((id, pokemon) -> {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                    .itemStack(new ItemBuilder(UtilSprite.getPixelmonSprite(pokemon))
                            .name(UtilChatColour.colour("&b" + pokemon.getTranslatedName() + (pokemon.isShiny() ? "&e★" : "")))
                            .lore(
                                    UtilChatColour.colour("&d" + PixelmonBank.getInstance().getLocale().getPixelmonLevel() + ": " + pokemon.getPokemonLevel() + " &b|&d " + PixelmonBank.getInstance().getLocale().getPixelmonDynamaxLevel() + ": " + pokemon.getDynamaxLevel() + (pokemon.hasGigantamaxFactor() ? (" &b|&d " + PixelmonBank.getInstance().getLocale().getPixelmonCanGigantamax()) : "")),
                                    UtilChatColour.colour("&d" + pokemon.getGender().getTranslatedName() + " &b|&d " + pokemon.getBall().getTranslatedName()),
                                    UtilChatColour.colour((pokemon.hasHiddenAbility() ? "&6" : "&d") + pokemon.getAbility().getTranslatedName() + " &b|&d " + pokemon.getNature().getTranslatedName() + (Optional.ofNullable(pokemon.getMintNature()).isPresent() ? ("&6 -> " + pokemon.getMintNature().getTranslatedName()) : "")),
                                    pokemon.getHeldItem().isEmpty() ? Component.empty() : (UtilChatColour.colour("&d" + PixelmonBank.getInstance().getLocale().getPixelmonHeld() + " " + pokemon.getHeldItem().getDisplayName().getString())),
                                    UtilChatColour.colour("&d" + PixelmonBank.getInstance().getLocale().getPixelmonIv() + ": " + MsgHandler.formatIV(pokemon.getIVs(), '|')),
                                    UtilChatColour.colour("&d" + PixelmonBank.getInstance().getLocale().getPixelmonEv() + ": " + MsgHandler.formatStatusValue(pokemon.getEVs().getArray(), '|')),
                                    UtilChatColour.colour("&d" + MsgHandler.formatMoves(pokemon.getMoveset(), '|')),
                                    Component.empty(),
                                    UtilChatColour.colour(PixelmonBank.getInstance().getLocale().getPbankGuiGet())
                            ).build()
                    ).clickHandler((envyPlayer, clickType) -> UtilForgeConcurrency.runSync(() -> {
                        envyPlayer.executeCommands("pixelmonbank get " + id);
                        player.getParent().closeContainer();})
                    ).build());
        });

        if (page > 1) {
            pane.set(0, 5,
                    PREV_BUTTON.itemStack(PREV_BUTTON_ITEM
                            .name(UtilChatColour.colour(PixelmonBank.getInstance().getLocale().getPbankGuiPrev()))
                            .build()
                    ).clickHandler((envyPlayer, clickType) -> envyPlayer.executeCommands("pixelmonbank see " + (page - 1))
                    ).build());
        }

        if (page < (count / 45 + 1)) {
            pane.set(8, 5,
                    NEXT_BUTTON.itemStack(NEXT_BUTTON_ITEM
                            .name(UtilChatColour.colour(PixelmonBank.getInstance().getLocale().getPbankGuiNext()))
                            .build()
                    ).clickHandler((envyPlayer, clickType) -> envyPlayer.executeCommands("pixelmonbank see " + (page + 1))
                    ).build());
        }

        pane.set(4, 5,
                INFO_BUTTON.itemStack(INFO_BUTTON_ITEM
                        .name(UtilChatColour.colour(String.format(PixelmonBank.getInstance().getLocale().getPbankGuiInfo1(), count)))
                        .lore(
                                UtilChatColour.colour(PixelmonBank.getInstance().getLocale().getPbankGuiInfo2())
                        )
                        .build()
                ).build());

        GuiFactory.guiBuilder()
                .addPane(pane)
                .height(6)
                .title(PixelmonBank.getInstance().getLocale().getTitle())
                .setPlayerManager(PixelmonBank.getInstance().getPlayerManager())
                .build().open(player);
    }
}
