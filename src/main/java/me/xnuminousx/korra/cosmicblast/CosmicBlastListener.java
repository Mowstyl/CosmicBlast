package me.xnuminousx.korra.cosmicblast;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.projectkorra.projectkorra.ability.CoreAbility;

public class CosmicBlastListener implements Listener {
	@EventHandler (ignoreCancelled = true)
	public void onSwing(PlayerToggleSneakEvent event) {
		if (!CoreAbility.hasAbility(event.getPlayer(), CosmicBlast.class))
			new CosmicBlast(event.getPlayer());
	}
}
