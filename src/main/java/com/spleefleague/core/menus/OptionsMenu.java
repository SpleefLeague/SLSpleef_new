/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menus;

import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class OptionsMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            // Options Menus
            menuItem = InventoryMenuAPI.createItem()
                    .setName("Options")
                    .setDisplayItem(new ItemStack(Material.WRITABLE_BOOK))
                    .setDescription("Customize your SpleefLeague experience")
                    .createLinkedContainer("Options Menu");

            // Chat Options Menus
            InventoryMenuItem chatOptionsItem = InventoryMenuAPI.createItem()
                    .setName("Chat Channels")
                    .setDisplayItem(new ItemStack(Material.WRITABLE_BOOK))
                    .setDescription("Toggle Chat Channels");

            for (ChatChannel.Channel channel : ChatChannel.Channel.values()) {
                chatOptionsItem.getLinkedContainer()
                        .addMenuItem(InventoryMenuAPI.createItem()
                        .setName(ChatChannel.getChannel(channel).getName())
                        .setDescription(cp -> { return "This chat is "
                                + (cp.isChannelDisabled(channel.toString()) ? (ChatColor.RED + "Disabled") : (ChatColor.GREEN + "Enabled")); })
                        .setDisplayItem(cp -> { return new ItemStack( cp.isChannelDisabled(channel.toString()) ? Material.BOOK : Material.WRITABLE_BOOK); })
                        .setAction(cp -> { cp.toggleDisabledChannel(channel.toString()); })
                        .setCloseOnAction(false)
                        .setVisibility(cp -> ChatChannel.getChannel(channel).isAvailable(cp)));
            }

            menuItem.getLinkedContainer()
                    .addMenuItem(chatOptionsItem);
        }
        return menuItem;
    }

}
