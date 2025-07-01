package dev.siebrenvde.doylcraft.commands;

import com.mojang.brigadier.Command;
import dev.siebrenvde.doylcraft.utils.CommandBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.*;

/**
 * Command to get the biomes a player hasn't yet explored for the
 * Adventuring Time and Hot Tourist Destinations advancements
 */
@NullMarked
public class RemainingBiomesCommand extends CommandBase {

    /**
     * The <a href="https://minecraft.wiki/w/Advancement#Adventuring_Time">Adventuring Time</a> advancement
     */
    private static final Advancement ADVENTURING_TIME = Objects.requireNonNull(
        Bukkit.getAdvancement(NamespacedKey.minecraft("adventure/adventuring_time"))
    );

    /**
     * The <a href="https://minecraft.wiki/w/Advancement#Hot_Tourist_Destinations">Hot Tourist Destinations</a> advancement
     */
    private static final Advancement HOT_TOURIST_DESTINATIONS = Objects.requireNonNull(
        Bukkit.getAdvancement(NamespacedKey.minecraft("nether/explore_nether"))
    );

    /**
     * The amout of biomes to show per page
     * <p>
     * While books support up to 14 lines per page,
     * I've limited this to 10 to prevent truncation due to long biome names
     */
    private static final int BIOMES_PER_PAGE = 10;

    public static void register(Commands commands) {
        commands.register(
            literal("remaining_biomes")
                .requires(isPlayer())
                .then(literal("overworld")
                    .executes(execute(ADVENTURING_TIME))
                )
                .then(literal("nether")
                    .executes(execute(HOT_TOURIST_DESTINATIONS))
                )
                .build()
        );
    }

    private static Command<CommandSourceStack> execute(Advancement advancement) {
        return withPlayer((ctx, player) -> {
            List<TranslatableComponent> biomes = player.getAdvancementProgress(advancement).getRemainingCriteria().stream()
                .map(key -> {
                    NamespacedKey biomeKey = Objects.requireNonNull(NamespacedKey.fromString(key));
                    return translatable("biome.minecraft." + biomeKey.getKey())
                        .hoverEvent(HoverEvent.showText(text(biomeKey.toString())));
                })
                .toList();

            if(biomes.isEmpty()) {
                player.sendMessage(text("You have explored all biomes!", NamedTextColor.GREEN));
                return;
            }

            Book.Builder book = Book.builder();

            for(int i = 0; i < biomes.size(); i += BIOMES_PER_PAGE) {
                book.addPage((join(
                    JoinConfiguration.newlines(),
                    biomes.subList(i, Math.min(biomes.size(), i + BIOMES_PER_PAGE))
                )));
            }

            player.openBook(book);
        });
    }

}
