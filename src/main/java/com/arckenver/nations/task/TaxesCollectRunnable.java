package com.arckenver.nations.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.object.Nation;

public class TaxesCollectRunnable implements Runnable
{
	public void run()
	{
		if (ConfigHandler.getNode("prices").getNode("upkeepPerCitizen").getDouble() == 0 && DataHandler.getNations().values().stream().allMatch(n -> n.getTaxes() == 0))
		{
			return;
		}
		if (NationsPlugin.getEcoService() == null)
		{
			NationsPlugin.getLogger().error(LanguageHandler.ERROR_NOECO);
			return;
		}

		EventContext context = EventContext.builder()
				.build();

		Cause cause = Cause.builder()
				.append(Sponge.getServer().getConsole())
				.append(NationsPlugin.getInstance())
				.build(context);

		MessageChannel.TO_ALL.send(Text.of(TextColors.AQUA, LanguageHandler.INFO_UPKEEPANNOUNCE));
		ArrayList<UUID> nationsToRemove = new ArrayList<UUID>();
		for (Nation nation : DataHandler.getNations().values())
		{
			if (nation.isAdmin())
				continue;
			Optional<Account> optAccount = NationsPlugin.getEcoService().getOrCreateAccount("nation-" + nation.getUUID().toString());
			if (!optAccount.isPresent())
			{
				NationsPlugin.getLogger().error("Nation " + nation.getName() + " doesn't have an account on the economy plugin of this server");
				continue;
			}
			// nation taxes
			BigDecimal taxes = BigDecimal.valueOf(nation.getTaxes());
			ArrayList<UUID> citizensToRemove = new ArrayList<UUID>();

			UserStorageService userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();

			for (UUID uuid : nation.getCitizens())
			{
				Optional<User> user = userStorage.get(uuid);
				if (user.isPresent() && user.get().hasPermission("nations.admin.nation.exempt"))
				{
					continue;
				}
				if (nation.isStaff(uuid)) {
					continue;
				}
				Optional<UniqueAccount> optCitizenAccount = NationsPlugin.getEcoService().getOrCreateAccount(uuid);

				TransactionResult result = optCitizenAccount.get().transfer(optAccount.get(), NationsPlugin.getEcoService().getDefaultCurrency(), taxes, cause);
				if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS)
				{
					citizensToRemove.add(uuid);
					Sponge.getServer().getPlayer(uuid).ifPresent(p ->
					p.sendMessage(Text.of(TextColors.RED, LanguageHandler.INFO_KICKUPKEEP)));
				}
				else if (result.getResult() != ResultType.SUCCESS)
				{
					MessageChannel.TO_CONSOLE.send(Text.of("Something bad happened: ", result.getResult()));
					NationsPlugin.getLogger().error("Error while taking taxes from player " + uuid.toString() + " for nation " + nation.getName());
				}
			}
			for (UUID uuid : citizensToRemove)
			{
				MessageChannel.TO_CONSOLE.send(Text.of("Removing player for nation (no funds): ", uuid));
				nation.removeCitizen(uuid);
			}
			// nation upkeep
			BigDecimal upkeep = BigDecimal.valueOf(nation.getUpkeep());
			TransactionResult result = optAccount.get().withdraw(NationsPlugin.getEcoService().getDefaultCurrency(), upkeep, cause);
			if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS)
			{
				nationsToRemove.add(nation.getUUID());
			}
			else if (result.getResult() != ResultType.SUCCESS)
			{
				MessageChannel.TO_CONSOLE.send(Text.of("Error occured: ", result.getResult()));
				NationsPlugin.getLogger().error("Error while taking upkeep from nation " + nation.getName());
			}
		}
		for (UUID uuid : nationsToRemove)
		{
			String name = DataHandler.getNation(uuid).getName();
			DataHandler.removeNation(uuid);
			MessageChannel.TO_ALL.send(Text.of(TextColors.RED, LanguageHandler.INFO_NATIONFAILUPKEEP.replaceAll("\\{NATION\\}", name)));
		}
	}
}
