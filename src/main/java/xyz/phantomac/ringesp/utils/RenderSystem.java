package xyz.phantomac.ringesp.utils;

import net.minecraft.client.renderer.GlStateManager;

public class RenderSystem {

    public static void shadeModel(int mode) {
        GlStateManager.shadeModel(mode);
    }

}
