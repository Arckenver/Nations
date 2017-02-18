package com.arckenver.nations.cmdexecutor.nation;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Nation;

public class NationSetspawnExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			if (!ctx.<String>getOne("name").isPresent())
			{
				src.sendMessage(Text.of(TextColors.YELLOW, "/n setspawn <name>"));
				return CommandResult.success();
			}
			String spawnName = ctx.<String>getOne("name").get();
			Player player = (Player) src;
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CI));
				return CommandResult.success();
			}
			if (!nation.isStaff(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CK));
				return CommandResult.success();
			}
			Location<World> newSpawn = player.getLocation();
			if (!nation.getRegion().isInside(newSpawn))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.FX));
				return CommandResult.success();
			}
			if (nation.getNumSpawns() + 1 > nation.getMaxSpawns() && !nation.getSpawns().containsKey(spawnName))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.HR
						.replaceAll("\\{MAX\\}", String.valueOf(nation.getMaxSpawns()))));
				return CommandResult.success();
			}
			if (!spawnName.matches("[\\p{Alnum}\\p{IsIdeographic}\\p{IsLetter}]{1,30}"))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.FY
						.replaceAll("\\{MIN\\}", ConfigHandler.getNode("others", "minZoneNameLength").getString())
						.replaceAll("\\{MAX\\}", ConfigHandler.getNode("others", "maxZoneNameLength").getString())));
				return CommandResult.success();
			}
			nation.addSpawn(spawnName, newSpawn);
			DataHandler.saveNation(nation.getUUID());
			src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.FZ));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
