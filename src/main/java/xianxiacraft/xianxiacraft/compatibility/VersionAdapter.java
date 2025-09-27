package xianxiacraft.xianxiacraft.compatibility;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public interface VersionAdapter {
    Material getMaterial(String name);
    Particle getParticle(String name);
    Sound getSound(String name);
    boolean isFlyingSupported(Player player);
    String getVersionName();
}