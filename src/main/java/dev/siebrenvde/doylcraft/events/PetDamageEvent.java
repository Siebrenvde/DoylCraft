package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.handlers.DiscordHandler;
import dev.siebrenvde.doylcraft.utils.Utils;
import dev.siebrenvde.tameableaxolotls.handlers.DataHandler;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.UUID;

public class PetDamageEvent implements Listener {

    private Main main;
    private DiscordHandler dUtils;
    private DataHandler aHandler;

    public PetDamageEvent(Main m) {
        main = m;
        dUtils = m.getDiscordHandler();
        aHandler = new DataHandler();
    }

    @EventHandler
    public boolean onEntityDamage(EntityDamageByEntityEvent event) {

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);

        Entity damagerE = event.getDamager();

        if(damagerE instanceof Projectile) {
            ProjectileSource source = ((Projectile) damagerE).getShooter();
            if(source instanceof Player) {
                damagerE = ((Player) source);
            }
        }

        if(damagerE instanceof Player) {

            Player damager = (Player) damagerE;
            Entity e = event.getEntity();

            Player owner;
            double damage = event.getDamage();
            double health;
            String petName = e.getCustomName();
            String type = "";
            if(e instanceof Axolotl) { type = "Axolotl"; }
            if(e instanceof Cat) { type = "Cat"; }
            if(e instanceof Wolf) { type = "Wolf"; }
            if(e instanceof Parrot) { type = "Parrot"; }
            if(petName == null) { petName = type; }

            if(e instanceof Axolotl) {
                owner = main.getServer().getPlayer(UUID.fromString(aHandler.getOwner(e)));
                health = ((Axolotl) e).getHealth();
            }
            else if(e instanceof Cat || e instanceof Wolf || e instanceof Parrot) {
                Tameable pet = (Tameable) e;
                owner = (Player) pet.getOwner();
                health = pet.getHealth();
            }
            else {
                return false;
            }

            if(owner.isOnline()) {
                owner.sendMessage(ChatColor.RED + damager.getName() + " did " + df.format(damage) + " damage to " + petName + ".");
            }

            dUtils.sendDiscordMessage("pet-log", "❤ " + damager.getName() + " did " + df.format(damage) + " damage to " + petName + " (" + owner.getName() + ").");

            if((health - damage) <= 0.0) {

                Utils.broadcastMessage(ChatColor.RED + damager.getName() + " just MURDERED " + owner.getName() + "'s " + type + "!");
                dUtils.sendDiscordMessage("pet-log", "\uD83D\uDC80 " + damager.getName() + " killed " + petName + " (" + owner.getName() + ").");

                damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 600, 1, false, false, false));
                damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 600, 5, false, false ,false));
                damager.playSound(damager.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
                damager.sendTitle(ChatColor.DARK_RED + "MURDERER!", ChatColor.RED + "How dare you murder an innocent " + type + ".", 0, 600, 10);

            }

            return true;
        }

        return false;
    }

}
