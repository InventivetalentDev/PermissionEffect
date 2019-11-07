package org.inventivetalent.permissioneffect;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PermissionEffectPlugin extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		refreshEffects(event.getPlayer());
	}

	@EventHandler
	public void on(PlayerChangedWorldEvent event) {
		refreshEffects(event.getPlayer());
	}

	@EventHandler
	public void on(PlayerRespawnEvent event) {
		refreshEffects(event.getPlayer());
	}

	void refreshEffects(Player player) {
		for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
			if (attachmentInfo.getValue()) {
				String permission = attachmentInfo.getPermission();
				if (permission.startsWith("permissioneffect.")) {
					String[] split = permission.split("\\.");
					int amplifier = 0;
					if (split.length >= 2) {// permissioneffect.abc_def
						PotionEffectType type = PotionEffectType.getByName(split[1]);
						if (type != null) {
							if (split.length >= 3) {// permissioneffect.xyz.4
								try {
									amplifier = Integer.parseInt(split[2]);
								} catch (NumberFormatException ignored) {
								}
							}

							PotionEffect effect;
							try {// 1.14 has a third boolean to disable icon
								effect = new PotionEffect(type, Integer.MAX_VALUE, amplifier, false, false, false);
							} catch (Exception ignored) {
								effect = new PotionEffect(type, Integer.MAX_VALUE, amplifier, false, false);
							}

							player.addPotionEffect(effect,true);
						}
					}
				}
			}
		}
	}

}
