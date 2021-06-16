package dev.antimoxs.LabyChatReply;

import net.labymod.api.LabyModAddon;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.settings.elements.StringElement;
import net.labymod.utils.Material;

import java.util.List;

public class LabyChatReply extends LabyModAddon {

    public ChatUser lastUser = null;
    private String version = "1.1";

    public String lmcSyntax = "lmc";
    public String lmrSyntax = "lmc";

    private boolean updateCheck = false;

    @Override
    public void onEnable() {

        LabyMod.getInstance().getEventManager().register(new OnMessageSendListener(this));

    }

    @Override
    public void loadConfig() {

        checkConfig("LCR_LMC_SYNTAX", "lmc");
        checkConfig("LCR_LMR_SYNTAX", "lmr");

        if (updateCheck) {

            updateCheck = false;
            System.out.println("[LabyChatReply] Some properties updated, reloading!");
            loadConfig();
            return;

        }

        try {

            this.lmcSyntax = getConfig().get("LCR_LMC_SYNTAX").getAsString();
            this.lmrSyntax = getConfig().get("LCR_LMR_SYNTAX").getAsString();

        } catch (Exception e) {

            System.err.println("[LabyChatReply] Can't load one of the fields, retrying. If this keeps happening restart your client.");
            loadConfig();

        }


        System.out.println("[LabyChatReply] Config loaded.");

    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {

        HeaderElement title = new HeaderElement("LabyChatReply by Antimoxs - v." + version);
        StringElement lmcSyntax = new StringElement("Chat message syntax", this, new ControlElement.IconData(Material.PAPER), "LCR_LMC_SYNTAX", this.lmcSyntax);
        StringElement lmrSyntax = new StringElement("QuickChat syntax", this, new ControlElement.IconData(Material.ARROW), "LCR_LMR_SYNTAX", this.lmrSyntax);
        HeaderElement upnext = new HeaderElement("Up next: Custom commands for direct messages");
        list.add(title);
        list.add(lmcSyntax);
        list.add(lmrSyntax);
        list.add(upnext);


    }

    public void sendIngameString(String text) {

        StringBuilder builder = new StringBuilder();
        builder.append("§f§l[LabyChat] §7");
        builder.append(text);
        builder.append("§r");

        api.displayMessageInChat(builder.toString());

    }

    private void checkConfig(String property, Object defaultValue) {

        //System.out.println("[LabyChatReply] Checking for '" + property + "'...");

        if (getConfig().has(property)) {

            //TBCLogger.log(TBCLoggingType.INFORMATION, config.name, "Loaded '" + property + "'");
            //System.out.println("[LabyChatReply] Loaded '" + property + "'.");

        } else {

            System.out.println("[LabyChatReply] Property '" + property + "' not yet in config, creating!");
            if (defaultValue instanceof String) {
                getConfig().addProperty(property, (String) defaultValue);
            } else if (defaultValue instanceof Integer) {
                getConfig().addProperty(property, (int) defaultValue);
            } else if (defaultValue instanceof Boolean) {
                getConfig().addProperty(property, (boolean) defaultValue);
            } else if (defaultValue instanceof Character) {
                getConfig().addProperty(property, (char) defaultValue);
            } else {
                System.err.println("[LabyChatReply] uhm this is not indented nor wanted. fix asap? " + property + " " + defaultValue);
            }
            updateCheck = true;

        }

    }




}
