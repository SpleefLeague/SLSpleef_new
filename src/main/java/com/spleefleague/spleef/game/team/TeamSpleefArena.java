/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.team;

import com.spleefleague.core.Core;
import com.spleefleague.core.annotation.DBField;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.party.Party;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefArena;
import com.spleefleague.spleef.game.SpleefMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class TeamSpleefArena extends SpleefArena {
    
    @DBField
    protected Integer teamSize;
    @DBField
    protected Integer teamCount;
    
    public TeamSpleefArena() {
        this.mode = SpleefMode.TEAM.getArenaMode();
    }
    
    public static InventoryMenuItem createMenu() {
        String mainColor = ChatColor.YELLOW + "" + ChatColor.BOLD;
        InventoryMenuItem spleefMenu = InventoryMenuAPI.createItem()
                .createLinkedContainer("Team Spleef Menu")
                .setName(mainColor + "Team Spleef")
                .setDescription("United with a team of the same color, conquer your foes with your allies in this multiplayer gamemode.")
                .setAvailability(cp -> {
                    Party party = cp.getParty();
                    if (party == null) {
                        Core.sendMessageToPlayer(cp, "You have to be in a party for TeamSpleef!");
                        return false;
                    }
                    return true;
                })
                .setDisplayItem(Material.LEATHER_HELMET, 56);
        
        InventoryMenuItem spleefMapMenu = InventoryMenuAPI.createItem()
                .setName("Map Select: Team Spleef")
                .setDisplayItem(new ItemStack(Material.FILLED_MAP))
                .createLinkedContainer("Map Select: Team Spleef");
        
        spleefMapMenu.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName("Random Arena")
                .setDisplayItem(new ItemStack(Material.EMERALD))
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.TEAM.getArenaMode(), Spleef.getInstance().getPlayers().get(cp))));
        
        getArenas(SpleefMode.TEAM.getArenaMode()).forEach((String s, Arena arena) -> spleefMapMenu.getLinkedContainer()
                .addMenuItem(InventoryMenuAPI.createItem()
                        .setName(arena.getDisplayName())
                        .setDescription(cp -> arena.getDescription())
                        .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                        .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.TEAM.getArenaMode(), Spleef.getInstance().getPlayers().get(cp), arena))));
        
        spleefMenu.getLinkedContainer().addMenuItem(spleefMapMenu, 0);
        
        return spleefMenu;
    }
    
}
