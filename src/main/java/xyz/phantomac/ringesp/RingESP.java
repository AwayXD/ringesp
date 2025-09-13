package xyz.phantomac.ringesp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.opengl.GL11;
import xyz.phantomac.ringesp.utils.ChatUtils;
import xyz.phantomac.ringesp.utils.RenderSystem;

import java.awt.Color;

@Mod(modid = "ringesp", name = "RingESP", version = "1.0", clientSideOnly = true)

public class RingESP {

    private final Minecraft mc = Minecraft.getMinecraft();

    // gl logic is mostly skidded from my project Sigma Rebase - thanks sigma devs lmao

    private boolean enabled = true;
    private long lastSwingTime = 0L;
    private final long pain = 1000;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new ToggleCommand());
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (!enabled) return;

        if (mc.thePlayer.swingProgress > 0) {
            lastSwingTime = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - lastSwingTime > pain) {
            return;
        }


        EntityPlayerSP self = mc.thePlayer;
        EntityPlayer closest = null;
        double closestDist = Double.MAX_VALUE;

        for (EntityPlayer p : mc.theWorld.playerEntities) {
            if (p == self || p.isDead) continue;
            double dist = self.getDistanceSqToEntity(p);
            if (dist < closestDist && dist <= 16.0) {
                closestDist = dist;
                closest = p;
            }
        }

        if (closest != null) {
            renderTargetESP(closest, event.partialTicks);
        }
    }

    private void renderTargetESP(Entity targetEntity, float partialTicks) {
        if (targetEntity == null || !targetEntity.isEntityAlive()) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glLineWidth(1.4F);

        double interpolatedX = targetEntity.lastTickPosX + (targetEntity.posX - targetEntity.lastTickPosX) * partialTicks;
        double interpolatedY = targetEntity.lastTickPosY + (targetEntity.posY - targetEntity.lastTickPosY) * partialTicks;
        double interpolatedZ = targetEntity.lastTickPosZ + (targetEntity.posZ - targetEntity.lastTickPosZ) * partialTicks;

        double cameraX = mc.getRenderManager().viewerPosX;
        double cameraY = mc.getRenderManager().viewerPosY;
        double cameraZ = mc.getRenderManager().viewerPosZ;

        GL11.glTranslated(interpolatedX - cameraX, interpolatedY - cameraY, interpolatedZ - cameraZ);

        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

        long cycleDurationMs = 1425;
        float glowProgress = (float) (System.currentTimeMillis() % cycleDurationMs) / cycleDurationMs;
        boolean isFadingOut = glowProgress > 0.5F;
        glowProgress = !isFadingOut ? glowProgress * 2.0F : 1.0F - (glowProgress * 2.0F % 1.0F);

        GL11.glTranslatef(0.0F, (targetEntity.height + 0.35F) * glowProgress, 0.0F);

        float glowFactor = (float) Math.sin(glowProgress * Math.PI);

        drawCircle(isFadingOut, 0.45F * glowFactor, 0.6F, 0.35F * glowFactor, 1.0F);

        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    private void drawCircle(boolean isFadingOut, float circleHeight, float radius, float glowStrength, float glowOpacity) {
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBegin(GL11.GL_QUAD_STRIP);

        int angleStep = (int) (360.0F / (40.0F * radius));

        Color espColor = Color.WHITE;
        float red = espColor.getRed() / 255.0F;
        float green = espColor.getGreen() / 255.0F;
        float blue = espColor.getBlue() / 255.0F;

        float alphaTop = glowStrength * glowOpacity;
        float alphaBottom = 0.0F;

        float topY = 0.0F;
        float bottomY = isFadingOut ? circleHeight : -circleHeight;

        for (int angle = 0; angle <= 360 + angleStep; angle += angleStep) {
            int effectiveAngle = angle > 360 ? 0 : angle;

            double x = Math.sin(Math.toRadians(effectiveAngle)) * radius;
            double z = Math.cos(Math.toRadians(effectiveAngle)) * radius;

            GL11.glColor4f(red, green, blue, alphaBottom);
            GL11.glVertex3d(x, bottomY, z);

            GL11.glColor4f(red, green, blue, alphaTop);
            GL11.glVertex3d(x, topY, z);
        }

        GL11.glEnd();

        GL11.glLineWidth(2.2F);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int angle = 0; angle <= 360 + angleStep; angle += angleStep) {
            int effectiveAngle = angle > 360 ? 0 : angle;

            double x = Math.sin(Math.toRadians(effectiveAngle)) * radius;
            double z = Math.cos(Math.toRadians(effectiveAngle)) * radius;

            float alpha = (0.5F + 0.5F * glowStrength) * glowOpacity;
            GL11.glColor4f(red, green, blue, alpha);
            GL11.glVertex3d(x, topY, z);
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderSystem.shadeModel(GL11.GL_FLAT);
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
            enabled = !enabled;
            ChatUtils.sendMessage(ChatUtils.getTagAwayTils() + "tehe its " + (enabled ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled"));;
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }
    }
}
