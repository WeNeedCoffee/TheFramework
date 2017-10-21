package com.austinv11.collectiveframework.minecraft.client.gui;

import com.austinv11.collectiveframework.minecraft.CollectiveFramework;
import com.austinv11.collectiveframework.minecraft.config.ConfigException;
import com.austinv11.collectiveframework.minecraft.config.ConfigRegistry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class CfGuiFactory {
    public static class GuiConfig extends net.minecraftforge.fml.client.config.GuiConfig {
        private Object config;

        GuiConfig(GuiScreen parentScreen, String modid, String title, Object config) {
            super(parentScreen,
                    getCategories(config, modid),
                    modid,
                    false,
                    false,
                    title);
            titleLine2 = ConfigRegistry.getFilePath(config);
            this.config = config;
        }

        private static List<IConfigElement> getCategories(Object config, String modid) {
            try {
                return ConfigRegistry.getCategories(config, modid);
            } catch (ConfigException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

        @Override
        public void onGuiClosed() {
            try {
                ConfigRegistry.onGuiClosed(config, entryList.listEntries);
            } catch (ConfigException e) {
                CollectiveFramework.LOGGER.warn("Failed to save config: " + e.getLocalizedMessage());
            }
        }
    }
}
