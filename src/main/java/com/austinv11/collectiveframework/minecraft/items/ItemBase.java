package com.austinv11.collectiveframework.minecraft.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Simple implementation of an item
 */
public abstract class ItemBase extends Item {
	
	public ItemBase() {
		super();
		this.setMaxStackSize(64);
		if (getTab() != null)
			this.setCreativeTab(getTab());
	}
	
	/**
	 * Gets the creative tab this item belongs to
	 * @return The tab (it can be null)
	 */
	public abstract CreativeTabs getTab();
	
	/**
	 * Gets the modid to which the block is associated to
	 * @return The modid
	 */
	public abstract String getModId();
	
}
