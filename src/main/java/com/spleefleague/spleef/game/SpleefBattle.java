/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.Request;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.Dimension;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class SpleefBattle extends Battle<SpleefPlayer, SpleefArena> {
    
    /*
    SpleefBattle object contains a number of players, their scores,
    the spectators, an arena, and the game loop functions
    
    This looked like a good place to put a comment about how much
    I enjoy playing Spleef on the server SpleefLeague!
    */
    
    protected class BattlePlayer {
        public SpleefPlayer player;
        public int points;
        public boolean fallen;
        public Location spawn;
        
        public BattlePlayer(SpleefPlayer player, Location spawn) {
            this.player = player;
            points = 0;
            fallen = false;
            this.spawn = spawn;
        }
    }
    // List of battling players
    protected final Map<SpleefPlayer, BattlePlayer> battlers = new HashMap<>();
    
    // Round countdown timer
    protected final Request<Integer> pauseRequest = new Request<>();
    protected final Request<Long> resetRequest = new Request<>();
    protected final Request<Integer> playToRequest = new Request<>();
    protected final Request endGameRequest = new Request();
    
    protected static final Material SPLEEFER_TOOL = Material.DIAMOND_SHOVEL;
    
    // Needed points to win game
    protected static final int WIN_POINTS = 5;
    protected int playToPoints = WIN_POINTS;
    
    protected int remainingPlayers = 0;
    
    public SpleefBattle(List<DBPlayer> players, SpleefArena arena) {
        super(Spleef.getInstance(), players, arena);
    }
    
    @Override
    public void updateScoreboard() {
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
        chatGroup.setTeamScore("PlayTo", playToPoints);
        for (BattlePlayer bp : battlers.values()) {
            CorePlayer cp = Core.getInstance().getPlayers().get(bp.player);
            chatGroup.setTeamScore(cp.getName(), bp.points);
        }
    }
    
    protected void addBattler(SpleefPlayer sp, int spawn) {
        battlers.put(sp, new BattlePlayer(sp, arena.getSpawns().get(spawn)));
        sp.joinBattle(this, BattleState.BATTLER);

        sp.getPlayer().getInventory().setHeldItemSlot(0);
        sp.getPlayer().getInventory().clear();

        sp.setGameMode(GameMode.SURVIVAL);

        chatGroup.addPlayer(sp);
    }
    
    protected void setupBattlers() {
        int i = 0;
        for (SpleefPlayer sp : players) {
            addBattler(sp, i);
            i++;
        }
    }
    
    @Override
    protected void startBattle() {
        setupBattlers();
        super.startBattle();
        chatGroup.addTeam("PlayTo", Chat.SCORE + "PlayTo");
        gameWorld.addBreakTool(SPLEEFER_TOOL);
        gameWorld.addBreakableBlock(Material.SNOW_BLOCK);
        Core.getInstance().sendMessage("A " +
                Chat.GAMEMODE + getMode().getDisplayName() + " " +
                Chat.DEFAULT + "match between " +
                playersFormatted +
                Chat.DEFAULT + " has begun on " +
                Chat.GAMEMAP + arena.getDisplayName());
        setupScoreboardTeams();
    }
    
    protected void setupScoreboardTeams() {
        for (BattlePlayer bp : battlers.values()) {
            chatGroup.addTeam(bp.player.getName(), Chat.PLAYER_NAME + bp.player.getName());
        }
    }
    
    @Override
    protected void startRound() {
        super.startRound();
        for (SpleefPlayer sp : battlers.keySet()) {
            sp.getPlayer().setGameMode(gameMode);
        }
    }
    
    protected void endRound(BattlePlayer winner) {
        winner.points++;
        if (winner.points < playToPoints) {
            Core.getInstance().sendMessage(chatGroup, Chat.PLAYER_NAME + winner.player.getDisplayName() + Chat.DEFAULT + " won the round");
        } else {
            endBattle(winner);
        }
    }
    
    public void endBattle(BattlePlayer winner) {
        BattlePlayer loser = null;
        for (BattlePlayer bp : battlers.values()) {
            if (!bp.equals(winner)) {
                loser = bp;
            }
        }
        if (loser == null) {
            loser = winner;
        }
        Core.getInstance().sendMessage(ChatChannel.getChannel(ChatChannel.Channel.SPLEEF),
                Chat.PLAYER_NAME + winner.player.getDisplayName() +
                winner.player.getDisplayElo(getMode()) +
                Chat.DEFAULT + " has " + this.randomDefeatSynonym() + " " +
                Chat.PLAYER_NAME + loser.player.getDisplayName() +
                loser.player.getDisplayElo(getMode()) +
                Chat.DEFAULT + " in " +
                Chat.GAMEMODE + getMode().getDisplayName() + " " +
                Chat.DEFAULT + "(" +
                Chat.SCORE + winner.points + Chat.DEFAULT + "-" + Chat.SCORE + loser.points +
                Chat.DEFAULT + ")");
        applyEloChange(winner);
        super.endBattle();
    }
    
    @Override
    public boolean surrender(SpleefPlayer sp) {
        if (battlers.containsKey(sp)) {
            if (battlers.size() <= 1) {
                Core.getInstance().sendMessage(Chat.PLAYER_NAME + sp.getDisplayName() +
                        Chat.DEFAULT + " has surrendered");
                endBattle();
                return true;
            } else {
                BattlePlayer winner = (BattlePlayer) (battlers.keySet().toArray()[0].equals(sp) ? battlers.values().toArray()[1] : battlers.values().toArray()[0]);
                endBattle(winner);
                Core.getInstance().sendMessage(Chat.PLAYER_NAME + sp.getDisplayName() +
                        Chat.DEFAULT + " has surrendered to " +
                        Chat.PLAYER_NAME + winner.player.getDisplayName());
            }
            return true;
        }
        return false;
    }
    
    @Override
    protected void resetPlayers() {
        remainingPlayers = battlers.size();
        battlers.forEach((p, bp) -> {
            bp.fallen = false;
            p.getPlayer().teleport(bp.spawn);
        });
    }
    
    protected void applyEloChange(BattlePlayer winner) {
        int avgRating = 0;
        int eloChange = 0;
        
        for (BattlePlayer bp : battlers.values()) {
            avgRating += bp.player.getRating(getMode());
        }
        avgRating /= battlers.size();
        /**
         * ELO change is 20 if players are the same rank
         * exponentially increasing/decreasing between (5, 40)
         */
        eloChange = (int) (20 - Math.max(0, Math.min(2, Math.sqrt(Math.abs((double)(winner.player.getRating(getMode()) - avgRating))) * 100)));
        if (winner.player.getRating(getMode()) > avgRating) {
            eloChange = 20 - (int)(Math.min((winner.player.getRating((getMode())) - avgRating) / 100.0, 1) * 15);
        } else {
            eloChange = 20 + (int)(Math.min((avgRating - winner.player.getRating((getMode()))) / 100.0, 1) * 20);
        }
        
        for (BattlePlayer sbp : battlers.values()) {
            if (sbp.equals(winner)) {
                sbp.player.addRating(getMode(), eloChange);
            } else {
                sbp.player.addRating(getMode(), -eloChange);
            }
        }
    }
    
    public void breakBlock(Location loc) {
        gameWorld.setBlock(new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), Material.AIR.createBlockData(), false);
    }
    
    public void chipBlock(BlockPosition pos, int amount) {
        gameWorld.chipBlock(pos, amount);
    }
    
    @Override
    protected void fillField() {
        for (Dimension field : arena.getField().getAreas()) {
            // Clear field
            gameWorld.clear();

            // Fill field with delayed blocks (snow goes up in stacks)
            for (int x = (int)field.getLow().x; x <= (int)field.getHigh().x; x++) {
                for (int y = (int)field.getLow().y; y <= (int)field.getHigh().y; y++) {
                    for (int z = (int)field.getLow().z; z <= (int)field.getHigh().z; z++) {
                        //fakeWorld.setBlock(new BlockPosition(x, y, z), Material.SNOW_BLOCK.createBlockData(), true);
                        gameWorld.setBlockDelayed(new BlockPosition(x, y, z), Material.SNOW_BLOCK.createBlockData(), 0.2D, arena.getSpawns());
                    }
                }
            }
        }
    }
    
    public void resetRound() {
        startRound();
        Core.getInstance().sendMessage("The field has been reset");
    }
    
    @Override
    public void requestPause(SpleefPlayer sp) {
        if (pauseRequest != null && !pauseRequest.getRequester().equals(sp)) {
            countdown = pauseRequest.getRequestedValue();
            pauseRequest.setRequest(null);
            Core.getInstance().sendMessage("The field has been paused for " + Chat.TIME + countdown + Chat.DEFAULT + " seconds");
        } else {
            requestPause(sp, 120);
        }
    }
    @Override
    public void requestPause(SpleefPlayer sp, int timeout) {
        CorePlayer cp = Core.getInstance().getPlayers().get(sp);
        if (!pauseRequest.getRequester().equals(sp)
                && pauseRequest.getRequestedValue() == timeout) {
            requestPause(sp);
        } else {
            pauseRequest.setRequest(sp, Math.min(Math.max(timeout, 1), 120));
            Core.getInstance().sendMessage(this.chatGroup, Chat.PLAYER_NAME + cp.getDisplayName() + Chat.DEFAULT + " is requesting to pause match for " + Chat.TIME + pauseRequest.getRequestedValue() + Chat.DEFAULT + " seconds");
        }
    }
    
    @Override
    public void requestReset(SpleefPlayer sp) {
        CorePlayer cp = Core.getInstance().getPlayers().get(sp);
        if (resetRequest.getRequester() == null || resetRequest.getRequester().equals(sp)) {
            resetRequest.setRequest(sp, System.currentTimeMillis() + 1000 * 15);
            Core.getInstance().sendMessage(this.chatGroup, Chat.PLAYER_NAME + cp.getDisplayName() + Chat.DEFAULT + " is requesting to reset the field");
        } else {
            resetRequest.setRequest(null);
            resetRound();
        }
    }
    
    @Override
    public void requestPlayTo(SpleefPlayer sp) {
        if (playToRequest.getRequester() != null &&
                !playToRequest.getRequester().equals(sp)) {
            playToRequest.setRequest(null);
            playToPoints = playToRequest.getRequestedValue();
            Core.getInstance().sendMessage(this.chatGroup, "Match is now playing to " + Chat.SCORE + playToPoints + Chat.DEFAULT + " points");
        }
    }
    @Override
    public void requestPlayTo(SpleefPlayer sp, int playTo) {
        CorePlayer cp = Core.getInstance().getPlayers().get(sp);
        if (playTo < 1 || playTo > 100) {
            Core.getInstance().sendMessage(sp, "PlayTo number invalid (1-100)");
        } else {
            if (playToRequest.getRequester() != null &&
                    !playToRequest.getRequester().equals(sp) &&
                    playToRequest.getRequestedValue() == playTo) {
                requestPlayTo(sp);
            } else {
                Core.getInstance().sendMessage(this.chatGroup, Chat.PLAYER_NAME + cp.getDisplayName() + Chat.DEFAULT + " is requesting to play to " + Chat.SCORE + playTo + Chat.DEFAULT + " points");
                playToRequest.setRequest(sp, playTo);
            }
        }
    }
    
    @Override
    public void requestEndGame(SpleefPlayer sp) {
        CorePlayer cp = Core.getInstance().getPlayers().get(sp);
        if (endGameRequest.getRequester() == null || endGameRequest.getRequester().equals(sp)) {
            endGameRequest.setRequest(sp);
            Core.getInstance().sendMessage(this.chatGroup, Chat.PLAYER_NAME + cp.getDisplayName() + Chat.DEFAULT + " is requesting to end the game");
        } else {
            endGameRequest.setRequest(null);
            this.endBattle();
        }
    }
    
    public void setPlayToPoints(int playToPoints) {
        this.playToPoints = playToPoints;
        Core.getInstance().sendMessage("The match is now set to playto " + Chat.GAMEMODE + playToPoints + Chat.DEFAULT);
    }
    
    @Override
    protected void failPlayer(SpleefPlayer sp) {
        System.out.println("fail player spleef battle");
        for (BattlePlayer bp : battlers.values()) {
            if (bp.player.equals(sp)) {
                bp.fallen = true;
                remainingPlayers--;
                gameWorld.doFailBlast(sp.getPlayer());
                break;
            }
        }
        if (remainingPlayers <= 1) {
            if (remainingPlayers < 1) {
                endRound(battlers.values().iterator().next());
            } else {
                for (BattlePlayer bp : battlers.values()) {
                    if (!bp.fallen) {
                        endRound(bp);
                        break;
                    }
                }
            }
            startRound();
        }
        updateScoreboard();
    }
    
    @Override
    public void doCountdown() {
        super.doCountdown();
        if (resetRequest.getRequester() != null) {
            if (resetRequest.getRequestedValue() < System.currentTimeMillis()) {
                Core.getInstance().sendMessage(this.chatGroup, "Reset request has timed out");
                resetRequest.setRequest(null);
            }
        }
    }
    
}
