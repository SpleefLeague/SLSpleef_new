/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef;

import com.mongodb.client.MongoDatabase;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.menu.InventoryMenu;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.spleef.commands.*;
import com.spleefleague.spleef.game.SpleefArena;
import com.spleefleague.spleef.game.Shovel;
import com.spleefleague.spleef.game.SpleefField;
import com.spleefleague.spleef.player.SpleefPlayer;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.splegg.SpleggGun;
import com.spleefleague.spleef.game.classic.ClassicSpleefArena;
import com.spleefleague.spleef.game.multi.MultiSpleefArena;
import com.spleefleague.spleef.game.power.Power;
import com.spleefleague.spleef.game.power.PowerSpleefArena;
import com.spleefleague.spleef.game.splegg.SpleggArena;
import com.spleefleague.spleef.game.team.TeamSpleefArena;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class Spleef extends CorePlugin<SpleefPlayer> {
    
    private static Spleef instance;
    
    private InventoryMenu spleefMenu;
    
    @Override
    public void init() {
        instance = this;
        
        // Initialize commands
        initCommands();
        
        Shovel.init();
        SpleggGun.init();
        Power.init();
        
        // Initialize player manager
        playerManager = new PlayerManager<>(this, SpleefPlayer.class, getPluginDB().getCollection("Players"));
        
        // Load Spleef gamemodes
        SpleefMode.init();
        addBattleManager(SpleefMode.CLASSIC.getArenaMode());
        addBattleManager(SpleefMode.MULTI.getArenaMode());
        addBattleManager(SpleefMode.SPLEGG.getArenaMode());
        addBattleManager(SpleefMode.POWER.getArenaMode());
        addBattleManager(SpleefMode.TEAM.getArenaMode());
        
        // Load database related static lists
        SpleefField.init();
        SpleefArena.init();
        initMenu();
        
        playerManager.initOnline();
    }
    
    @Override
    public void close() {
        playerManager.close();
    }
    
    public static Spleef getInstance() {
        return instance;
    }
    
    public InventoryMenu getSpleefMenu() {
        return spleefMenu;
    }
    
    public void initMenu() {
        spleefMenu = InventoryMenu.createMenu()
                .setTitle("Spleef Menu")
                .setName("Spleef")
                .setDescription("A competitive gamemode in which you must knock your opponent into the water while avoiding a similar fate.\n\nThis is not with any ordinary weapon; the weapon of choice is a shovel, and you must destroy the blocks underneath your foe!")
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1561);
        
        spleefMenu.addMenuItem(ClassicSpleefArena.createMenu(), 2);
        spleefMenu.addMenuItem(PowerSpleefArena.createMenu(), 3);
        spleefMenu.addMenuItem(TeamSpleefArena.createMenu(), 5);
        spleefMenu.addMenuItem(MultiSpleefArena.createMenu(), 6);
        spleefMenu.addStaticItem(Shovel.createMenu(), 4, 4);
        
        InventoryMenu.getHotbarMenu(InventoryMenu.InvMenuType.SLMENU).addMenuItem(spleefMenu, 0, 0);
        
        spleefMenu = InventoryMenu.createMenu()
                .setTitle("Splegg Menu")
                .setName("Spleef")
                .setDescription("Imagine the following description included the word egg in it somewhere.\n\nA competitive gamemode in which you must knock your opponent into the water while avoiding a similar fate.\n\nThis is not with any ordinary weapon; the weapon of choice is a shovel, and you must destroy the blocks underneath your foe!")
                .setDisplayItem(Material.EGG);
        spleefMenu.addMenuItem(SpleggArena.createMenu(), 4);
        spleefMenu.addStaticItem(SpleggGun.createMenu(), 4, 4);
        
        InventoryMenu.getHotbarMenu(InventoryMenu.InvMenuType.SLMENU).addMenuItem(spleefMenu, 0, 2);
    }
    
    public void initCommands() {
        Core.getInstance().addCommand(new ShovelCommand());
        Core.getInstance().addCommand(new SpleefCommand());
        
        Core.getInstance().flushCommands();
    }
    
    @Override
    public MongoDatabase getPluginDB() {
        return Core.getInstance().getMongoClient().getDatabase("SuperSpleef");
    }
    
    public Battle getPlayerBattle(SpleefPlayer dbp) {
        return dbp.getBattle();
    }
    public void spectatePlayer(SpleefPlayer spectator, SpleefPlayer target) {
        target.getBattle().addSpectator(spectator, target);
    }
    
}
