package com.austinv11.collectiveframework.minecraft.utils;

import com.austinv11.collectiveframework.language.TranslationManager;
import com.austinv11.collectiveframework.language.translation.TranslationException;
import com.austinv11.collectiveframework.minecraft.reference.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.Locale;

/**
 * Translation Manager for use in Minecraft
 */
@SideOnly(Side.CLIENT)
public class MinecraftTranslator {
	
	/**
	 * Translates a given text (either from the unlocalized key or from standard text) (prefers Minecraft's translation)
	 * @param text String to translate
	 * @param toLang Language to translate to
	 * @return The translated text
	 * @throws TranslationException
	 * @throws IOException
	 */
	public static String translate(String text, String toLang) throws IOException, TranslationException {
		if (I18n.hasKey(text)) {
			return I18n.format(text);
		}
		String toTranslate = I18n.format(text);
		return TranslationManager.translate(toTranslate, toLang);
	}
	
	/**
	 * Translates a given text (either from the unlocalized key or from standard text) (prefers Minecraft's translation)
	 * @param text String to translate
	 * @param fromLang Language the string is from
	 * @param toLang Language to translate to
	 * @return The translated text
	 * @throws TranslationException
	 * @throws IOException
	 */
	public static String translate(String text, String fromLang, String toLang) throws TranslationException, IOException {
		if (I18n.hasKey(text)) {
			return I18n.format(text);
		}
		String toTranslate = I18n.format(text);
		return TranslationManager.translate(toTranslate, fromLang, toLang);
	}
	
	/**
	 * Simplified method to translate a string to the local language for Minecraft
	 * @param text String to translate
	 * @param fromLang Language of the string to translate
	 * @return The translated string
	 * @throws com.austinv11.collectiveframework.language.translation.TranslationException
	 * @throws java.io.IOException
	 */
	public static String translateToLocal(String text, String fromLang) throws TranslationException, IOException {
		return translate(text, fromLang, langToUsable());
	}
	
	/**
	 * Simplified method to translate a string to the local language for Minecraft
	 * @param text String to translate
	 * @return The translated string
	 * @throws TranslationException
	 * @throws IOException
	 */
	public static String translateToLocal(String text) throws TranslationException, IOException {
		return translate(text, langToUsable());
	}
	
	/**
	 * Gets the usable language key for the local language from Minecraft
	 * @return The key
	 */
	public static String langToUsable() {
		return mcLangCodesToUsable(Minecraft.getMinecraft().gameSettings.language);
	}
	
	/**
	 * Gets the usable language key for the given Minecraft language code
	 * @param code The Minecraft language code
	 * @return The usable key
	 */
	public static String mcLangCodesToUsable(String code) {
		String[] langInfo = code.split("_");
		Locale loc = new Locale(langInfo[0], langInfo[1]);
		return loc.getLanguage();
	}
	
	@SubscribeEvent
	public void onTooltipEvent(ItemTooltipEvent event) {
		if (Config.translateItems)
			try {
					if (I18n.format(event.getItemStack().getTranslationKey())
							.equals(event.getItemStack().getDisplayName())) {
						String toTranslate = event.getItemStack().getDisplayName();
						event.getItemStack().setStackDisplayName(translateToLocal(toTranslate, "en"));
					}
			} catch (Exception ignore) {}
		
	}
	
	@SubscribeEvent
	public void onChatEvent(ClientChatReceivedEvent event) {
		if (Config.translateChat)
			if (!event.isCanceled())
				try {
					StringBuilder message = new StringBuilder();
					for (String part : event.getMessage().getUnformattedText().split(" "))
						message.append(I18n.format(part)).append(" ");
					event.setMessage(new TextComponentString(translateToLocal(message.toString(), "en")));
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
}
