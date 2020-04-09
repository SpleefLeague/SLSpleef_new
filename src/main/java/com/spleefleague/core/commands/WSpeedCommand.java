/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.annotation.CommandAnnotation;
import com.spleefleague.core.annotation.LiteralArg;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;

/**
 * @author NickM13
 */
public class WSpeedCommand extends CommandTemplate {
    
    public WSpeedCommand() {
        super(WSpeedCommand.class, "wspeed", Rank.MODERATOR, Rank.BUILDER);
        setUsage("/wspeed [player] <-10 to 10>");
        setDescription("Set walking speed");
    }
    
    @CommandAnnotation
    public void wspeed(CorePlayer sender, Double f) {
        f /= 10.;
        sender.getPlayer().setWalkSpeed(f.floatValue());
        success(sender, "Fly speed set to " + f);
    }
    
    @CommandAnnotation
    public void wspeed(CorePlayer sender, CorePlayer cp, Double f) {
        f /= 10.;
        cp.getPlayer().setWalkSpeed(f.floatValue());
        success(cp, "Fly speed set to " + f);
        success(sender, "Fly speed of " + cp.getDisplayName() + " set to " + f);
    }
    
    @CommandAnnotation
    public void wspeed(CorePlayer sender, CorePlayer cp, @LiteralArg(value="reset") String l) {
        wspeed(sender, cp, 2.);
    }

}
