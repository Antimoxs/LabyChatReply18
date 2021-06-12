package dev.antimoxs.LabyChatReply;

import net.labymod.api.LabyModAddon;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.SettingsElement;

import java.util.List;

public class LabyChatReply extends LabyModAddon {

    public ChatUser lastUser = null;
    private String version = "1.0";

    @Override
    public void onEnable() {
        LabyMod.getInstance().getEventManager().register(new OnMessageSendListener(this));
    }

    @Override
    public void loadConfig() {

    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {

        HeaderElement title = new HeaderElement("LabyChatReply by Antimoxs - v." + version);
        list.add(title);


    }

    public void sendIngameString(String text) {

        StringBuilder builder = new StringBuilder();
        builder.append("§f§l[LabyChat] §7");
        builder.append(text);
        builder.append("§r");

        api.displayMessageInChat(builder.toString());

    }




}
