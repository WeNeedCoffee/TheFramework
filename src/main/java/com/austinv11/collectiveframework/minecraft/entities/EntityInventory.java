package com.austinv11.collectiveframework.minecraft.entities;

import com.austinv11.collectiveframework.minecraft.tiles.TileEntityInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import scala.actors.threadpool.Arrays;

import java.util.List;

/**
 * An entity implementation with an inventory
 */
public abstract class EntityInventory extends Entity implements IInventory {

	public List<ItemStack> items;

	public EntityInventory(World world) {
		super(world);
		items = Arrays.asList(new ItemStack[getSizeInventory()]);
	}

	@Override
	protected void entityInit() {}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		NBTTagList itemsNbt = tag.getTagList("items", Constants.NBT.TAG_COMPOUND);
		items = Arrays.asList(new ItemStack[getSizeInventory()]);
		for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
			NBTTagCompound itemNbt = itemsNbt.getCompoundTagAt(itemIndex);
			items.set(itemIndex, ItemStack.loadItemStackFromNBT(itemNbt));
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		NBTTagList itemsNbt = new NBTTagList();
		for (ItemStack item : items) {
			NBTTagCompound itemNbt = new NBTTagCompound();
			if (item != null)
				item.writeToNBT(itemNbt);
			itemsNbt.appendTag(itemNbt);
		}
		tag.setTag("items", itemsNbt);
	}

	@Override
	public abstract int getSizeInventory();

	@Override
	public ItemStack getStackInSlot(int slot) {
		return items.get(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return TileEntityInventory.decrStackSizeStatic(this, items, slot, amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (items.size() > slot) {
			if (this.items.get(slot) != null) {
				ItemStack item = this.items.get(slot);
				this.items.set(slot, null);
				return item;
			}
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		TileEntityInventory.setInventorySlotContentsStatic(this, items, slot, stack);
	}

	@Override
	public abstract String getInventoryName();

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public abstract int getInventoryStackLimit();

	@Override
	public void markDirty() {
		for (int i = 0; i < items.size(); i++)
			if (items.get(i) != null && items.get(i).stackSize < 1)
				items.set(i, null);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public abstract boolean isItemValidForSlot(int slot, ItemStack stack);

	@Override
	public abstract boolean interactFirst(EntityPlayer player);
}
