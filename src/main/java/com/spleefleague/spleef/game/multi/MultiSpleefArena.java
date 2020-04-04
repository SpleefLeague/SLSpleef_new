/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.multi;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.menu.InventoryMenu;
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
    
    public static InventoryMenu createMenu() {
        String mainColor = ChatColor.RED + "" + ChatColor.BOLD;
        InventoryMenu spleefMenu = InventoryMenu.createMenu()
                .setTitle("Multispleef Menu")
                .setName(mainColor + "Multispleef")
                .setDescription("Fight your dominance in this free-for-all edition of Spleef.")
                .setDisplayItem(Material.SHEARS, 238);
        
        InventoryMenu spleefMapMenu = InventoryMenu.createMenu()
                .setTitle("Map Select: Multispleef")
                .setName("Map Select: Multispleef")
                .setDisplayItem(new ItemStack(Material.FILLED_MAP));
        
        spleefMapMenu.addMenuItem(InventoryMenu.createItem()
                .setName("Random Arena")
                .setDisplayItem(new ItemStack(Material.EMERALD))
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.MULTI.getArenaMode(), Spleef.getInstance().getPlayers().get(cp))));
        
        getArenas(SpleefMode.MULTI.getArenaMode()).forEach((String s, Arena arena) -> spleefMapMenu.addMenuItem(InventoryMenu.createItem()
                .setName(arena.getDisplayName())
                .setDescription(cp -> arena.getDescription())
                .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.MULTI.getArenaMode(), Spleef.getInstance().getPlayers().get(cp), arena))));
        
        spleefMenu.addMenuItem(spleefMapMenu, 0);
        
        return spleefMenu;
    }
    
}
