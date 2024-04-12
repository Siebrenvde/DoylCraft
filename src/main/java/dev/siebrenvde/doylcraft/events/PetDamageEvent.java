package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.handlers.DiscordHandler;
import dev.siebrenvde.doylcraft.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
                    String petTypeLegacy = Utils.getTameableName(pet);
                    Component petName = pet.customName();
                    //String petNameContent = petName != null ? ((TextComponent) petName).content() : null;
                    String petNameContent;
                    if(petName != null) {
                        petNameContent = ((TextComponent) petName).content();
                        if(petNameContent.isEmpty()) {
                            // Temporary fix
                            petNameContent = ((TextComponent) petName.children().get(0)).content();
                        }
                    } else {
                        petNameContent = null;
                    }

                    if(damager.equals(owner)) {
                        TextComponent tc = Component.text("You did " + df.format(damage) + " damage to your ", MESSAGE_COLOUR);
                        tc = tc.append(petType.color(MESSAGE_COLOUR));
                        if(petName != null) {
                            tc = tc.append(Component.text(", ", MESSAGE_COLOUR));
                            tc = tc.append(petName.color(MESSAGE_COLOUR));
                        }
                        tc = tc.append(Component.text(".", MESSAGE_COLOUR));
                        damager.sendMessage(tc);
                    } else {
                        if(owner.isOnline()) {
                            TextComponent tc = Component.empty();
                            tc = tc.append(Utils.entityComponent(Component.text(damager.getName(), MESSAGE_COLOUR), damager));
                            tc = tc.append(Component.text(" did " + df.format(damage) + " damage to your ", MESSAGE_COLOUR));
                            tc = tc.append(petType.color(MESSAGE_COLOUR));
                            if(petName != null) {
                                tc = tc.append(Component.text(", ", MESSAGE_COLOUR));
                                tc = tc.append(petName.color(MESSAGE_COLOUR));
                            }
                            tc = tc.append(Component.text(".", MESSAGE_COLOUR));
                            ((Player) owner).sendMessage(tc);
                        }
                        TextComponent tc = Component.text("You did " + df.format(damage) + " damage to ", MESSAGE_COLOUR);
                        tc = tc.append(Utils.entityComponent(Component.text(owner.getName(), MESSAGE_COLOUR), owner));
                        tc = tc.append(Component.text("'s ", MESSAGE_COLOUR));
                        tc = tc.append(petType.color(MESSAGE_COLOUR));
                        tc = tc.append(Component.text(".", MESSAGE_COLOUR));
                        damager.sendMessage(tc);
                    }

                    discordHandler.sendDiscordMessage("pet-log", "❤ " + damager.getName().replaceAll("_", "\\_") + " did " + df.format(damage) + " damage to " + (petNameContent != null ? petNameContent : petTypeLegacy) + " (" + (petNameContent != null ? petTypeLegacy + ", " : "") + owner.getName().replaceAll("_", "\\_") + ").");

                    if((health - damage) <= 0.0) {
                        TextComponent tc = Component.empty();
                        tc = tc.append(Utils.entityComponent(Component.text(damager.getName(), MESSAGE_COLOUR), damager));
                        tc = tc.append(Component.text(" killed ", MESSAGE_COLOUR));
                        tc = tc.append(Utils.entityComponent(Component.text(owner.getName(), MESSAGE_COLOUR), owner));
                        tc = tc.append(Component.text("'s ", MESSAGE_COLOUR));
                        tc = tc.append(petType.color(MESSAGE_COLOUR));
                        tc = tc.append(Component.text(".", MESSAGE_COLOUR));

                        Utils.broadcastMessage(tc);

                        discordHandler.sendDiscordMessage("pet-log", "\uD83D\uDC80 " + damager.getName().replaceAll("_", "\\_") + " killed " + (petNameContent != null ? petNameContent : petTypeLegacy) + " (" + (petNameContent != null ? petTypeLegacy + ", " : "") + owner.getName().replaceAll("_", "\\_") + ").");
                    }

                }

            }

        }

    }

}
