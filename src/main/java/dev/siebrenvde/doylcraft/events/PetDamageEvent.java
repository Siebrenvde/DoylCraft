package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.handlers.DiscordHandler;
import dev.siebrenvde.doylcraft.utils.Utils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PetDamageEvent implements Listener {

    private DiscordHandler discordHandler;

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

        if(damagerE instanceof Projectile) {
            ProjectileSource source = ((Projectile) damagerE).getShooter();
            if(source instanceof Player) {
                damagerE = ((Player) source);
            }
        }

        if(damagerE instanceof Player damager) {

            if(event.getEntity() instanceof Tameable pet) {

                if(pet.isTamed()) {

                    OfflinePlayer owner = (OfflinePlayer) pet.getOwner();
                    double damage = event.getDamage();
                    double health = pet.getHealth();
                    String petType = Utils.getTameableName(pet);
                    //String petName = pet.customName() != null ? ((TextComponent) pet.customName()).content() : null;
                    String petName;
                    if(pet.customName() != null) {
                        petName = ((TextComponent) pet.customName()).content();
                        if(petName.isEmpty()) {
                            // Temporary fix
                            petName = ((TextComponent) pet.customName().children().get(0)).content();
                        }
                    } else {
                        petName = null;
                    }

                    if(damager.equals(owner)) {
                        damager.sendMessage(ChatColor.RED + "You did " + df.format(damage) + " damage to your " + petType + (petName != null ? ", " + petName : "") + ".");
                    } else {
                        if(owner.isOnline()) {
                            ((Player) owner).sendMessage(ChatColor.RED + damager.getName() + " did " + df.format(damage) + " damage to your " + petType + (petName != null ? ", " + petName : "") + ".");
                        }
                        damager.sendMessage(ChatColor.RED + "You did " + df.format(damage) + " damage to " + owner.getName() + "'s " + petType + ".");
                    }

                    discordHandler.sendDiscordMessage("pet-log", "❤ " + damager.getName().replaceAll("_", "\\_") + " did " + df.format(damage) + " damage to " + (petName != null ? petName : petType) + " (" + (petName != null ? petType + ", " : "") + owner.getName().replaceAll("_", "\\_") + ").");

                    if((health - damage) <= 0.0) {
                        Utils.broadcastMessage(ChatColor.RED + damager.getName() + " killed " + owner.getName() + "'s " + petType + "!");
                        discordHandler.sendDiscordMessage("pet-log", "\uD83D\uDC80 " + damager.getName().replaceAll("_", "\\_") + " killed " + (petName != null ? petName : petType) + " (" + (petName != null ? petType + ", " : "") + owner.getName().replaceAll("_", "\\_") + ").");
                    }

                }

            }

        }

    }

}
