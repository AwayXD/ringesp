package xyz.phantomac.ringesp;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import xyz.phantomac.ringesp.gl.SimsESP;
import xyz.phantomac.ringesp.manager.ModManager;
import xyz.phantomac.ringesp.utils.ChatUtils;
import xyz.phantomac.ringesp.gl.RingLogic;

import java.awt.Color;

@Mod(modid = "ringesp", name = "RingESP", version = "1.0", clientSideOnly = true)

public class RingESP {

    private final Minecraft mc = Minecraft.getMinecraft();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
     //   ClientCommandHandler.instance.registerCommand(new ToggleCommand());
        ClientCommandHandler.instance.registerCommand(new ModManager.UtilsCommand());
        MinecraftForge.EVENT_BUS.register(new ModManager());

    }



    private class ToggleCommand extends CommandBase {
        @Override
        public String getCommandName() {
            return "ringesp";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/ringesp - toggle ESP ring";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            RingLogic.enabled = !RingLogic.enabled;
            ChatUtils.sendMessage(ChatUtils.getTagAwayTils() + "tehe its " + (RingLogic.enabled ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled"));;
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }
    }
}
