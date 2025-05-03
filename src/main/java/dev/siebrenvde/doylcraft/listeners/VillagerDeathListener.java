package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
import dev.siebrenvde.doylcraft.utils.Components;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.Optionull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;

/**
 * Listener for {@link EntityDeathEvent}
 * <p>
 * Broadcasts a message when a villager dies
 */
@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class VillagerDeathListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onVillagerDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) return;

        TranslatableComponent originalDeathMessage = (TranslatableComponent) villager.getCombatTracker().getDeathMessage();
        List<TranslationArgument> arguments = new ArrayList<>(originalDeathMessage.arguments());

        // Add the villager's profession to their name
        arguments.set(0, TranslationArgument.component(Components.entity(
            Optionull.mapOrDefault(
                villager.customName(),
                name -> name.append(text(" the ")).append(translatable(villager.getProfession())),
                translatable(villager.getProfession())
            ),
            villager
        )));

        Component location = Components.location(villager.getLocation());

        TranslatableComponent.Builder builder = originalDeathMessage.toBuilder();
        builder.arguments(arguments);
        builder.color(NamedTextColor.GREEN);
        builder.hoverEvent(HoverEvent.showText(location));
        TranslatableComponent deathMessage = builder.build();

        Bukkit.broadcast(deathMessage);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(plainText().serialize(deathMessage));
        embed.setDescription("A villager died at " + plainText().serialize(location));
        DiscordSRVAddon.get().sendDiscordEmbed("global", embed);

    }

}
