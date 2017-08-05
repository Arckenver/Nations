package com.arckenver.nations.cmdexecutor.nation;

import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.cmdelement.CitizenNameElement;
import com.arckenver.nations.object.Nation;

public class NationResignExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.resign")
				.arguments(GenericArguments.optional(new CitizenNameElement(Text.of("successor"))))
				.executor(new NationResignExecutor())
				.build(), "resign");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			
			Player player = (Player) src;
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NONATION));
				return CommandResult.success();
			}
			if (!nation.isPresident(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_NATIONPRES));
				return CommandResult.success();
			}
			if (!ctx.<String>getOne("successor").isPresent())
			{
				src.sendMessage(Text.of(TextColors.YELLOW, "/n resign <successor>"));
				return CommandResult.success();
			}
			String successorName = ctx.<String>getOne("successor").get();
			UUID successor = DataHandler.getPlayerUUID(successorName);
			if (successor == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADPRESNAME));
				return CommandResult.success();
			}
			if (!nation.isCitizen(successor))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOTINNATION));
				return CommandResult.success();
			}
			nation.removeMinister(successor);
			nation.setPresident(successor);
			DataHandler.saveNation(nation.getUUID());
			for (UUID citizen : nation.getCitizens())
			{
				Sponge.getServer().getPlayer(citizen).ifPresent(
					p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.INFO_SUCCESSOR.replaceAll("\\{SUCCESSOR\\}", successorName).replaceAll("\\{PLAYER\\}", player.getName()))));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
