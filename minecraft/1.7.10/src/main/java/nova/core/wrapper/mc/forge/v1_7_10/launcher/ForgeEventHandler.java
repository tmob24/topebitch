/*
 * Copyright (c) 2015 NOVA, All rights reserved.
 * This library is free software, licensed under GNU Lesser General Public License version 3
 *
 * This file is part of NOVA.
 *
 * NOVA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NOVA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NOVA.  If not, see <http://www.gnu.org/licenses/>.
 */

package nova.core.wrapper.mc.forge.v1_7_10.launcher;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;
import nova.core.event.PlayerEvent;
import nova.core.item.Item;
import nova.core.item.ItemDictionary;
import nova.core.wrapper.mc.forge.v1_7_10.wrapper.block.world.WorldConverter;
import nova.core.wrapper.mc.forge.v1_7_10.wrapper.entity.EntityConverter;
import nova.core.wrapper.mc.forge.v1_7_10.wrapper.item.ItemConverter;
import nova.internal.core.Game;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * @author Stan, Calclavia
 */
public class ForgeEventHandler {
	@SubscribeEvent
	public void worldUnload(WorldEvent.Load evt) {
		Game.events().publish(new nova.core.event.WorldEvent.Load(WorldConverter.instance().toNova(evt.world)));
	}

	@SubscribeEvent
	public void worldLoad(WorldEvent.Unload evt) {
		Game.events().publish(new nova.core.event.WorldEvent.Unload(WorldConverter.instance().toNova(evt.world)));
	}

	@SubscribeEvent
	public void onOreRegister(OreDictionary.OreRegisterEvent event) {
		ItemDictionary novaItemDictionary = Game.itemDictionary();

		Item item = ItemConverter.instance().getNovaItem(event.Ore);
		if (!novaItemDictionary.get(event.Name).contains(item)) {
			novaItemDictionary.add(event.Name, item);
		}
	}

	@SubscribeEvent
	public void playerJoin(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent evt) {
		Game.events().publish(new PlayerEvent.Join(EntityConverter.instance().toNova(evt.player)));
	}

	@SubscribeEvent
	public void playerLeave(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent evt) {
		Game.events().publish(new PlayerEvent.Leave(EntityConverter.instance().toNova(evt.player)));
	}

	@SubscribeEvent
	public void playerInteractEvent(PlayerInteractEvent event) {
		nova.core.event.PlayerEvent.Interact evt = new nova.core.event.PlayerEvent.Interact(
			WorldConverter.instance().toNova(event.world),
			new Vector3D(event.x, event.y, event.z),
			EntityConverter.instance().toNova(event.entityPlayer),
			nova.core.event.PlayerEvent.Interact.Action.values()[event.action.ordinal()]
		);

		Game.events().publish(evt);

		event.useBlock = Event.Result.values()[evt.useBlock.ordinal()];
		event.useItem = Event.Result.values()[evt.useItem.ordinal()];
		event.setCanceled(evt.isCanceled());
	}
}
