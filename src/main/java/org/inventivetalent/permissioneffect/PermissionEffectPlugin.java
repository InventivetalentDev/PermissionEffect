package org.inventivetalent.permissioneffect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("permissioneffect".equals(command.getName())) {
			if(!sender.hasPermission("permissioneffect.admin"))return false;

			if (args.length == 0) {
				sender.sendMessage("/pe refresh <player>");
				return true;
			}
			if ("refresh".equalsIgnoreCase(args[0])) {
				Player player;
				if (args.length == 1) {
					player = (Player)sender;
				}else{
					player = Bukkit.getPlayer(args[1]);
				}
				if (player == null || !player.isOnline()) {
					sender.sendMessage(ChatColor.RED + "Player not found");
					return false;
				}
				refreshEffects(player);
				sender.sendMessage(ChatColor.GREEN + "Refreshed effects for " + player.getName());
				return true;
			}
		}


		return super.onCommand(sender, command, label, args);
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
						PotionEffectType type = PotionEffectType.getByName(split[1].toUpperCase());
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
