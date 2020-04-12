/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.banana;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.game.SpleefBattle;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.util.List;

/**
 * @author NickM13
 */
public class BananaSpleefBattle extends SpleefBattle {
    
    public BananaSpleefBattle(List<DBPlayer> players, BananaSpleefArena arena) {
        super(players, arena);
    }
    
    @Override
    public void updateScoreboard() {
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
        chatGroup.setTeamName("PlayerCount", "Players (" + sortedBattlers.size() + ")");
        chatGroup.setTeamScore("PlayerCount", 1);
        
        /*
        BattlePlayer bp;
        for (int i = 0; i < sortedBattlers.size() && i < seenScores; i++) {
            bp = sortedBattlers.get(i);
            chatGroup.setTeamName("PLACE" + i, Chat.PLAYER_NAME + bp.player.getName());
            chatGroup.setTeamScore("PLACE" + i, bp.points);
        }
        */
    }
    
    @Override
    protected void startBattle() {
        super.startBattle();
        chatGroup.addTeam("PlayerCount", Chat.SCORE + "Players");
        for (BattlePlayer bp : battlers.values()) {
            bp.player.getPlayer().getInventory().addItem(bp.player.getActiveShovel().getItem());
        }
    }
    
    @Override
    protected void failPlayer(SpleefPlayer sp) {
        for (BattlePlayer bp : battlers.values()) {
            if (bp.player.equals(sp)) {
                gameWorld.doFailBlast(sp.getPlayer());
                BattlePlayer cbp = getClosestPlayer(bp);
                if (cbp != null) {
                    cbp.knockouts++;
                    sortBattlers();
                }
                resetPlayer(bp);
                break;
            }
        }
        updateScoreboard();
    }
    
}
