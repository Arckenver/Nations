package com.arckenver.nations.task;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Pattern;

import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.object.Nation;

public class TaxesCollectRunnable implements Runnable
{
	public void run()
	{
		MessageChannel.TO_ALL.send(Text.of(TextColors.AQUA, LanguageHandler.CL));
		for (Nation nation : DataHandler.getNations().values())
		{
			if (NationsPlugin.getEcoService() == null)
			{
				NationsPlugin.getLogger().error(LanguageHandler.DC);
				continue;
			}
			Optional<Account> optAccount = NationsPlugin.getEcoService().getOrCreateAccount("nation-" + nation.getUUID().toString());
			if (!optAccount.isPresent())
			{
				NationsPlugin.getLogger().error("Nation " + nation.getName() + " doesn't have an account on the economy plugin of this server");
				continue;
			}
			BigDecimal upkeep = BigDecimal.valueOf(nation.getUpkeep());
			TransactionResult result = optAccount.get().withdraw(NationsPlugin.getEcoService().getDefaultCurrency(), upkeep, NationsPlugin.getCause());
			if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS)
			{
				String nationName = nation.getName();
				DataHandler.removeNation(nation.getUUID());
				MessageChannel.TO_ALL.send(Text.of(TextColors.RED, LanguageHandler.CM.replaceAll(Pattern.quote("\\{NATION\\}"), nationName)));
			}
			else if (result.getResult() != ResultType.SUCCESS)
			{
				NationsPlugin.getLogger().error("An error has occured while taking taxes from nation " + nation.getName());
			}
		}
		
	}
}
