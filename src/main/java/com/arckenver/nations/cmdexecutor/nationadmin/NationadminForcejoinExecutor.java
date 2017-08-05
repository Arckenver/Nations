package com.arckenver.nations.cmdexecutor.nationadmin;

import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.cmdelement.NationNameElement;
import com.arckenver.nations.cmdelement.PlayerNameElement;
import com.arckenver.nations.object.Nation;

public class NationadminForcejoinExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.forcejoin")
				.arguments(
						GenericArguments.optional(new NationNameElement(Text.of("nation"))),
						GenericArguments.optional(new PlayerNameElement(Text.of("player"))))
				.executor(new NationadminForcejoinExecutor())
				.build(), "forcejoin");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("nation").isPresent() || !ctx.<String>getOne("player").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na forcejoin <nation> <player>"));
			return CommandResult.success();
		}
		String nationName = ctx.<String>getOne("nation").get();
		String playerName = ctx.<String>getOne("player").get();
		
		Nation nation = DataHandler.getNation(nationName);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADNATIONNAME));
			return CommandResult.success();
		}
		UUID playerUUID = DataHandler.getPlayerUUID(playerName);
		if (playerUUID == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADPLAYERNAME));
			return CommandResult.success();
		}
		
		Nation playerNation = DataHandler.getNationOfPlayer(playerUUID);
		if (playerNation != null)
		{
			playerNation.removeCitizen(playerUUID);
			for (UUID uuid : playerNation.getCitizens())
			{
				Sponge.getServer().getPlayer(uuid).ifPresent(p -> 
					p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.INFO_LEAVENATION.replaceAll("\\{PLAYER\\}", playerName))));
			}
		}
		
		for (UUID uuid : nation.getCitizens())
		{
			Sponge.getServer().getPlayer(uuid).ifPresent(p -> 
				p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.INFO_JOINNATIONANNOUNCE.replaceAll("\\{PLAYER\\}", playerName))));
		}
		nation.addCitizen(playerUUID);
		DataHandler.saveNation(nation.getUUID());
		Sponge.getServer().getPlayer(playerUUID).ifPresent(p -> 
			p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.INFO_JOINNATION.replaceAll("\\{NATION\\}", nationName))));
		src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.SUCCESS_GENERAL));
		return CommandResult.success();
	}
}
