package dev.siebrenvde.doylcraft.events;

import dev.siebrenvde.doylcraft.utils.Components;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class VillagerDeathEvent implements Listener {

    @EventHandler
    private void onVillagerDeath(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Villager villager)) return;

        DamageSource source = event.getDamageSource();
        String translationKey = "death.attack." + source.getDamageType().getTranslationKey();
        List<TranslationArgument> arguments = new ArrayList<>();

        arguments.add(TranslationArgument.component(Components.entityComponent(
            villager.customName() != null
                ? villager.customName().append(Component.text(" the ")).append(Component.translatable(villager.getProfession()))
                : Component.translatable(villager.getProfession()),
            villager
        )));

        if(source.getCausingEntity() == null && source.getDirectEntity() == null) {
            Player killer = event.getEntity().getKiller();
            if(killer != null) {
                arguments.add(TranslationArgument.component(Components.entityComponent(killer)));
                translationKey += ".player";
            }
        } else {
            Entity causingEntity = source.getCausingEntity();

            if(causingEntity != null) {
                arguments.add(TranslationArgument.component(Components.entityComponent(causingEntity)));

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
                arguments.add(TranslationArgument.component(Components.entityComponent(source.getDirectEntity())));
            }
        }

        TranslatableComponent.Builder builder = Component.translatable();
        builder.key(translationKey).arguments(arguments);
        builder.colorIfAbsent(NamedTextColor.GREEN);
        Location loc = event.getEntity().getLocation();
        builder.hoverEvent(HoverEvent.showText(Component.text(String.format(
            "X: %.2f, Y: %.2f, Z: %.2f",
            loc.x(),
            loc.y(),
            loc.z()
        ))));

        Bukkit.broadcast(builder.build());
    }

}
