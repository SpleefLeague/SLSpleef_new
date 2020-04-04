/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.power;

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
public class PowerSpleefArena extends SpleefArena {
    
    public PowerSpleefArena() {
        mode = SpleefMode.POWER.getArenaMode();
    }
    
    public static InventoryMenu createMenu() {
        String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;
        InventoryMenu menu = InventoryMenu.createMenu()
                .setTitle("Power Spleef Menu")
                .setName(mainColor + "Power Spleef")
                .setDescription("A twist on the original 1v1 Spleef Mode.  Add unique powers to your Spleefing strategy!")
                .setDisplayItem(Material.GOLDEN_SHOVEL, 32);
        
        InventoryMenu mapMenu = InventoryMenu.createMenu()
                .setTitle("Map Select: Power Spleef")
                .setName("Map Select: Power Spleef")
                .setDisplayItem(new ItemStack(Material.FILLED_MAP));
        
        mapMenu.addMenuItem(InventoryMenu.createItem()
                .setName("Random Arena")
                .setDisplayItem(new ItemStack(Material.EMERALD))
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.POWER.getArenaMode(), Spleef.getInstance().getPlayers().get(cp))));
        
        getArenas(SpleefMode.POWER.getArenaMode()).forEach((String s, Arena arena) -> {
            mapMenu.addMenuItem(arena.createMenu((cp -> {
                Spleef.getInstance().queuePlayer(SpleefMode.POWER.getArenaMode(), Spleef.getInstance().getPlayers().get(cp), arena);
            })));
        });
        
        menu.addStaticItem(Power.createMenu(0), 1, 4);
        menu.addStaticItem(Power.createMenu(1), 3, 4);
        menu.addStaticItem(Power.createMenu(2), 5, 4);
        menu.addStaticItem(Power.createMenu(3), 7, 4);
        
        menu.addMenuItem(mapMenu, 0);
        
        return menu;
    }
    
}
