/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.splegg;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.annotation.DBField;
import com.spleefleague.core.annotation.DBLoad;
import com.spleefleague.core.menu.InventoryMenu;
import com.spleefleague.core.vendor.VendorItem;
import com.spleefleague.core.world.projectile.FakeProjectile;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.util.Map;
import java.util.TreeMap;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class SpleggGun extends VendorItem {
    
    private static final Map<Integer, SpleggGun> spleggGuns = new TreeMap<>();
    private static ItemStack LOCKED_ICON;
    
    public static void init() {
        MongoCollection<Document> collection = Spleef.getInstance().getPluginDB().getCollection("SpleggGuns");
        collection.find().iterator().forEachRemaining(doc -> {
            SpleggGun spleggGun = new SpleggGun();
            spleggGun.load(doc);
            spleggGuns.put(spleggGun.getDamage(), spleggGun);
        });
        
        LOCKED_ICON = InventoryMenu.createItem(Material.DIAMOND_AXE, 12);
    }
    
    public static SpleggGun getSpleggGun(int id) {
        return spleggGuns.get(id);
    }
    
    public static SpleggGun getDefault() {
        for (SpleggGun sg : spleggGuns.values()) {
            return sg;
        }
        return null;
    }
    
    private static InventoryMenu createActiveSpleggGunMenuItem() {
        return InventoryMenu.createItem()
                .setName(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActiveSpleggGun().getDisplayName();
                }).setDescription(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActiveSpleggGun().getDescription();
                }).setDisplayItem(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActiveSpleggGun().getItem();
                }).setCloseOnAction(false);
    }
    
    public static InventoryMenu createMenu() {
        InventoryMenu menu = InventoryMenu.createMenu()
                .setTitle("Active Splegg Gun")
                .setName("Splegg Guns")
                .setDescription("Set your active splegg gun")
                .setDisplayItem(cp -> {
                        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                        return sp.getActiveSpleggGun().getItem();
                        });
        menu.addStaticItem(createActiveSpleggGunMenuItem(), 4, 5);
        
        for (SpleggGun spleggGun : spleggGuns.values()) {
            InventoryMenu smi = InventoryMenu.createItem()
                    .setName(cp -> {
                        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                        return sp.hasSpleggGun(spleggGun.getDamage()) ? spleggGun.getDisplayName() : "Locked";
                    })
                    .setDisplayItem(cp -> {
                        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                        return sp.hasSpleggGun(spleggGun.getDamage()) ? spleggGun.getItem() : LOCKED_ICON;
                    })
                    .setDescription(cp -> {
                        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                        return sp.hasSpleggGun(spleggGun.getDamage()) ? spleggGun.getDescription() : "";
                    })
                    .setAction(cp -> {
                        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                        sp.setActiveSpleggGun(spleggGun.damage);
                    })
                    .setCloseOnAction(false);
                menu.addMenuItem(smi);
        }
        
        return menu;
    }
    
    private FakeProjectile projectile;
    
    public SpleggGun() {
        super("splegg");
        this.material = Material.DIAMOND_SHOVEL;
    }
    
    @DBLoad(fieldname="projectile")
    private void loadProjectile(Document doc) {
        projectile = new FakeProjectile();
        projectile.load(doc);
    }
    
    public boolean isDefault() {
        return true;
    }
    
    @Override
    public void load(Document doc) {
        super.load(doc);
        this.identifier = String.valueOf(damage);
        addVendorItem(this);
    }
    
    public FakeProjectile getProjectile() {
        return projectile;
    }
    
}
