package com.arckenver.nations.cmdexecutor.nationadmin;

import java.util.UUID;

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
import com.arckenver.nations.object.Rect;

public class NationadminCreateExecutor implements CommandExecutor
{
	@Override
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("name").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na create <name>"));
			return CommandResult.success();
		}
		if (src instanceof Player)
		{
			Player player = (Player) src;
			String nationName = ctx.<String>getOne("name").get();
			if (DataHandler.getNation(nationName) != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EL));
				return CommandResult.success();
			}
			if (!nationName.matches("[a-zA-Z0-9\\._-]{1,}"))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EM));
				return CommandResult.success();
			}
			if (nationName.length() < ConfigHandler.getNode("others", "minNationNameLength").getInt() || nationName.length() > ConfigHandler.getNode("others", "maxNationNameLength").getInt())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EN
						.replaceAll("\\{MIN\\}", ConfigHandler.getNode("others", "minNationNameLength").getString())
						.replaceAll("\\{MAX\\}", ConfigHandler.getNode("others", "maxNationNameLength").getString())));
				return CommandResult.success();
			}
			
			Location<World> loc = player.getLocation();
			if (!DataHandler.canClaim(loc, true))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EI));
				return CommandResult.success();
			}
			
			Nation nation = new Nation(UUID.randomUUID(), nationName, true);
			nation.getRegion().addRect(new Rect(loc.getExtent().getUniqueId(), loc.getBlockX(), loc.getBlockX(), loc.getBlockZ(), loc.getBlockZ()));
			DataHandler.addNation(nation);
			DataHandler.addToWorldChunks(nation);
			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.HL));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
