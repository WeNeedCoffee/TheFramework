package com.austinv11.collectiveframework.minecraft.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * Simple implementation for a Tile Entity to contain an inventory
 */
public abstract class TileEntityInventory extends TileEntity implements IInventory {

	public List<ItemStack> items;
	public String invName = "null";
	private int numUsingPlayers = 0;
	
	public TileEntityInventory() {
		items = Arrays.asList(new ItemStack[getSize()]);
	}
	
	/**
	 * This represents how many slots are in the inventory
	 * @return The number of slots
	 */
	public abstract int getSize();
	
	@Override
	public void updateEntity() {
		super.updateEntity();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTTagList itemsNbt = nbttagcompound.getTagList("items", Constants.NBT.TAG_COMPOUND);
		items = Arrays.asList(new ItemStack[getSize()]);
		for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
			NBTTagCompound itemNbt = itemsNbt.getCompoundTagAt(itemIndex);
			items.set(itemIndex, ItemStack.loadItemStackFromNBT(itemNbt));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		NBTTagList itemsNbt = new NBTTagList();
		for (ItemStack item : items) {
			NBTTagCompound itemNbt = new NBTTagCompound();
			if (item != null)
				item.writeToNBT(itemNbt);
			itemsNbt.appendTag(itemNbt);
		}
		nbttagcompound.setTag("items", itemsNbt);
	}

	@Override
	public int getSizeInventory() {
		return getSize();
	}

	@Override
	@Nullable
	public ItemStack getStackInSlot(int slot) {
		if (items.size() > slot)
			return items.get(slot);
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return decrStackSizeStatic(this, items, slot, amount);
	}

	@Nullable
	public static ItemStack decrStackSizeStatic(IInventory inventory, List<ItemStack> items, int slot,
												int amount) {
		if (items.size() > slot) {
			if (items.get(slot) != null) {
				if (items.get(slot).stackSize <= amount) {
					ItemStack item = items.get(slot);
					items.set(slot, null);
					inventory.markDirty();
					return item;
				}
				ItemStack item = items.get(slot).splitStack(amount);
				if (items.get(slot).stackSize == 0) {
					items.set(slot, null);
				}
				inventory.markDirty();
				return item;
			}
		}
		return null;
	}

	@Nullable
	public static ItemStack removeStackFromSlotStatic(List<ItemStack> items, int slot) {
		if (items.size() > slot) {
			if (items.get(slot) != null) {
				ItemStack item = items.get(slot);
				items.set(slot, null);
				return item;
			}
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		setInventorySlotContentsStatic(this, items, slot, stack);
	}

	public static void setInventorySlotContentsStatic(IInventory inventory, List<ItemStack> items, int slot,
													  ItemStack stack) {
		if (items.size() > slot) {
			items.set(slot, stack);
			if (stack != null && stack.stackSize > inventory.getInventoryStackLimit()) {
				stack.stackSize = inventory.getInventoryStackLimit();
			}
			inventory.markDirty();
		}
	}

	@Override
	public String getInventoryName() {
		return invName;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if (worldObj == null) {
			return true;
		}
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		}
		return player.getDistanceSq((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openInventory() {
		numUsingPlayers++;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, this.getBlockType(), 1, numUsingPlayers);
	}

	@Override
	public void closeInventory() {
		numUsingPlayers--;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, this.getBlockType(), 1, numUsingPlayers);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}
	
	@Override
	public void markDirty() {
		for (int i = 0; i < items.size(); i++)
			if (items.get(i) != null && items.get(i).stackSize < 1)
				items.set(i, null);
		super.markDirty();
	}
}
