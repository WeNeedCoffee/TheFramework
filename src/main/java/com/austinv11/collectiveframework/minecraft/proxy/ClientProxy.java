package com.austinv11.collectiveframework.minecraft.proxy;

import com.austinv11.collectiveframework.minecraft.client.gui.KeyOverlay;
import com.austinv11.collectiveframework.minecraft.event.handler.ClientTickHandler;
import com.austinv11.collectiveframework.minecraft.event.handler.KeyHandler;
import com.austinv11.collectiveframework.minecraft.event.handler.TooltipHandler;
import com.austinv11.collectiveframework.minecraft.init.Keybindings;
import com.austinv11.collectiveframework.minecraft.utils.MinecraftTranslator;
import com.austinv11.collectiveframework.minecraft.utils.ModelManager;
import com.austinv11.collectiveframework.minecraft.utils.TextureManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void prepareClient() {
		Keybindings.init();
	}
	
	@Override
	public void registerEvents() {
		super.registerEvents();
		MinecraftForge.EVENT_BUS.register(new TextureManager());
		MinecraftForge.EVENT_BUS.register(new ModelManager());
		MinecraftForge.EVENT_BUS.register(new KeyOverlay());
		MinecraftForge.EVENT_BUS.register(new KeyHandler());
		MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
		MinecraftForge.EVENT_BUS.register(new MinecraftTranslator());
		MinecraftForge.EVENT_BUS.register(new TooltipHandler());
	}
	
	@Override
	public Side getSide() {
		return Side.CLIENT;
	}
}
