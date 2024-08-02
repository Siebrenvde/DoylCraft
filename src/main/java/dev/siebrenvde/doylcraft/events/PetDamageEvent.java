package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.handlers.DiscordHandler;
import dev.siebrenvde.doylcraft.utils.Components;
import github.scarsz.discordsrv.dependencies.jda.api.utils.MarkdownSanitizer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

public class PetDamageEvent implements Listener {

    private DiscordHandler discordHandler;

    private static final TextColor MESSAGE_COLOUR = NamedTextColor.RED;

    public PetDamageEvent(DiscordHandler discordHandler) {
        this.discordHandler = discordHandler;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);

        if(event.getDamage() == 0) {
            return;
        }

        Entity damagerE = event.getDamager();

        if(damagerE instanceof Projectile projectile) {
            ProjectileSource source = projectile.getShooter();
            if(source instanceof Player damager) {
                damagerE = damager;
            }
        }

        if(damagerE instanceof Player damager) {

            if(event.getEntity() instanceof Tameable pet) {

                if(pet.isTamed()) {

                    OfflinePlayer owner = (OfflinePlayer) pet.getOwner();
                    double damage = event.getDamage();
                    double health = pet.getHealth();
                    TranslatableComponent petType = Component.translatable(pet.getType());
                    Component petName = pet.customName();

                    if(damager.equals(owner)) {
                        damager.sendMessage(
                            text("You did " + df.format(damage) + " damage to ")
                                .append(petName == null ? text("your ") : empty())
                                .append(Components.entityComponent(pet))
                                .append(text("."))
                                .color(MESSAGE_COLOUR)
                        );
                    } else {
                        if(owner.isOnline()) {
                            ((Player) owner).sendMessage(
                                empty()
                                    .append(Components.entityComponent(damager))
                                    .append(text(" did " + df.format(damage) + " damage to "))
                                    .append(petName == null ? text("your ") : empty())
                                    .append(Components.entityComponent(pet))
                                    .append(text("."))
                                    .color(MESSAGE_COLOUR)
                            );
                        }

                        damager.sendMessage(
                            text("You did " + df.format(damage) + " damage to ")
                                .append(Components.entityComponent(owner))
                                .append(text("'s "))
                                .append(Components.entityComponent(petType, pet))
                                .append(text("."))
                                .color(MESSAGE_COLOUR)
                        );
                    }

                    discordHandler.sendDiscordMessage("pet-log",
                        text(":heart: ")
                            .append(text(MarkdownSanitizer.escape(damager.getName(), true)))
                            .append(text(" did " + df.format(damage) + " damage to "))
                            .append(petName != null ? petName : petType)
                            .append(text(" ("))
                            .append(petName != null ? petType.append(text(", ")) : empty())
                            .append(text(MarkdownSanitizer.escape(owner.getName(), true)))
                            .append(text(")."))
                    );

                    if((health - damage) <= 0.0) {
                        Bukkit.broadcast(
                            Component.empty()
                                .append(Components.entityComponent(damager))
                                .append(text(" killed "))
                                .append(Components.entityComponent(owner))
                                .append(text("'s "))
                                .append(Components.entityComponent(petType, pet))
                                .append(text("."))
                                .color(MESSAGE_COLOUR)
                        );

                        discordHandler.sendDiscordMessage("pet-log",
                            text(":skull: ")
                                .append(text(MarkdownSanitizer.escape(damager.getName(), true)))
                                .append(text(" killed "))
                                .append(petName != null ? petName : petType)
                                .append(text(" ("))
                                .append(petName != null ? petType.append(text(", ")) : empty())
                                .append(text(MarkdownSanitizer.escape(owner.getName(), true)))
                                .append(text(")."))
                        );
                    }

                }

            }

        }

    }

}
