/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.splegg.classic;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefArena;
import com.spleefleague.spleef.game.SpleefMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class SpleggArena extends SpleefArena {
    
    public SpleggArena() {
        mode = SpleefMode.SPLEGG.getArenaMode();
    }
    
    public static InventoryMenuItem createMenu() {
        String mainColor = ChatColor.GREEN + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "Classic Splegg")
                .setDescription("This is infact a real gamemode")
                .setDisplayItem(Material.EGG)
                .createLinkedContainer("Splegg Menu");
        
        InventoryMenuItem mapMenuItem = InventoryMenuAPI.createItem()
                .setName("Map Select: Classic Splegg")
                .setDisplayItem(new ItemStack(Material.FILLED_MAP))
                .createLinkedContainer("Map Select: Classic Splegg");
        
        mapMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName("Random Arena")
                .setDisplayItem(new ItemStack(Material.EMERALD))
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.SPLEGG.getArenaMode(), Spleef.getInstance().getPlayers().get(cp))));
        
        getArenas(SpleefMode.SPLEGG.getArenaMode()).forEach((String s, Arena arena) -> mapMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName(arena.getDisplayName())
                .setDescription(cp -> arena.getDescription())
                .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.SPLEGG.getArenaMode(), Spleef.getInstance().getPlayers().get(cp), arena))));
        
        menuItem.getLinkedContainer().addStaticItem(SpleggGun.createMenu(), 4, 4);
        
        menuItem.getLinkedContainer().addMenuItem(mapMenuItem, 0);
        
        return menuItem;
    }
    
}
