/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.multi;

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
public class MultiSpleefArena extends SpleefArena {
    
    public MultiSpleefArena() {
        this.mode = SpleefMode.MULTI.getArenaMode();
    }
    
    public static InventoryMenuItem createMenu() {
        String mainColor = ChatColor.RED + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "Multispleef")
                .setDescription("Fight your dominance in this free-for-all edition of Spleef.")
                .setDisplayItem(Material.SHEARS, 238)
                .createLinkedContainer("Multispleef Menu");
        
        InventoryMenuItem mapMenuItem = InventoryMenuAPI.createItem()
                .setName("Map Select: Multispleef")
                .setDisplayItem(new ItemStack(Material.FILLED_MAP))
                .createLinkedContainer("Map Select: Multispleef");
        
        mapMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName("Random Arena")
                .setDisplayItem(new ItemStack(Material.EMERALD))
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.MULTI.getArenaMode(), Spleef.getInstance().getPlayers().get(cp))));
        
        getArenas(SpleefMode.MULTI.getArenaMode()).forEach((String s, Arena arena) -> mapMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName(arena.getDisplayName())
                .setDescription(cp -> arena.getDescription())
                .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.MULTI.getArenaMode(), Spleef.getInstance().getPlayers().get(cp), arena))));
        
        menuItem.getLinkedContainer().addMenuItem(mapMenuItem, 0);
        
        return menuItem;
    }
    
}
