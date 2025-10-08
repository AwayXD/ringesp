package xyz.phantomac.ringesp.gl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import xyz.phantomac.ringesp.manager.ModManager;

import java.awt.Color;

public class SimsESP implements ModManager.ModLifecycle {

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    private final Minecraft mc = Minecraft.getMinecraft();

    public void drawTriangle(float r, float g, float b) {
        GL11.glColor3f(r, g, b);
        GL11.glTranslatef(0.0F, 0.0F, 0.25F);
        GL11.glNormal3f(0.0F, 0.0F, 1.0F);
        GL11.glRotated(-30.0, 1.0, 0.0, 0.0);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2f(0.0F, 0.5F);
        GL11.glVertex2f(-0.105F, 0.0F);
        GL11.glVertex2f(0.105F, 0.0F);
        GL11.glEnd();
    }

    public void drawSimsPlumbob() {
        Color[] colors = new Color[]{
                new Color(136, 217, 72),
                new Color(124, 189, 72),
                new Color(103, 181, 75),
                new Color(136, 217, 72),
                new Color(124, 189, 72),
                new Color(103, 181, 75),
                new Color(136, 217, 72),
                new Color(103, 181, 75)
        };

        for (int angle = 0; angle <= 315; angle += 45) {
            GL11.glPushMatrix();
            GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
            int i = angle / 45;
            Color c = colors[i];
            drawTriangle(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);
            GL11.glPopMatrix();
        }

        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        for (int angle = 0; angle <= 315; angle += 45) {
            GL11.glPushMatrix();
            GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            int i = angle / 45;
            Color base = colors[i];
            Color darker = new Color(
                    (int) (base.getRed() * 0.8F),
                    (int) (base.getGreen() * 0.8F),
                    (int) (base.getBlue() * 0.8F)
            );
            drawTriangle(darker.getRed() / 255F, darker.getGreen() / 255F, darker.getBlue() / 255F);
            GL11.glPopMatrix();
        }
    }

    public void renderPlumbob(double x, double y, double z, Entity entity) {
        GL11.glPushMatrix();

        RenderManager renderManager = mc.getRenderManager();
        double renderX = x - renderManager.viewerPosX;
        double renderY = y - renderManager.viewerPosY;
        double renderZ = z - renderManager.viewerPosZ;


        GL11.glTranslated(renderX, renderY, renderZ);
        GL11.glRotated((entity.ticksExisted % 180) * 2, 0.0, -1.0, 0.0);

        float bob = (entity.ticksExisted % 100) - 50;
        if (bob < 0) bob = -bob;
        GL11.glTranslated(0.0, 0.7F + bob / 500.0F, 0.0);

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        drawSimsPlumbob();

        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();

        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity != mc.thePlayer) {
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.partialTicks;
                double y = entity.lastTickPosY + entity.height + (entity.posY - entity.lastTickPosY) * event.partialTicks;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.partialTicks;

                renderPlumbob(x, y, z, entity);
            }
        }
    }
}
