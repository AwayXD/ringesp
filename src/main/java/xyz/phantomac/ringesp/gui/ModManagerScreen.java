package xyz.phantomac.ringesp.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import xyz.phantomac.ringesp.manager.ModManager;
import xyz.phantomac.ringesp.utils.ChatUtils;


import java.io.IOException;

public class ModManagerScreen extends GuiScreen {
    private final Minecraft mc = Minecraft.getMinecraft();
    private int buttonId = 0;

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        int y = this.height / 4;

        for (String modName : ModManager.getModStates().keySet()) {
            boolean enabled = ModManager.isModEnabled(modName);
            this.buttonList.add(new GuiButton(buttonId++, this.width / 2 - 100, y, 200, 20, modName + " :" + (enabled ? EnumChatFormatting.GREEN + " Enabled" : EnumChatFormatting.RED + " Disabled")));
            y += 24;
        }


        this.buttonList.add(new GuiButton(buttonId, this.width / 2 - 100, y, 200, 20, "Close"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id < ModManager.getModStates().size()) {
            String modName = (String) ModManager.getModStates().keySet().toArray()[button.id];
            ModManager.toggleMod(modName);
            button.displayString = modName + (ModManager.isModEnabled(modName) ? EnumChatFormatting.GREEN + " Enabled" : EnumChatFormatting.RED + " Disabled");
        } else if (button.id == buttonList.size() - 1) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, ChatUtils.getTagAwayTils(), this.width / 2, 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}