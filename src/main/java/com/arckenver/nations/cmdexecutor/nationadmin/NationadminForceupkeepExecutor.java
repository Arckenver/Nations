package com.arckenver.nations.cmdexecutor.nationadmin;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.object.Nation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.event.command.TabCompleteEvent;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class NationadminForceupkeepExecutor implements CommandExecutor {
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {
		if (ConfigHandler.getNode("prices").getNode("upkeepPerCitizen").getDouble() == 0 && DataHandler.getNations().values().stream().allMatch(n -> n.getTaxes() == 0)) {
			return CommandResult.success();
		}
		MessageChannel.TO_ALL.send(Text.of(TextColors.AQUA, LanguageHandler.CL));
		ArrayList<UUID> nationsToRemove = new ArrayList<UUID>();
		for (Nation nation : DataHandler.getNations().values()) {
			if (NationsPlugin.getEcoService() == null) {
				NationsPlugin.getLogger().error(LanguageHandler.DC);
				continue;
			}
			Optional<Account> optAccount = NationsPlugin.getEcoService().getOrCreateAccount("nation-" + nation.getUUID().toString());
			if (!optAccount.isPresent()) {
				NationsPlugin.getLogger().error("Nation " + nation.getName() + " doesn't have an account on the economy plugin of this server");
				continue;
			}
			// nation taxes
			BigDecimal taxes = BigDecimal.valueOf(nation.getTaxes());
			ArrayList<UUID> citizensToRemove = new ArrayList<UUID>();
			for (UUID uuid : nation.getCitizens()) {
				if (!nation.isStaff(uuid)) {
					Optional<UniqueAccount> optCitizenAccount = NationsPlugin.getEcoService().getOrCreateAccount(uuid);
					TransactionResult result = optCitizenAccount.get().transfer(optAccount.get(), NationsPlugin.getEcoService().getDefaultCurrency(), taxes, NationsPlugin.getCause());
					if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
						citizensToRemove.add(uuid);
						Sponge.getServer().getPlayer(uuid).ifPresent(p ->
								p.sendMessage(Text.of(TextColors.RED, LanguageHandler.HQ)));
					} else if (result.getResult() != ResultType.SUCCESS) {
						NationsPlugin.getLogger().error("Error while taking taxes from player " + uuid.toString() + " for nation " + nation.getName());
					}
				}
			}
			for (UUID uuid : citizensToRemove) {
				nation.removeCitizen(uuid);
			}
			// nation upkeep
			BigDecimal upkeep = BigDecimal.valueOf(nation.getUpkeep());

			TransactionResult result = optAccount.get().withdraw(NationsPlugin.getEcoService().getDefaultCurrency(), upkeep, NationsPlugin.getCause());

			if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
				nationsToRemove.add(nation.getUUID());
			} else if (result.getResult() != ResultType.SUCCESS) {
				NationsPlugin.getLogger().error("Error while taking upkeep from nation " + nation.getName());
			} else {
				if (ConfigHandler.getNode("economy", "serverAccount").getString() != null) {
					String serverAccount = ConfigHandler.getNode("economy", "serverAccount").getString();
					Optional<Account> optServerAccount = NationsPlugin.getEcoService().getOrCreateAccount(serverAccount);
					TransactionResult resultServer = optServerAccount.get().deposit(NationsPlugin.getEcoService().getDefaultCurrency(), upkeep, NationsPlugin.getCause());

					if (resultServer.getResult() != ResultType.SUCCESS) {
						NationsPlugin.getLogger().error("Error Giving money to SERVER account");
					}
				}
			}
		}
		for (UUID uuid : nationsToRemove) {
			String name = DataHandler.getNation(uuid).getName();
			DataHandler.removeNation(uuid);
			MessageChannel.TO_ALL.send(Text.of(TextColors.RED, LanguageHandler.CM.replaceAll("\\{NATION\\}", name)));
		}
		return CommandResult.success();
	}
}