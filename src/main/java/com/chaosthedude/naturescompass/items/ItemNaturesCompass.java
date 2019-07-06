package com.chaosthedude.naturescompass.items;

import com.chaosthedude.naturescompass.NaturesCompass;
import com.chaosthedude.naturescompass.gui.GuiNaturesCompass;
import com.chaosthedude.naturescompass.network.PacketRequestSync;
import com.chaosthedude.naturescompass.util.BiomeUtils;
import com.chaosthedude.naturescompass.util.EnumCompassState;
import com.chaosthedude.naturescompass.util.ItemUtils;
import com.chaosthedude.naturescompass.util.SearchResult;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemNaturesCompass extends Item {

	public static final String NAME = "naturescompass";

	public ItemNaturesCompass() {
		super(new Properties().maxStackSize(1).group(ItemGroup.TOOLS));
        setRegistryName(NAME);

		addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
			@OnlyIn(Dist.CLIENT)
	        private double rotation;
	        @OnlyIn(Dist.CLIENT)
	        private double rota;
	        @OnlyIn(Dist.CLIENT)
	        private long lastUpdateTick;

	        @OnlyIn(Dist.CLIENT)
	        @Override
			public float call(ItemStack stack, World world, EntityLivingBase entityLiving) {
				if (entityLiving == null && !stack.isOnItemFrame()) {
					return 0.0F;
				} else {
					final boolean entityExists = entityLiving != null;
					final Entity entity = (Entity) (entityExists ? entityLiving : stack.getItemFrame());
					if (world == null) {
						world = entity.world;
					}

					double rotation = entityExists ? (double) entity.rotationYaw : getFrameRotation((EntityItemFrame) entity);
					rotation = rotation % 360.0D;
					double adjusted = Math.PI - ((rotation - 90.0D) * 0.01745329238474369D - getAngle(world, entity, stack));

					if (entityExists) {
						adjusted = wobble(world, adjusted);
					}

					final float f = (float) (adjusted / (Math.PI * 2D));
					return MathHelper.positiveModulo(f, 1.0F);
				}
			}

	        @OnlyIn(Dist.CLIENT)
			private double wobble(World world, double amount) {
				if (world.getGameTime() != lastUpdateTick) {
					lastUpdateTick = world.getGameTime();
					double d0 = amount - rotation;
					d0 = d0 % (Math.PI * 2D);
					d0 = MathHelper.clamp(d0, -1.0D, 1.0D);
					rota += d0 * 0.1D;
					rota *= 0.8D;
					rotation += rota;
				}

				return rotation;
			}

	        @OnlyIn(Dist.CLIENT)
			private double getFrameRotation(EntityItemFrame itemFrame) {
				return (double) MathHelper.wrapDegrees(180 + itemFrame.facingDirection.getHorizontalIndex() * 90);
			}

	        @OnlyIn(Dist.CLIENT)
			private double getAngle(World world, Entity entity, ItemStack stack) {
				BlockPos pos;
				if (getState(stack) == EnumCompassState.FOUND) {
					pos = new BlockPos(getFoundBiomeX(stack), 0, getFoundBiomeZ(stack));
				} else {
					pos = world.getSpawnPoint();
				}

				return Math.atan2((double) pos.getZ() - entity.posZ, (double) pos.getX() - entity.posX);
			}
		});
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!player.isSneaking()) {
			if (world.isRemote) {
				final ItemStack stack = ItemUtils.getHeldNatureCompass(player);
				NaturesCompass.network.sendToServer(new PacketRequestSync());
				Minecraft.getInstance().displayGuiScreen(new GuiNaturesCompass(world, player, stack, (ItemNaturesCompass) stack.getItem(), BiomeUtils.getAllowedBiomes()));
			}
			//player.openGui(NaturesCompass.instance, 0, world, 0, 0, 0);
			
		} else {
			setState(player.getHeldItem(hand), null, EnumCompassState.INACTIVE, player);
		}

		return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
	}

	public void searchForBiome(World world, EntityPlayer player, int biomeID, BlockPos pos, ItemStack stack) {
		setSearching(stack, biomeID, player);
		final SearchResult result = BiomeUtils.searchForBiome(world, stack, Biome.getBiome(biomeID, null), pos);
		if (result.found()) {
			setFound(stack, result.getX(), result.getZ(), result.getSamples(), player);
		} else {
			setNotFound(stack, player, result.getRadius(), result.getSamples());
		}
	}

	public boolean isActive(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return getState(stack) != EnumCompassState.INACTIVE;
		}

		return false;
	}

	public void setSearching(ItemStack stack, int biomeID, EntityPlayer player) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().setInt("BiomeID", biomeID);
			stack.getTag().setInt("State", EnumCompassState.SEARCHING.getID());
		}
	}

	public void setFound(ItemStack stack, int x, int z, int samples, EntityPlayer player) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().setInt("State", EnumCompassState.FOUND.getID());
			stack.getTag().setInt("FoundX", x);
			stack.getTag().setInt("FoundZ", z);
			stack.getTag().setInt("Samples", samples);
		}
	}

	public void setNotFound(ItemStack stack, EntityPlayer player, int searchRadius, int samples) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().setInt("State", EnumCompassState.NOT_FOUND.getID());
			stack.getTag().setInt("SearchRadius", searchRadius);
			stack.getTag().setInt("Samples", samples);
		}
	}

	public void setInactive(ItemStack stack, EntityPlayer player) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().setInt("State", EnumCompassState.INACTIVE.getID());
		}
	}

	public void setState(ItemStack stack, BlockPos pos, EnumCompassState state, EntityPlayer player) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().setInt("State", state.getID());
		}
	}

	public void setFoundBiomeX(ItemStack stack, int x, EntityPlayer player) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().setInt("FoundX", x);
		}
	}

	public void setFoundBiomeZ(ItemStack stack, int z, EntityPlayer player) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().setInt("FoundZ", z);
		}
	}

	public void setBiomeID(ItemStack stack, int biomeID, EntityPlayer player) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().setInt("BiomeID", biomeID);
		}
	}

	public void setSearchRadius(ItemStack stack, int searchRadius, EntityPlayer player) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().setInt("SearchRadius", searchRadius);
		}
	}

	public void setSamples(ItemStack stack, int samples, EntityPlayer player) {
		if (ItemUtils.verifyNBT(stack)) {
			stack.getTag().setInt("Samples", samples);
		}
	}

	public EnumCompassState getState(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return EnumCompassState.fromID(stack.getTag().getInt("State"));
		}

		return null;
	}

	public int getFoundBiomeX(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return stack.getTag().getInt("FoundX");
		}

		return 0;
	}

	public int getFoundBiomeZ(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return stack.getTag().getInt("FoundZ");
		}

		return 0;
	}

	public int getBiomeID(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return stack.getTag().getInt("BiomeID");
		}

		return -1;
	}

	public int getSearchRadius(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return stack.getTag().getInt("SearchRadius");
		}

		return -1;
	}

	public int getSamples(ItemStack stack) {
		if (ItemUtils.verifyNBT(stack)) {
			return stack.getTag().getInt("Samples");
		}

		return -1;
	}

	public String getBiomeName(ItemStack stack) {
		return BiomeUtils.getBiomeName(getBiomeID(stack));
	}

	public int getDistanceToBiome(EntityPlayer player, ItemStack stack) {
		return (int) player.getDistance(getFoundBiomeX(stack), player.posY, getFoundBiomeZ(stack));
	}

}
