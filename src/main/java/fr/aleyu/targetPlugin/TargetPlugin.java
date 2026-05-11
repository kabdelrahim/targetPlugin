package fr.aleyu.targetPlugin;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class TargetPlugin extends JavaPlugin {

    private final Set<UUID> enabledPlayers = new HashSet<>();


    @Override
    public void onEnable() {

        getCommand("target").setExecutor((sender, command, label, args) -> {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("Commande joueur uniquement.");
                return true;
            }

            //toggle
            if (enabledPlayers.contains(player.getUniqueId())) {

                enabledPlayers.remove(player.getUniqueId());
                player.sendMessage("§cMode target désactivé.");
            } else {
                enabledPlayers.add(player.getUniqueId());
                player.sendMessage("§aMode target activé.");
            }

            return true;
        });

        new BukkitRunnable() {

            @Override
            public void run() {
                for (UUID uuid : enabledPlayers) {
                    Player player = getServer().getPlayer(uuid);
                    if (player == null) {
                        continue;
                    }

                    // Récupérer la cible du joueur
                    Entity target = player.getTargetEntity(10); // 10 blocs de distance max

                    // Si la cible est un joueur
                    if (target instanceof Player targetPlayer) {
                        player.sendActionBar(
                                Component.text("§e" + targetPlayer.getName())
                        );
                    } else if (target instanceof ArmorStand armorStand) {
                        String name = armorStand.getCustomName();

                        if (name != null) {
                            player.sendActionBar(
                                    Component.text("§bArmorStand:" + name)
                            );
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L); // Exécute toutes les secondes (20 ticks)
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled");
    }
}
