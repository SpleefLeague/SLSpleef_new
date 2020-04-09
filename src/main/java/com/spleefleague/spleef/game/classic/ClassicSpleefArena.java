/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.classic;

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
public class ClassicSpleefArena extends SpleefArena {
    
    public ClassicSpleefArena() {
        mode = SpleefMode.CLASSIC.getArenaMode();
    }
    
    public static InventoryMenu createMenu() {
        String mainColor = ChatColor.GREEN + "" + ChatColor.BOLD;
        InventoryMenu spleefMenu = InventoryMenu.createMenu()
                .setTitle("Classic Spleef Menu")
                .setName(mainColor + "Classic Spleef")
                .setDescription("The classic version in which you duel a single opponent with a basic diamond shovel.")
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1561);
        
        InventoryMenu spleefMapMenu = InventoryMenu.createMenu()
                .setTitle("Map Select: Classic Spleef")
                .setName("Map Select: Classic Spleef")
                .setDisplayItem(new ItemStack(Material.FILLED_MAP));
        
        spleefMapMenu.addMenuItem(InventoryMenu.createItem()
                .setName("Random Arena")
                .setDisplayItem(new ItemStack(Material.EMERALD))
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.CLASSIC.getArenaMode(), Spleef.getInstance().getPlayers().get(cp))));
        
        getArenas(SpleefMode.CLASSIC.getArenaMode()).forEach((String s, Arena arena) -> spleefMapMenu.addMenuItem(InventoryMenu.createItem()
                .setName(arena.getDisplayName())
                .setDescription(cp -> arena.getDescription())
                .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.CLASSIC.getArenaMode(), Spleef.getInstance().getPlayers().get(cp), arena))));
        
        spleefMenu.addMenuItem(spleefMapMenu, 0);
        
        return spleefMenu;
    }
    
}
