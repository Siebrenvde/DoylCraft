package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.handlers.DiscordHandler;
import dev.siebrenvde.doylcraft.utils.Utils;
import dev.siebrenvde.tameableaxolotls.handlers.DataHandler;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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

            OfflinePlayer owner;
            double damage = event.getDamage();
            double health;
            String petName = e.getCustomName();
            String type = "";
            if(e instanceof Axolotl) { type = "Axolotl"; }
            if(e instanceof Cat) { type = "Cat"; }
            if(e instanceof Wolf) { type = "Wolf"; }
            if(e instanceof Parrot) { type = "Parrot"; }
            if(e instanceof Horse) { type = "Horse"; }
            if(e instanceof Donkey) { type = "Donkey"; }
            if(e instanceof Mule) { type = "Mule"; }
            if(e instanceof Llama) { type = "Llama"; }
            if(e instanceof SkeletonHorse) { type = "Skeleton Horse"; }
            if(e instanceof ZombieHorse) { type = "Zombie Horse"; }
            if(e instanceof TraderLlama) { type = "Trader Llama"; }
            if(petName == null) { petName = type; }

            if(e instanceof Axolotl) {
                if(aHandler.hasOwner(e)) {
                    owner = main.getServer().getOfflinePlayer(UUID.fromString(aHandler.getOwner(e)));
                } else {
                    return false;
                }
                health = ((Axolotl) e).getHealth();
            }
            else if(e instanceof Cat || e instanceof Wolf || e instanceof Parrot || e instanceof AbstractHorse) {
                Tameable pet = (Tameable) e;
                if(pet.isTamed()) {
                    owner = (OfflinePlayer) pet.getOwner();
                } else {
                    return false;
                }
                health = pet.getHealth();
            }
            else {
                return false;
            }

            if(owner.isOnline()) {
                ((Player) owner).sendMessage(ChatColor.RED + damager.getName() + " did " + df.format(damage) + " damage to " + petName + ".");
            }

            dUtils.sendDiscordMessage("pet-log", "❤ " + damager.getName().replaceAll("_", "\\_") + " did " + df.format(damage) + " damage to " + petName + " (" + owner.getName().replaceAll("_", "\\_") + ").");

            if((health - damage) <= 0.0) {
                Utils.broadcastMessage(ChatColor.RED + damager.getName() + " just MURDERED " + owner.getName() + "'s " + type + "!");
                dUtils.sendDiscordMessage("pet-log", "\uD83D\uDC80 " + damager.getName().replaceAll("_", "\\_") + " killed " + petName + " (" + owner.getName().replaceAll("_", "\\_") + ").");
            }

            return true;
        }

        return false;
    }

}
