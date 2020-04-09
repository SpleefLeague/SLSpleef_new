/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.request;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author NickM13
 */
public class Request {
    
    // Receiver, <Sender, Request>
    protected static Map<String, Map<String, Request>> requests = new HashMap<>();
    
    protected static void validatePlayer(CorePlayer receiver) {
        if (!requests.containsKey(receiver.getName())) {
            requests.put(receiver.getName(), new HashMap<>());
        }
    }
    
    public static void checkTimeouts() {
        CorePlayer receiver, sender;
        for (Map.Entry<String, Map<String, Request>> r : requests.entrySet()) {
            receiver = Core.getInstance().getPlayers().get(r.getKey());
            Iterator<Map.Entry<String, Request>> sit = r.getValue().entrySet().iterator();
            while (sit.hasNext()) {
                Map.Entry<String, Request> sn = sit.next();
                sender = Core.getInstance().getPlayers().get(sn.getKey());
                if (sn.getValue().isTimedout()) {
                    sn.getValue().timeout(receiver, sender);
                    sit.remove();
                }
            }
        }
    }
    
    public static void acceptRequest(CorePlayer receiver, CorePlayer sender) {
        validatePlayer(receiver);
        if (requests.get(receiver.getName()).containsKey(sender.getName())) {
            requests.get(receiver.getName()).get(sender.getName()).accept(receiver, sender);
            requests.get(receiver.getName()).remove(sender.getName());
        } else {
            Core.sendMessageToPlayer(receiver, "Request no longer exists");
        }
    }
    
    public static void declineRequest(CorePlayer receiver, CorePlayer sender) {
        validatePlayer(receiver);
        if (requests.get(receiver.getName()).containsKey(sender.getName())) {
            requests.get(receiver.getName()).get(sender.getName()).decline(receiver, sender);
            requests.get(receiver.getName()).remove(sender.getName());
        } else {
            Core.sendMessageToPlayer(receiver, "Request no longer exists");
        }
    }
    
    public static void sendRequest(String tag, String msg, CorePlayer receiver, CorePlayer sender, BiConsumer<CorePlayer, CorePlayer> action) {
        validatePlayer(receiver);
        requests.get(receiver.getName()).put(sender.getName(), new Request(tag, action));
        
        TextComponent text = new TextComponent(tag);
        TextComponent accept = new TextComponent(Chat.BRACE + "[" + Chat.SUCCESS + "Accept" + Chat.BRACE + "]");
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept").create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/request accept " + sender.getName()));
        TextComponent decline = new TextComponent(Chat.BRACE + "[" + Chat.ERROR + "Decline" + Chat.BRACE + "]");
        decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to decline").create()));
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/request decline " + sender.getName()));
        text.addExtra(accept);
        text.addExtra(" ");
        text.addExtra(decline);
        
        receiver.sendMessage(tag + msg);
        receiver.sendMessage(text);
    }
    
    
    
    private BiConsumer<CorePlayer, CorePlayer> action;
    private String tag;
    private long timeout;
    
    public Request(String tag, BiConsumer<CorePlayer, CorePlayer> action) {
        this.action = action;
        this.tag = tag;
        this.timeout = System.currentTimeMillis() + 10 * 1000;
    }
    
    public boolean isTimedout() {
        return timeout < System.currentTimeMillis();
    }
    
    public void accept(CorePlayer receiver, CorePlayer sender) {
        if (isTimedout()) {
            timeout(receiver, sender);
        } else {
            action.accept(receiver, sender);
        }
    }
    
    public void decline(CorePlayer receiver, CorePlayer sender) {
        receiver.sendMessage(tag + "You have declined " + sender.getDisplayNamePossessive()+ " request");
        sender.sendMessage(tag + receiver.getDisplayName() + " declined your reqeust");
    }
    
    public void timeout(CorePlayer receiver, CorePlayer sender) {
        receiver.sendMessage(tag + "Request from " + sender.getDisplayName()+ " has timed out");
        sender.sendMessage(tag + "Request to " + receiver.getDisplayName() + " has timed out");
    }
    
}
