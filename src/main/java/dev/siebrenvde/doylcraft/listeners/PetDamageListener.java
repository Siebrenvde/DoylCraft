package dev.siebrenvde.doylcraft.listeners;

import dev.siebrenvde.doylcraft.addons.DiscordSRVAddon;
import dev.siebrenvde.doylcraft.preferences.Preferences;
import dev.siebrenvde.doylcraft.utils.Components;
import github.scarsz.discordsrv.dependencies.jda.api.utils.MarkdownSanitizer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.projectiles.ProjectileSource;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

/**
 * Listener for {@link EntityDamageByEntityEvent}
 * <p>
 * Sends a message to a pet's owner when it is damaged
 * <br>
 * Broadcasts a message to all online players if a pet is killed
 * <br>
 * Logs all damage and deaths to a Discord channel
 */
public class PetDamageListener implements Listener {

    private final DiscordSRVAddon discordSRVAddon;
    private final DecimalFormat df;

    private static final TextColor MESSAGE_COLOUR = NamedTextColor.RED;

    public PetDamageListener(DiscordSRVAddon discordSRVAddon) {
        this.discordSRVAddon = discordSRVAddon;
        df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if(event.getFinalDamage() == 0) return;
        if(!(event.getEntity() instanceof Tameable pet)) return;
        if(!pet.isTamed() || pet.getOwner() == null) return;
        if(pet instanceof Wolf && pet.getEquipment().getItem(EquipmentSlot.BODY).getType() == Material.WOLF_ARMOR) return;

        Entity damagerE = event.getDamager();

        if(damagerE instanceof Projectile projectile) {
            ProjectileSource source = projectile.getShooter();
            if(source instanceof Player damager) {
                damagerE = damager;
            }
        }

        if(!(damagerE instanceof Player damager)) return;

        OfflinePlayer owner = (OfflinePlayer) pet.getOwner();
        double damage = event.getFinalDamage();
        double health = pet.getHealth();
        TranslatableComponent petType = Component.translatable(pet.getType());
        Component petName = pet.customName();

        if(damager.equals(owner)) {
            if(Preferences.get(damager).petDamageMessages.attacker()) {
                damager.sendMessage(
                    text()
                        .append(text("You did " + df.format(damage) + " damage to "))
                        .append(petName == null ? text("your ") : empty())
                        .append(Components.entity(pet))
                        .color(MESSAGE_COLOUR)
                );
            }
        } else {
            if(owner.isOnline()) {
                Player ownerPlayer = (Player) owner;
                if(Preferences.get(ownerPlayer).petDamageMessages.owner()) {
                    ownerPlayer.sendMessage(
                        text()
                            .append(Components.entity(damager))
                            .append(text(" did " + df.format(damage) + " damage to "))
                            .append(petName == null ? text("your ") : empty())
                            .append(Components.entity(pet))
                            .color(MESSAGE_COLOUR)
                    );
                }
            }

            if(Preferences.get(damager).petDamageMessages.attacker()) {
                damager.sendMessage(
                    text()
                        .append(text("You did " + df.format(damage) + " damage to "))
                        .append(Components.entity(owner))
                        .append(text("'s "))
                        .append(petName != null
                            ? petType.append(text(", "))
                            : empty()
                        )
                        .append(Components.entity(pet))
                        .color(MESSAGE_COLOUR)
                );
            }
        }

        discordSRVAddon.sendDiscordMessage("pet-log",
            text()
                .append(text(":heart: "))
                .append(text(MarkdownSanitizer.escape(damager.getName(), true)))
                .append(text(" did " + df.format(damage) + " damage to "))
                .append(petName != null ? petName : petType)
                .append(text(" ("))
                .append(petName != null ? petType.append(text(", ")) : empty())
                .append(text(MarkdownSanitizer.escape(owner.getName() != null ? owner.getName() : "Unknown Player", true)))
                .build()
        );

        if((health - damage) <= 0.0) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if(!Preferences.get(player).petDamageMessages.broadcast()) return;
                player.sendMessage(
                    text()
                        .append(Components.entity(damager))
                        .append(text(" killed "))
                        .append(damager != owner
                            ? Components.entity(owner).append(text("'s "))
                            : text("their ")
                        )
                        .append(petName != null
                            ? petType.append(text(", "))
                            : empty()
                        )
                        .append(Components.entity(pet))
                        .color(MESSAGE_COLOUR)
                );
            });

            discordSRVAddon.sendDiscordMessage("pet-log",
                text()
                    .append(text(":skull: "))
                    .append(text(MarkdownSanitizer.escape(damager.getName(), true)))
                    .append(text(" killed "))
                    .append(petName != null ? petName : petType)
                    .append(text(" ("))
                    .append(petName != null ? petType.append(text(", ")) : empty())
                    .append(text(MarkdownSanitizer.escape(owner.getName() != null ? owner.getName() : "Unknown Player", true)))
                    .append(text(")"))
                    .build()
            );
        }

    }

}
