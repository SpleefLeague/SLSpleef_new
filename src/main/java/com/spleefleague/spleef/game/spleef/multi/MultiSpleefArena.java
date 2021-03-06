/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.multi;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefArena;
import com.spleefleague.spleef.game.SpleefMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class MultiSpleefArena extends SpleefArena {
    
    public MultiSpleefArena() {
        this.mode = SpleefMode.MULTI.getArenaMode();
    }
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.RED + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "Multispleef")
                .setDescription("Fight your dominance in this free-for-all edition of Spleef.")
                .setDisplayItem(Material.SHEARS, 238)
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.MULTI.getArenaMode(), Spleef.getInstance().getPlayers().get(cp)));
        
        Spleef.getInstance().getSpleefMenu().getLinkedContainer().addMenuItem(menuItem, x, y);
    }
    
}
