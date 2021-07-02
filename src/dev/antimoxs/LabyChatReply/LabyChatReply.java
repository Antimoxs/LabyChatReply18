package dev.antimoxs.LabyChatReply;

import net.labymod.api.LabyModAddon;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.main.LabyMod;
import net.labymod.settings.Settings;
import net.labymod.settings.elements.*;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LabyChatReply extends LabyModAddon {

    public ChatUser lastUser = null;
    private String version = "1.2";
    private String lmcSyntax = "lmc";
    private String lmrSyntax = "lmc";
    public boolean msgToggl = true;
    public boolean cfcToggl = true;
    public boolean cfcUpdate = true;
    private String cfcStorageS = "";
    private HashMap<String, String> cfcStorage = new HashMap<>();
    private boolean updateCheck = false;

    @Override
    public void onEnable() {

        LabyMod.getInstance().getEventManager().register(new OnMessageSendListener(this));

    }

    @Override
    public void loadConfig() {

        loadConfig(0);

    }

    private void loadConfig(int i) {

        checkConfig("LCR_LMC_SYNTAX", "lmc");
        checkConfig("LCR_LMR_SYNTAX", "lmr");
        checkConfig("LCR_MSG_TOGGLE", true);
        checkConfig("LCR_CFC_TOGGLE", true);
        checkConfig("LCR_CFC_STORAGE", "");


        if (updateCheck) {

            updateCheck = false;
            System.out.println("[LabyChatReply] Some properties updated, reloading!");
            loadConfig();
            return;

        }

        try {

            this.lmcSyntax = getConfig().get("LCR_LMC_SYNTAX").getAsString();
            this.lmrSyntax = getConfig().get("LCR_LMR_SYNTAX").getAsString();
            this.msgToggl = getConfig().get("LCR_MSG_TOGGLE").getAsBoolean();
            this.cfcToggl = getConfig().get("LCR_CFC_TOGGLE").getAsBoolean();

            loadCFC();

        } catch (Exception e) {

            System.err.println("[LabyChatReply] Can't load one of the fields, retrying. If this keeps happening restart your client.");

            if (i >= 10) {

                System.err.println("[LabyChatReply] Sorry, we are unable to load the connfig file. Stacktrace:");
                e.printStackTrace();
                return;

            }

            loadConfig(i+1);

        }


        System.out.println("[LabyChatReply] Config loaded.");

    }

    public void loadCFC() {

        this.cfcStorageS = getConfig().get("LCR_CFC_STORAGE").getAsString();



        try {

            HashMap<String, String> cfcCacheMap = new HashMap<>();

            String[] splitter = this.cfcStorageS.split(";");
            for (String s : splitter) {

                String name = s.split(" ")[0];
                String cmmd = s.substring(name.length() + 1);

                cfcCacheMap.put(name, cmmd);
                System.out.println("[LabyChatReply] Indexed custom syntax for '" + name + "': " + cmmd);

            }

            if (!this.cfcStorage.isEmpty()) this.cfcStorage.clear();

            this.cfcStorage = cfcCacheMap;
            this.cfcUpdate = false;

        }
        catch (Exception e) {



        }



    }

    private void saveCFC(String input) {

        getConfig().remove("LCR_CFC_STORAGE");
        getConfig().addProperty("LCR_CFC_STORAGE", input);
        cfcUpdate = true;

    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {

        HeaderElement title = new HeaderElement("LabyChatReply by Antimoxs - v." + version);
        StringElement lmcSyntax = new StringElement("Chat message syntax", this, new ControlElement.IconData(Material.PAPER), "LCR_LMC_SYNTAX", this.lmcSyntax);
        StringElement lmrSyntax = new StringElement("QuickChat syntax", this, new ControlElement.IconData(Material.ARROW), "LCR_LMR_SYNTAX", this.lmrSyntax);
        BooleanElement msgToggle = new BooleanElement("Toggle message output", this, new ControlElement.IconData(Material.LEVER), "LCR_MSG_TOGGLE", this.msgToggl);
        BooleanElement cfcToggle = new BooleanElement("Toggle custom friend commands", this, new ControlElement.IconData(Material.SKULL), "LCR_CFC_TOGGLE", this.cfcToggl);
        //HeaderElement upnext = new HeaderElement("Up next: Custom commands for direct messages");

        HeaderElement cfc_note = new HeaderElement("§lCustom friend commands");
        HeaderElement cfc_note1 = new HeaderElement("§oSyntax: '<Name> <cmd>;<Name> <cmd>;...'");
        StringElement cfc_input = new StringElement("Custom syntax input", new ControlElement.IconData(Material.MAP), cfcStorageS, new Consumer<String>() {
            @Override
            public void accept(String s) {

                saveCFC(s);

            }
        });
        ButtonElement cfc_clear = new ButtonElement("Reload syntax-list", new ControlElement.IconData(Material.REDSTONE), new Consumer<ButtonElement>() {
            @Override
            public void accept(ButtonElement buttonElement) {

                loadCFC();

            }
        }, "Reload", "Reload the current syntax-list.", Color.RED);

        Settings cfc_sub = new Settings();
        cfc_sub.add(cfc_note);
        cfc_sub.add(cfc_note1);
        cfc_sub.add(cfc_clear);
        cfc_sub.add(cfc_input);

        cfcToggle.setSubSettings(cfc_sub);

        list.add(title);
        list.add(lmcSyntax);
        list.add(lmrSyntax);
        list.add(msgToggle);
        list.add(cfcToggle);
        //list.add(upnext);


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

    public String getLmcSyntax() { return this.lmcSyntax; }
    public String getLmrSyntax() { return this.lmrSyntax; }
    public HashMap<String, String> getStorage() { return this.cfcStorage; }




}
