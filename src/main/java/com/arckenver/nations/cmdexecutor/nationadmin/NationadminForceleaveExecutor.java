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
import com.arckenver.nations.cmdelement.PlayerNameElement;
import com.arckenver.nations.object.Nation;

public class NationadminForceleaveExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.forceleave")
				.arguments(GenericArguments.optional(new PlayerNameElement(Text.of("player"))))
				.executor(new NationadminForceleaveExecutor())
				.build(), "forceleave");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("player").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na forceleave <player>"));
			return CommandResult.success();
		}
		String playerName = ctx.<String>getOne("player").get();
		
		UUID uuid = DataHandler.getPlayerUUID(playerName);
		if (uuid == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADPLAYERNAME));
			return CommandResult.success();
		}
		Nation nation = DataHandler.getNationOfPlayer(uuid);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PLAYERNOTINNATION));
			return CommandResult.success();
		}
		if (nation.isPresident(uuid))
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PLAYERISPRES));
			return CommandResult.success();
		}
		nation.removeCitizen(uuid);
		DataHandler.saveNation(nation.getUUID());
		Sponge.getServer().getPlayer(uuid).ifPresent(p -> 
			p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.SUCCESS_LEAVENATION)));
		src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.SUCCESS_GENERAL));
		return CommandResult.success();
	}
}
