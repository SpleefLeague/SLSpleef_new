/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menus;

import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class LeaderboardMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            // Options Menus
            menuItem = InventoryMenuAPI.createItem()
                    .setName("Leaderboards")
                    .setDisplayItem(new ItemStack(Material.OAK_SIGN))
                    .setDescription("View the Top Players of SpleefLeague!")
                    .createLinkedContainer("Leaderboards");
        }
        return menuItem;
    }

}
