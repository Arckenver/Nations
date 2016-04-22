package com.arckenver.nations.cmdexecutor.nationadmin;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;

public class NationadminPermExecutor implements CommandExecutor
{
	@Override
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("nation").isPresent() || !ctx.<String>getOne("type").isPresent() || !ctx.<String>getOne("perm").isPresent() || !ctx.<String>getOne("bool").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na perm <nation> <type> <perm> <true|false>"));
			return CommandResult.success();
		}
		String nationName = ctx.<String>getOne("nation").get();
		Nation nation = DataHandler.getNation(nationName);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CB));
			return CommandResult.success();
		}
		String type = ctx.<String>getOne("type").get();
		String perm = ctx.<String>getOne("perm").get();
		boolean bool = (ctx.<Boolean>getOne("bool").isPresent()) ? ctx.<Boolean>getOne("bool").get() : !nation.getPerm(type, perm);
		nation.setPerm(type, perm, bool);
		DataHandler.saveNation(nation.getUUID());
		src.sendMessage(Utils.formatNationDescription(nation, Utils.CLICKER_NONE));
		return CommandResult.success();
	}
}
