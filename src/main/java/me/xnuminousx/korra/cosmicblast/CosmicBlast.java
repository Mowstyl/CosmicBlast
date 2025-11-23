package me.xnuminousx.korra.cosmicblast;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AvatarAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.DamageHandler;

public class CosmicBlast extends AvatarAbility implements AddonAbility {
	private long cooldown;
	private double range;
	private double damage;
	private boolean isCharged;
	private boolean launched;
	private Location origin;
	private Location location;
	private Vector direction;
	private double t;
	private Permission perm;
	private boolean doPotEffects;
	private boolean doDamage;

	public CosmicBlast(Player player) {
		super(player);
		if (!this.bPlayer.canBend(this)) {
			return;
		}
		setFields();
		start();
	}

	public void setFields() {
		this.cooldown = ConfigManager.getConfig().getLong("ExtraAbilities.xNuminousx.CosmicBlast.Cooldown");
		this.range = ConfigManager.getConfig().getLong("ExtraAbilities.xNuminousx.CosmicBlast.Range");
		this.damage = ConfigManager.getConfig().getLong("ExtraAbilities.xNuminousx.CosmicBlast.Damage");
		this.doPotEffects = ConfigManager.getConfig().getBoolean("ExtraAbilities.xNuminousx.CosmicBlast.DoPotionEffects");
		this.doDamage = ConfigManager.getConfig().getBoolean("ExtraAbilities.xNuminousx.CosmicBlast.DoDamage");
	}
	
	@Override
	public void progress() {
		if (player.isDead() || !player.isOnline()) {
			return;
		}
		if ((player.isSneaking()) && (!launched)) {
			chargeAnimation();
		} else {
			if (!isCharged) {
				remove();
				return;
			}
			if (!launched) {
				bPlayer.addCooldown(this);
				launched = true;
			}
			blast();
		}

	}

	private void chargeAnimation() {
		t += Math.PI / 32;
		Location loc = player.getLocation();
		World world = loc.getWorld();
		if (t >= Math.PI * 4) {
			world.spawnParticle(Particle.FIREWORK, player.getLocation().add(0, 1, 0), 1, 0.2, 0.3, 0.2, 0.03F);
			world.spawnParticle(Particle.DRAGON_BREATH, loc, 1, 0.3, 0.1, 0.3, 0.02F, 0F);
			location = GeneralMethods.getTargetedLocation(player, 1);
			origin = GeneralMethods.getTargetedLocation(player, 1);
			direction = GeneralMethods.getTargetedLocation(player, 1).getDirection();
			isCharged = true;
			launched = false;
		}
		
		if (isCharged) {
			loc.getWorld().playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 0.2F, 1);
			
		} else {
			for (double phi = 0; phi <= Math.PI * 2; phi += Math.PI / 1.5) {
				double x = 0.3D * (Math.PI * 4 - t) * Math.cos(t + phi);
	            double y = 1.5D * (Math.PI * 4 - t);
	            double z = 0.3D * (Math.PI * 4 - t) * Math.sin(t + phi);
	            loc.add(x, y, z);
				world.spawnParticle(Particle.PORTAL, loc, 5, 0, 0, 0, 0.1F);
				world.spawnParticle(Particle.ENTITY_EFFECT, loc, 2, 0, 0, 0, 0.02F, Color.PURPLE);
				world.spawnParticle(Particle.END_ROD, loc, 2, 0, 0, 0, 0);
				world.spawnParticle(Particle.ENCHANTED_HIT, loc, 5, 0, 0, 0, 0);
				loc.subtract(x, y, z);
				loc.getWorld().playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 0.2F, 1);
			}
		}
	}

	private void blast() {
		direction = GeneralMethods.getTargetedLocation(player, 1).getDirection();
		location.add(direction);
		location.getWorld().spawnParticle(Particle.ENCHANTED_HIT, location, 5, 0, 0, 0, 1);
		location.getWorld().spawnParticle(Particle.END_ROD, location, 3, 0, 0, 0, 0.05F);
		location.getWorld().spawnParticle(Particle.PORTAL, location, 5, 0, 0, 0, 1.5F);
		location.getWorld().playSound(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1, 0.01F);
		
		for (Entity e : GeneralMethods.getEntitiesAroundPoint(location, 2.5D)) {
			if (((e instanceof LivingEntity)) && (e.getEntityId() != player.getEntityId())) {
				if (doDamage) {
					DamageHandler.damageEntity(e, damage, this);
					
				}
				if (doPotEffects) {
					LivingEntity le = (LivingEntity)e;
					le.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 1));
					le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 1));
					
				}
				remove();
				return;
			}
		}
		if (origin.distance(location) > range) {
			remove();
			bPlayer.addCooldown(this);
			return;
		}
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return "CosmicBlast";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public String getAuthor() {
		return "xNuminousx";
	}

	@Override
	public String getDescription() {
		return "Meditate on your 7th Chakra to gain energy and guidence from the cosmos. Focus this energy outward toward your opponent to exhaust them and deal damage.";
	}

	@Override
	public String getInstructions() {
		return "Hold SHIFT until charge animation finishes";
	}

	@Override
	public String getVersion() {
		return "1.4";
	}

	@Override
	public void load() {
		ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new CosmicBlastListener(), ProjectKorra.plugin);
		ProjectKorra.log.info("Successfully loaded " + getName() + " by " + getAuthor());
		
		perm = new Permission("bending.ability.cosmicblast");
		ProjectKorra.plugin.getServer().getPluginManager().addPermission(perm);
		perm.setDefault(PermissionDefault.FALSE);
		
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.Cooldown", 5000);
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.Range", 20);
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.DoPotionEffects", true);
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.DoDamage", true);
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.Damage", 5);
		ConfigManager.defaultConfig.save();
	}

	@Override
	public void stop() {
		Bukkit.getServer().getPluginManager().removePermission(this.perm);
		super.remove();
		ProjectKorra.plugin.getLogger().info(getName() + "disabled.");
	}
}