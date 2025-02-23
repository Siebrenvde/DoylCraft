package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
import dev.siebrenvde.doylcraft.utils.Components;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

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
        if(!(event.getEntity() instanceof Villager villager)) return;

        DamageSource source = event.getDamageSource();
        String translationKey = "death.attack." + source.getDamageType().getTranslationKey();
        List<TranslationArgument> arguments = new ArrayList<>();

        Component villagerName = villager.customName();
        arguments.add(TranslationArgument.component(Components.entity(
            villagerName != null
                ? villagerName.append(Component.text(" the ")).append(Component.translatable(villager.getProfession()))
                : Component.translatable(villager.getProfession()),
            villager
        )));

        Entity causingEntity = source.getCausingEntity();
        Entity directEntity = source.getDirectEntity();

        if(causingEntity == null && directEntity == null) {
            Player killer = event.getEntity().getKiller();
            if(killer != null) {
                arguments.add(TranslationArgument.component(Components.entity(killer)));
                translationKey += ".player";
            }
        } else {
            if(causingEntity != null) {
                arguments.add(TranslationArgument.component(Components.entity(causingEntity)));

                if(causingEntity instanceof LivingEntity livingEntity && livingEntity.getEquipment() != null) {
                    ItemStack heldItem = livingEntity.getEquipment().getItemInMainHand();
                    if(heldItem.getItemMeta() != null && heldItem.getItemMeta().hasDisplayName()) {
                        arguments.add(TranslationArgument.component(
                            Component.text()
                                .append(heldItem.displayName())
                                .hoverEvent(heldItem.asHoverEvent())
                                .build()
                        ));
                        translationKey += ".item";
                    }
                }
            } else {
                arguments.add(TranslationArgument.component(Components.entity(directEntity)));
            }
        }

        Location loc = event.getEntity().getLocation();
        String formattedLocation = String.format(
            "X: %.2f, Y: %.2f, Z: %.2f",
            loc.x(),
            loc.y(),
            loc.z()
        );

        TranslatableComponent.Builder builder = Component.translatable();
        builder.key(translationKey).arguments(arguments);
        builder.colorIfAbsent(NamedTextColor.GREEN);
        builder.hoverEvent(HoverEvent.showText(Component.text(formattedLocation)));
        TranslatableComponent component = builder.build();

        Bukkit.broadcast(component);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(PlainTextComponentSerializer.plainText().serialize(component));
        embed.setDescription("A villager died at " + formattedLocation);
        DiscordSRVAddon.get().sendDiscordEmbed("global", embed);

    }

}
