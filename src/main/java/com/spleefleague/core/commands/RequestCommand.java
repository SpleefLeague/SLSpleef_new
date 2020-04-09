/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.annotation.CommandAnnotation;
import com.spleefleague.core.annotation.LiteralArg;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.request.Request;

/**
 * @author NickM13
 */
public class RequestCommand extends CommandTemplate {
    
    public RequestCommand() {
        super(RequestCommand.class, "request", Rank.DEFAULT);
        setUsage("Not for personal use");
    }
    
    @CommandAnnotation(hidden=true)
    public void requestAccept(CorePlayer sender,
            @LiteralArg(value="accept") String test,
            String player) {
        CorePlayer cp;
        if ((cp = Core.getInstance().getPlayers().get(player)) != null) {
            Request.acceptRequest(sender, cp);
        } else {
            error(sender, "No pending request");
        }
    }
    
    @CommandAnnotation(hidden=true)
    public void requestDecline(CorePlayer sender,
            @LiteralArg(value="decline") String test,
            String player) {
        CorePlayer cp;
        if ((cp = Core.getInstance().getPlayers().get(player)) != null) {
            Request.declineRequest(sender, cp);
        } else {
            error(sender, "No pending request");
        }
    }

}
