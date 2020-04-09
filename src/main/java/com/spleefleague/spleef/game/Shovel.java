/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import com.spleefleague.core.annotation.DBLoad;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.menu.InventoryMenu;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.vendor.VendorItem;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

/**
 * @author NickM13
 */
public class Shovel extends VendorItem {
    
    private static final Map<Integer, Shovel> shovels = new HashMap<>();
    private static ItemStack LOCKED_ICON;
    private static MongoCollection<Document> shovelCollection;
    
    public static void init() {
        shovelCollection = Spleef.getInstance().getPluginDB().getCollection("Shovels");
        shovelCollection.find().iterator().forEachRemaining(doc -> {
            Shovel shovel = new Shovel();
            shovel.load(doc);
            shovels.put(shovel.getDamage(), shovel);
        });
        
        LOCKED_ICON = InventoryMenu.createItem(Material.DIAMOND_AXE, 12);
    }
    
    public static void save(Shovel shovel) {
        if (shovelCollection.find(new Document("damage", shovel.getDamage())).first() != null) {
            shovelCollection.replaceOne(new Document("damage", shovel.getDamage()), shovel.save());
        } else {
            shovelCollection.insertOne(shovel.save());
        }
    }
    
    public static boolean isShovel(ItemStack item) {
        if (item != null && item.getType().equals(Material.DIAMOND_SHOVEL)) {
            int id = ((Damageable) item.getItemMeta()).getDamage();
            return (shovels.containsKey(id));
        }
        return false;
    }
    
    public static boolean createShovel(int damage) {
        if (shovels.containsKey(damage)) return false;
        Shovel shovel = new Shovel(damage);
        shovels.put(damage, shovel);
        Shovel.save(shovel);
        VendorItem.addVendorItem(shovel);
        return true;
    }
    
    public static Shovel getShovel(int id) {
        return shovels.get(id);
    }
    public static String getShovelName(int id) {
        if (shovels.containsKey(id)) {
            return shovels.get(id).getDisplayName() + Chat.DEFAULT;
        }
        return "" + id;
    }
    
    public static Shovel getDefault() {
        for (Shovel s : shovels.values()) {
            if (s.isDefault()) {
                return s;
            }
        }
        return null;
    }
    
    private static InventoryMenu createActiveShovelMenuItem() {
        return InventoryMenu.createItem()
                .setName(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActiveShovel().getDisplayName();
                }).setDescription(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActiveShovel().getDescription();
                }).setDisplayItem(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActiveShovel().getItem();
                }).setCloseOnAction(false);
    }
    
    public static InventoryMenu createMenuTyped(InventoryMenu menu, ShovelType shovelType) {
        for (Shovel shovel : shovels.values()) {
            if (shovel.getShovelType().equals(shovelType)) {
                InventoryMenu smi = InventoryMenu.createItem()
                        .setName(cp -> {
                            SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                            return sp.hasShovel(shovel.getDamage()) ? shovel.getDisplayName() : "Locked";
                        })
                        .setDisplayItem(cp -> {
                            SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                            return sp.hasShovel(shovel.getDamage()) ? shovel.getItem() : LOCKED_ICON;
                        })
                        .setDescription(cp -> {
                            SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                            return sp.hasShovel(shovel.getDamage()) ? shovel.getDescription() : "";
                        })
                        .setAction(cp -> {
                            SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                            sp.setActiveShovel(shovel.damage);
                        })
                        .setCloseOnAction(false);
                menu.addMenuItem(smi);
            }
        }
        menu.addStaticItem(createActiveShovelMenuItem(), 4, 5);
        return menu;
    }
    
    public static InventoryMenu createMenu() {
        InventoryMenu shovelMenu = InventoryMenu.createMenu()
                .setTitle("Active Shovel")
                .setName("Shovels")
                .setDescription("Set your active shovel")
                .setDisplayItem(cp -> { 
                        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                        return sp.getActiveShovel().getItem();
                        });
        shovelMenu.addStaticItem(createActiveShovelMenuItem(), 4, 5);
        
        shovelMenu.addMenuItem(InventoryMenu.createItem()
                .setName("Default Shovels")
                .setDescription("Shovels you have unlocked by default!")
                .setDisplayItem(new ItemStack(Material.LIGHT_BLUE_BANNER))
                .setAction(cp -> { cp.setInventoryMenu(createMenuTyped(InventoryMenu.createMenu()
                        .setTitle("Default Shovels")
                        .addBackButton(shovelMenu),
                        ShovelType.DEFAULT)); })
                .setCloseOnAction(false));
        
        shovelMenu.addMenuItem(InventoryMenu.createItem()
                .setName("Hidden Shovels")
                .setDescription("Shhhovels!")
                .setDisplayItem(new ItemStack(Material.BLACK_BANNER))
                .setAction(cp -> { cp.setInventoryMenu(createMenuTyped(InventoryMenu.createMenu()
                        .setTitle("Hidden Shovels")
                        .addBackButton(shovelMenu),
                        ShovelType.HIDDEN)); })
                .setCloseOnAction(false));
        
        shovelMenu.addMenuItem(InventoryMenu.createItem()
                .setName("Event Shovels")
                .setDescription("Unlock these by attending special events!")
                .setDisplayItem(new ItemStack(Material.RED_BANNER))
                .setAction(cp -> { cp.setInventoryMenu(createMenuTyped(InventoryMenu.createMenu()
                        .setTitle("Event Shovels")
                        .addBackButton(shovelMenu),
                        ShovelType.EVENT)); })
                .setCloseOnAction(false));
        
        shovelMenu.addMenuItem(InventoryMenu.createItem()
                .setName("Tournament Shovels")
                .setDescription("Unlock these by winning tournaments!")
                .setDisplayItem(new ItemStack(Material.ORANGE_BANNER))
                .setAction(cp -> { cp.setInventoryMenu(createMenuTyped(InventoryMenu.createMenu()
                        .setTitle("Tournament Shovels")
                        .addBackButton(shovelMenu),
                        ShovelType.TOURNAMENT)); })
                .setCloseOnAction(false));
        
        shovelMenu.addMenuItem(InventoryMenu.createItem()
                .setName("Purchased Shovels")
                .setDescription("Shovels you have unlocked by default!")
                .setDisplayItem(new ItemStack(Material.GREEN_BANNER))
                .setAction(cp -> { cp.setInventoryMenu(createMenuTyped(InventoryMenu.createMenu()
                        .setTitle("Purchased Shovels")
                        .addBackButton(shovelMenu),
                        ShovelType.SHOP)); })
                .setCloseOnAction(false));
        
        return shovelMenu;
    }
    
    private enum ShovelType {
        DEFAULT,
        HIDDEN,
        EVENT,
        TOURNAMENT,
        SHOP
    }
    
    public static Set<String> getShovelTypes() {
        return CoreUtils.enumToSet(ShovelType.class);
    }
    
    private ShovelType shovelType;
    
    public Shovel() {
        super("shovel");
        this.material = Material.DIAMOND_SHOVEL;
        this.shovelType = ShovelType.DEFAULT;
    }
    public Shovel(int damage) {
        super("shovel");
        this.material = Material.DIAMOND_SHOVEL;
        this.shovelType = ShovelType.DEFAULT;
        this.damage = damage;
        this.identifier = String.valueOf(damage);
    }
    
    @Override
    public void load(Document doc) {
        super.load(doc);
        this.identifier = String.valueOf(damage);
        addVendorItem(this);
    }
    
    @DBLoad(fieldname="type")
    public void loadType(String str) {
        shovelType = ShovelType.valueOf(str);
    }
    
    public boolean isDefault() {
        return shovelType.equals(ShovelType.DEFAULT);
    }
    public void setShovelType(String type) {
        shovelType = ShovelType.valueOf(type);
    }
    public ShovelType getShovelType() {
        return shovelType;
    }
    
    @Override
    public boolean isUnlocked(CorePlayer cp) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
        return (sp.hasShovel(getDamage()) || isDefault());
    }
    @Override
    public boolean isPurchaseable(CorePlayer cp) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
        return cp.getCoins() >= getCoinCost();
    }
    @Override
    public void purchase(CorePlayer cp) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
        sp.addShovel(getDamage());
        cp.addCoins(-getCoinCost());
    }
    
}
