package dev.antimoxs.LabyChatReply;

import dev.antimoxs.LabyChatReply.LabyChatReply;
import net.labymod.api.events.MessageSendEvent;
import net.labymod.labyconnect.LabyConnect;
import net.labymod.labyconnect.log.MessageChatComponent;
import net.labymod.labyconnect.packets.PacketMessage;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import tv.twitch.chat.Chat;

public class OnMessageSendListener implements MessageSendEvent {

    LabyChatReply LabyChatReply;

    public OnMessageSendListener(LabyChatReply LabyChatReply) {

        this.LabyChatReply = LabyChatReply;

    }

    @Override
    public boolean onSend(String s) {


        if (s.startsWith("/" + LabyChatReply.lmcSyntax.trim() + " ")) {

            String[] msg = s.split(" ");
            int synlen = LabyChatReply.lmcSyntax.split(" ").length;

            if (msg.length < 2 + synlen) return true;

            for (ChatUser u : LabyMod.getInstance().getLabyConnect().getFriends()) {

                if (msg[synlen].equalsIgnoreCase(u.getGameProfile().getName())) {

                    String text = s.replaceFirst(msg[0], "").replaceFirst(msg[1],"").trim();
                    sendLMCMessage(u, text);
                    return true;

                }

            }

            LabyChatReply.sendIngameString("§cSorry, we can't find the user with name '" + msg[1] + "'.");
            return true;

        }
        else if (s.startsWith("/" + LabyChatReply.lmrSyntax.trim() + " ")) {

            if (LabyChatReply.lastUser == null) {

                LabyChatReply.sendIngameString("§cSorry, you don't have any recent conversations.");
                return true;

            }

            String[] msg = s.split(" ");
            int synlen = LabyChatReply.lmrSyntax.split(" ").length;
            ChatUser u = LabyChatReply.lastUser;

            if (msg.length < 1 + synlen) return true;

                String text = s.substring(LabyChatReply.lmrSyntax.length() + 1).trim();
                sendLMCMessage(u, text);
                return true;

        }

        return false;

    }

    public void sendLMCMessage(ChatUser u, String text) {

        ChatUser me = LabyMod.getInstance().getLabyConnect().getClientProfile().buildClientUser();
        LabyConnect c = LabyMod.getInstance().getLabyConnect();
        LabyChatReply.lastUser = u;

        Minecraft.getMinecraft().addScheduledTask(() -> {

            c.getChatlogManager().getChat(u).addMessage(new MessageChatComponent(me.getGameProfile().getName(), System.currentTimeMillis(), text));
            c.getChatlogManager().saveChatlogs(me.getGameProfile().getId());
            LabyChatReply.sendIngameString("§f§l[" + me.getGameProfile().getName() + "§f§l] -> [" + u.getGameProfile().getName() + "§f§l]: §7" + text);

        });

    }

}
