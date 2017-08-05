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

public class NationadminSetpresExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.setpres")
				.arguments(
						GenericArguments.optional(new NationNameElement(Text.of("nation"))),
						GenericArguments.optional(new PlayerNameElement(Text.of("president"))))
				.executor(new NationadminSetpresExecutor())
				.build(), "setpres", "setpresident");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("nation").isPresent() || !ctx.<String>getOne("president").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na setpres <nation> <president>"));
			return CommandResult.success();
		}
		String nationName = ctx.<String>getOne("nation").get();
		Nation nation = DataHandler.getNation(nationName);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADNATIONNAME));
			return CommandResult.success();
		}
		String presidentName = ctx.<String>getOne("president").get();
		UUID presidentUUID = DataHandler.getPlayerUUID(presidentName);
		if (presidentUUID == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADPLAYERNAME));
			return CommandResult.success();
		}
		if (nation.isPresident(presidentUUID))
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PLAYERALREADYPRES));
			return CommandResult.success();
		}
		if (!nation.isCitizen(presidentUUID))
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PLAYERNOTPARTOFNATION));
			return CommandResult.success();
		}
		UUID oldPresidentUUID = nation.getPresident();
		final String oldPresidentName = DataHandler.getPlayerName(oldPresidentUUID);
		nation.setPresident(presidentUUID);
		DataHandler.saveNation(nation.getUUID());
		
		for (UUID citizen : nation.getCitizens())
		{
			Sponge.getServer().getPlayer(citizen).ifPresent(
					p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.INFO_SUCCESSOR
							.replaceAll("\\{SUCCESSOR\\}", presidentName)
							.replaceAll("\\{PLAYER\\}", (oldPresidentName == null) ? LanguageHandler.FORMAT_UNKNOWN : oldPresidentName))));
		}
		
		return CommandResult.success();
	}
}
