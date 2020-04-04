package com.arckenver.nations.channel;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.object.Nation;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

public class AdminSpyMessageChannel extends NationMessageChannel {

    @Override
    public void send(Object sender, Text message){
        if(sender == null){
            super.send(null, message);
        }else{
            Nation senderNation = DataHandler.getNationOfPlayer(((Player) sender).getUniqueId());

            for(MessageReceiver receiver : getMembers()){
                final Nation receiverNation = DataHandler.getNationOfPlayer(((Player) receiver).getUniqueId());
                if(receiverNation == senderNation) continue;
                receiver.sendMessage(message);
            }
        }
    }

}
