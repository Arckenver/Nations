package com.arckenver.nations.cmdexecutor.nation;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Nation;

public class NationTaxesExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
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
			if (!ctx.<Double>getOne("amount").isPresent())
			{
				src.sendMessage(Text.of(TextColors.YELLOW, "/n taxes <amount>"));
				return CommandResult.success();
			}
			double newTaxes = ctx.<Double>getOne("amount").get();
			if (!ConfigHandler.getNode("nations", "canEditTaxes").getBoolean())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.HN));
				return CommandResult.success();
			}
			if (newTaxes > ConfigHandler.getNode("nations", "maxTaxes").getDouble())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.HO.replaceAll("\\{AMOUNT\\}", String.valueOf(ConfigHandler.getNode("nations", "maxTaxes").getDouble()))));
				return CommandResult.success();
			}
			nation.setTaxes(newTaxes);
			DataHandler.saveNation(nation.getUUID());
			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.HP));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
