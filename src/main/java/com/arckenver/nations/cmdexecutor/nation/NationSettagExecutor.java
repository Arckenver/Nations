package com.arckenver.nations.cmdexecutor.nation;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Nation;

public class NationSettagExecutor implements CommandExecutor
{
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
			if (!nation.isStaff(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_NATIONSTAFF));
				return CommandResult.success();
			}
			String newTag = null;
			if (ctx.<String>getOne("tag").isPresent())
				newTag = ctx.<String>getOne("tag").get();
			if (newTag != null && DataHandler.getNation(newTag) != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NAMETAKEN));
				return CommandResult.success();
			}
			if (newTag != null && DataHandler.getNationByTag(newTag) != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_TAGTAKEN));
				return CommandResult.success();
			}
			if (newTag != null && !newTag.matches("[\\p{Alnum}\\p{IsIdeographic}\\p{IsLetter}\"_\"]*"))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_TAGALPHA));
				return CommandResult.success();
			}
			if (newTag != null && (newTag.length() < ConfigHandler.getNode("others", "minNationTagLength").getInt() || newTag.length() > ConfigHandler.getNode("others", "maxNationTagLength").getInt()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_TAGLENGTH
						.replaceAll("\\{MIN\\}", ConfigHandler.getNode("others", "minNationTagLength").getString())
						.replaceAll("\\{MAX\\}", ConfigHandler.getNode("others", "maxNationTagLength").getString())));
				return CommandResult.success();
			}
			String oldName = nation.getTag();
			nation.setTag(newTag);
			DataHandler.saveNation(nation.getUUID());
			MessageChannel.TO_ALL.send(Text.of(TextColors.AQUA, LanguageHandler.INFO_TAG
					.replaceAll("\\{NAME\\}", nation.getName())
					.replaceAll("\\{OLDTAG\\}", oldName)
					.replaceAll("\\{NEWTAG\\}", nation.getTag())));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
