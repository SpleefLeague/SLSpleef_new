/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.classic;

import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.game.SpleefBattle;
import java.util.List;

/**
 * @author NickM13
 */
public class ClassicSpleefBattle extends SpleefBattle {
    
    public ClassicSpleefBattle(List<DBPlayer> players, ClassicSpleefArena arena) {
        super(players, arena);
    }
    
    @Override
    protected void startBattle() {
        super.startBattle();
        for (BattlePlayer bp : battlers.values()) {
            bp.player.getPlayer().getInventory().addItem(bp.player.getActiveShovel().getItem());
        }
    }
    
}
