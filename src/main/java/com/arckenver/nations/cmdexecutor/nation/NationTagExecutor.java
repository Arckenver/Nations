package com.arckenver.nations.cmdexecutor.nation;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import static org.spongepowered.api.util.SpongeApiTranslationHelper.t;

public class NationTagExecutor implements CommandExecutor
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
			String tag = ctx.<String>getOne("tag").get();
			if (!player.hasPermission("nations.command.nation.tag"))
			{
				player.sendMessage(t("You do not have permission to use this command!"));
				return CommandResult.success();
			}
			if (!tag.matches("[a-zA-Z0-9]{1,}") | tag.length() < 2 || tag.length() > 4)
			{
				//TODO set the lang better here and just below
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EM));
				return CommandResult.success();
			}
			nation.setTag(tag);
			DataHandler.saveNation(nation.getUUID());
			src.sendMessage(Text.of(TextColors.GOLD, "Nation tag set to " + tag));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
