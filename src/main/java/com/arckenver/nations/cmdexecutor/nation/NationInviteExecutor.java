package com.arckenver.nations.cmdexecutor.nation;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Request;

public class NationInviteExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player hostPlayer = (Player) src;
			if (!ctx.<Player>getOne("player").isPresent())
			{
				src.sendMessage(Text.of(TextColors.YELLOW, "/n invite <player>"));
				return CommandResult.success();
			}
			Nation nation = DataHandler.getNationOfPlayer(hostPlayer.getUniqueId());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CI));
				return CommandResult.success();
			}
			if (!nation.isStaff(hostPlayer.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CK));
				return CommandResult.success();
			}
			Player guestPlayer = ctx.<Player>getOne("player").get();
			
			if (nation.isCitizen(guestPlayer.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EW));
				return CommandResult.success();
			}
			
			Request req = DataHandler.getInviteRequest(nation.getUUID(), guestPlayer.getUniqueId());
			if (req != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EX));
				return CommandResult.success();
			}
			req = DataHandler.getJoinRequest(nation.getUUID(), guestPlayer.getUniqueId());
			if (req != null)
			{
				DataHandler.removeJoinRequest(req);
				for (UUID uuid : nation.getCitizens())
				{
					Optional<Player> optPlayer = Sponge.getServer().getPlayer(uuid);
					if (optPlayer.isPresent())
						optPlayer.get().sendMessage(Text.of(TextColors.AQUA, LanguageHandler.EY.replaceAll("\\{PLAYER\\}", guestPlayer.getName())));
				}
				nation.addCitizen(guestPlayer.getUniqueId());
				guestPlayer.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.EZ.replaceAll("\\{NATION\\}", nation.getName())));
				DataHandler.saveNation(nation.getUUID());;
				return CommandResult.success();
			}
			DataHandler.addInviteRequest(new Request(nation.getUUID(), guestPlayer.getUniqueId()));

			String str = LanguageHandler.FA.replaceAll("\\{NATION\\}", nation.getName());
			guestPlayer.sendMessage(Text.builder()
					.append(Text.of(TextColors.AQUA, str.split("\\{CLICKHERE\\}")[0]))
					.append(Text.builder(LanguageHandler.JA)
							.onClick(TextActions.runCommand("/nation join " + nation.getRealName()))
							.color(TextColors.DARK_AQUA)
							.build())
					.append(Text.of(TextColors.AQUA, str.split("\\{CLICKHERE\\}")[1])).build());

			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.FB.replaceAll("\\{RECEIVER\\}", guestPlayer.getName())));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
