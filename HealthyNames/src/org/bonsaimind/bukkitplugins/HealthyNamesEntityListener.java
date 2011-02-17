/*
 * This file is part of HealthyNames.
 *
 * HealthyNames is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HealthyNames is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HealthyNames.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Author: Robert 'Bobby' Zenz
 * Website: http://www.bonsaimind.org
 * GitHub: https://github.com/RobertZenz/org.bonsaimind.bukkitplugins/tree/master/HealthyNames
 * E-Mail: bobby@bonsaimind.org
 */
package org.bonsaimind.bukkitplugins;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class HealthyNamesEntityListener extends EntityListener {
	private HealthyNames parent = null;

	public HealthyNamesEntityListener(HealthyNames parentInstance) {
		parent = parentInstance;
	}

	@Override
	public void onEntityCombust(EntityCombustEvent event) {
		if(event.getEntity() instanceof Player) {
			parent.damageOccured((Player)event.getEntity());
		}
		super.onEntityCombust(event);
	}

	@Override
	public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
		if(event.getEntity() instanceof Player) {
			parent.damageOccured((Player)event.getEntity());
		}
		super.onEntityDamageByBlock(event);
	}

	@Override
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player) {
			parent.damageOccured((Player)event.getEntity());
		}
		super.onEntityDamageByEntity(event);
	}

	@Override
	public void onEntityDamageByProjectile(EntityDamageByProjectileEvent event) {
		if(event.getEntity() instanceof Player) {
			parent.damageOccured((Player)event.getEntity());
		}
		super.onEntityDamageByProjectile(event);
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			parent.damageOccured((Player)event.getEntity());
		}
		super.onEntityDamage(event);
	}
}
