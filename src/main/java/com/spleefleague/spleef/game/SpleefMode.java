/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.spleef.game.splegg.classic.SpleggBattle;
import com.spleefleague.spleef.game.splegg.classic.SpleggArena;
import com.spleefleague.spleef.game.spleef.power.PowerSpleefArena;
import com.spleefleague.spleef.game.spleef.power.PowerSpleefBattle;
import com.spleefleague.spleef.game.spleef.multi.MultiSpleefBattle;
import com.spleefleague.spleef.game.spleef.multi.MultiSpleefArena;
import com.spleefleague.spleef.game.spleef.classic.ClassicSpleefBattle;
import com.spleefleague.spleef.game.spleef.classic.ClassicSpleefArena;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.spleef.game.team.*;

/**
 * @author NickM13
 */
public enum SpleefMode {
    
    CLASSIC,
    TEAM,
    MULTI,
    POWER,
    WC,
    SPLEGG;
    
    public static void init() {
        ArenaMode.addArenaMode("SPLEEF_CLASSIC", "Classic Spleef", 2, ArenaMode.TeamStyle.MULTI_STATIC, false, ClassicSpleefArena.class, ClassicSpleefBattle.class);
        ArenaMode.addArenaMode("SPLEEF_TEAM", "Team Spleef", 2, ArenaMode.TeamStyle.TEAM, false, TeamSpleefArena.class, TeamSpleefBattle.class);
        ArenaMode.addArenaMode("SPLEEF_MULTI", "Multispleef", 8, ArenaMode.TeamStyle.MULTI_DYNAMIC, true, MultiSpleefArena.class, MultiSpleefBattle.class);
        ArenaMode.addArenaMode("SPLEEF_POWER", "Power Spleef", 2, ArenaMode.TeamStyle.MULTI_STATIC, false, PowerSpleefArena.class, PowerSpleefBattle.class);
        ArenaMode.addArenaMode("SPLEEF_WC", "SWC", 2, ArenaMode.TeamStyle.MULTI_STATIC, false, null, null);
        ArenaMode.addArenaMode("SPLEEF_SPLEGG", "Splegg", 2, ArenaMode.TeamStyle.MULTI_STATIC, false, SpleggArena.class, SpleggBattle.class);
    }
    
    public ArenaMode getArenaMode() {
        return ArenaMode.getArenaMode("SPLEEF_" + this.name());
    }
    
}
