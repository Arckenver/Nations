package com.arckenver.nations.cmdexecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

public class NationJoinExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player guestPlayer = (Player) src;
			if (!ctx.<String>getOne("nation").isPresent())
			{
				src.sendMessage(Text.of(TextColors.YELLOW, "/n join <nation>"));
				return CommandResult.success();
			}
			if (DataHandler.getNationOfPlayer(guestPlayer.getUniqueId()) != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.EK));
				return CommandResult.success();
			}
			String nationName = ctx.<String>getOne("nation").get();
			Nation nation = DataHandler.getNation(nationName);
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CB));
				return CommandResult.success();
			}
			
			Request req = DataHandler.getJoinRequest(nation.getUUID(), guestPlayer.getUniqueId());
			if (req != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.FC));
				return CommandResult.success();
			}
			req = DataHandler.getInviteRequest(nation.getUUID(), guestPlayer.getUniqueId());
			if (req != null)
			{
				DataHandler.removeInviteRequest(req);
				for (UUID uuid : nation.getCitizens())
				{
					Optional<Player> optPlayer = Sponge.getServer().getPlayer(uuid);
					if (optPlayer.isPresent())
						optPlayer.get().sendMessage(Text.of(TextColors.GREEN, LanguageHandler.EY.replaceAll("\\{PLAYER\\}", guestPlayer.getName())));
				}
				nation.addCitizen(guestPlayer.getUniqueId());
				guestPlayer.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.EZ.replaceAll("\\{NATION\\}", nation.getName())));
				DataHandler.saveNation(nation.getUUID());
				return CommandResult.success();
			}
			ArrayList<UUID> nationStaff = nation.getStaff();
			List<Player> nationStaffPlayers = nationStaff
					.stream()
					.filter(uuid -> Sponge.getServer().getPlayer(uuid).isPresent())
					.map(uuid -> Sponge.getServer().getPlayer(uuid).get())
					.collect(Collectors.toList());
			
			if (nationStaffPlayers.isEmpty())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.FD));
				return CommandResult.success();
			}
			DataHandler.addJoinRequest(new Request(nation.getUUID(), guestPlayer.getUniqueId()));
			for (Player p : nationStaffPlayers)
			{
				String str = LanguageHandler.FE.replaceAll("\\{PLAYER\\}", guestPlayer.getName());
				p.sendMessage(Text.builder()
						.append(Text.of(TextColors.AQUA, str.split("\\{CLICKHERE\\}")[0]))
						.append(Text.builder(LanguageHandler.JA)
								.onClick(TextActions.runCommand("/nation invite " + guestPlayer.getName()))
								.color(TextColors.DARK_AQUA)
								.build())
						.append(Text.of(TextColors.AQUA, str.split("\\{CLICKHERE\\}")[1])).build());
			}
			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.FB.replaceAll("\\{RECEIVER\\}", nationName)));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
