/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menus;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.Rank;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * @author NickM13
 */
public class ModerativeMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            menuItem = InventoryMenuAPI.createItem()
                    .setName("Moderative Menu")
                    .setDisplayItem(Material.REDSTONE)
                    .setDescription("Useful tools to deal with the general population")
                    .setMinRank(Rank.MODERATOR)
                    .createLinkedContainer("Moderative Menu");
            
            menuItem.getLinkedContainer()
                    .addMenuItem(InventoryMenuAPI.createItem()
                            .setName("Clear Effects")
                            .setDisplayItem(Material.MILK_BUCKET)
                            .setAction(cp -> {
                                    for (PotionEffect pe : cp.getPlayer().getActivePotionEffects()) {
                                        cp.getPlayer().removePotionEffect(pe.getType());
                                    }
                                    }));
            
            menuItem.getLinkedContainer()
                    .addMenuItem(InventoryMenuAPI.createItem()
                            .setName("Night Vision")
                            .setDisplayItem(InventoryMenuAPI.createCustomPotion(PotionType.NIGHT_VISION))
                            .setAction(cp -> cp.getPlayer().addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(Integer.MAX_VALUE, 0))));
        }
        return menuItem;
    }

}
