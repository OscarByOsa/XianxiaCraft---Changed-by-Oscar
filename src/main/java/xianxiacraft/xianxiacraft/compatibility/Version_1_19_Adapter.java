package xianxiacraft.xianxiacraft.compatibility;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Version_1_19_Adapter implements VersionAdapter {
    public Material getMaterial(String name) {
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException e) {
            switch (name) {
                case "DRIPPING_OBSIDIAN_TEAR": return Material.LAVA;
                case "SCULK_CHARGE_POP": return Material.STONE;
                default: return Material.STONE;
            }
        }
    }
    
    public Particle getParticle(String name) {
        try {
            return Particle.valueOf(name);
        } catch (IllegalArgumentException e) {
            return Particle.FLAME;
        }
    }
    
    public Sound getSound(String name) {
        try {
            return Sound.valueOf(name);
        } catch (IllegalArgumentException e) {
            return Sound.ENTITY_PLAYER_LEVELUP;
        }
    }
    
    public boolean isFlyingSupported(Player player) {
        return true;
    }
    
    public String getVersionName() {
        return "Minecraft 1.19";
    }
}