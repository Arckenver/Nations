package com.arckenver.nations.listener;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.object.Rect;
import com.flowpowered.math.vector.Vector2i;

public class GoldenAxeListener
{
	@Listener
	public void onPlayerRightClick(InteractBlockEvent.Secondary event, @First Player player)
	{
		Optional<ItemStack> optItem = player.getItemInHand();
		if (!optItem.isPresent())
		{
			return;
		}
		if (optItem.get().getItem().equals(ItemTypes.GOLDEN_AXE))
		{
			event.setCancelled(true);
			Optional<Location<World>> optLoc = event.getTargetBlock().getLocation();
			if (!optLoc.isPresent())
			{
				return;
			}
			Vector2i secondPoint = new Vector2i(optLoc.get().getBlockX(), optLoc.get().getBlockZ());
			DataHandler.setSecondPoint(player.getUniqueId(), secondPoint);
			Vector2i firstPoint = DataHandler.getFirstPoint(player.getUniqueId());
			player.sendMessage(Text.of(
					TextColors.AQUA,
					"Second position set to (" + secondPoint.getX() + " " + secondPoint.getY() + ")" +
					((firstPoint != null) ? " (" + new Rect(null, firstPoint, secondPoint).size() + ")" : "")));
		}
	}
	
	@Listener
	public void onPlayerLeftClick(InteractBlockEvent.Primary event, @First Player player)
	{
		Optional<ItemStack> optItem = player.getItemInHand();
		if (!optItem.isPresent())
		{
			return;
		}
		if (optItem.get().getItem().equals(ItemTypes.GOLDEN_AXE))
		{
			event.setCancelled(true);
			Optional<Location<World>> optLoc = event.getTargetBlock().getLocation();
			if (!optLoc.isPresent())
			{
				return;
			}
			Vector2i firstPoint = new Vector2i(optLoc.get().getBlockX(), optLoc.get().getBlockZ());
			DataHandler.setFirstPoint(player.getUniqueId(), firstPoint);
			Vector2i secondPoint = DataHandler.getSecondPoint(player.getUniqueId());
			player.sendMessage(Text.of(
					TextColors.AQUA,
					"First position set to (" + firstPoint.getX() + " " + firstPoint.getY() + ")" +
					((secondPoint != null) ? " (" + new Rect(null, firstPoint, secondPoint).size() + ")" : "")));
		}
	}
}
