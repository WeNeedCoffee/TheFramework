package com.austinv11.collectiveframework.minecraft;

import com.austinv11.collectiveframework.minecraft.client.gui.GuiHandler;
import com.austinv11.collectiveframework.minecraft.compat.modules.Modules;
import com.austinv11.collectiveframework.minecraft.config.ConfigException;
import com.austinv11.collectiveframework.minecraft.config.ConfigRegistry;
import com.austinv11.collectiveframework.minecraft.network.ConfigPacket;
import com.austinv11.collectiveframework.minecraft.network.TileEntityClientUpdatePacket;
import com.austinv11.collectiveframework.minecraft.network.TileEntityServerUpdatePacket;
import com.austinv11.collectiveframework.minecraft.network.TimeUpdatePacket;
import com.austinv11.collectiveframework.minecraft.proxy.CommonProxy;
import com.austinv11.collectiveframework.minecraft.reference.Config;
import com.austinv11.collectiveframework.minecraft.reference.Reference;
import com.austinv11.collectiveframework.multithreading.SimpleRunnable;
import com.austinv11.collectiveframework.utils.TimeProfiler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod(modid= Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, acceptableRemoteVersions = "*",
		guiFactory = Reference.GUI_FACTORY_CLASS)
public class CollectiveFramework {
	
	public static SimpleNetworkWrapper NETWORK;
	
	public static Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);
	
	@Mod.Instance(Reference.MOD_ID)
	public static CollectiveFramework instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOGGER = event.getModLog();
		TimeProfiler profiler = new TimeProfiler();
		try {
			ConfigRegistry.registerConfig(Config.INSTANCE);
		} catch (ConfigException e) {
			e.printStackTrace();
		}
		ConfigRegistry.init();
		proxy.registerEvents();
		Modules.init();
		SimpleRunnable.RESTRICT_THREAD_USAGE = Config.restrictThreadUsage;
		NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.NETWORK_NAME);
		Modules.propagate(event);
		LOGGER.info("Pre-Init took "+profiler.getTime()+"ms");
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		TimeProfiler profiler = new TimeProfiler();
		ConfigRegistry.init();
		proxy.prepareClient();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		Modules.propagate(event);
		LOGGER.info("Init took "+profiler.getTime()+"ms");
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		TimeProfiler profiler = new TimeProfiler();
		ConfigRegistry.init();
		NETWORK.registerMessage(TileEntityServerUpdatePacket.TileEntityServerUpdatePacketHandler.class, TileEntityServerUpdatePacket.class, 0, Side.SERVER);
		NETWORK.registerMessage(TileEntityClientUpdatePacket.TileEntityClientUpdatePacketHandler.class, TileEntityClientUpdatePacket.class, 1, Side.CLIENT);
		NETWORK.registerMessage(ConfigPacket.ConfigPacketHandler.class, ConfigPacket.class, 2, Side.CLIENT);
		NETWORK.registerMessage(TimeUpdatePacket.TimeUpdatePacketHandler.class, TimeUpdatePacket.class, 3, Side.SERVER);
		Modules.propagate(event);
		LOGGER.info("Post-Init took "+profiler.getTime()+"ms");
	}
	
	@SubscribeEvent
	public void onServerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		sendProxySyncToClient((EntityPlayerMP) event.player, false);
	}
	
	@SubscribeEvent
	public void onClientDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
		sendProxySyncToClient((EntityPlayerMP) event.player, true);
	}

	/**
	 * Send config data to a client
	 * @param player player to sent to
	 */
	private void sendProxySyncToClient(@Nullable EntityPlayerMP player, boolean isRevert) {
		if (FMLCommonHandler.instance().getSide() == Side.SERVER || FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			for (ConfigRegistry.ConfigProxy proxy : ConfigRegistry.configs) {
				if (proxy.doesSync && proxy.fileName != null && proxy.config != null & player != null) {
					String proxyString = proxy.handler.convertToString(proxy.config);
					if (proxyString == null)
						continue;
					try {
						NETWORK.sendTo(new ConfigPacket(proxy.fileName, proxyString, isRevert), player);
					} catch (Exception e) {
						LOGGER.throwing(e);
					}
				}
			}
		}
	}
}
