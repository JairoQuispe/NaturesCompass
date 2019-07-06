package com.chaosthedude.naturescompass.client;

import com.chaosthedude.naturescompass.config.ConfigHandler;
import com.chaosthedude.naturescompass.items.ItemNaturesCompass;
import com.chaosthedude.naturescompass.util.BiomeUtils;
import com.chaosthedude.naturescompass.util.EnumCompassState;
import com.chaosthedude.naturescompass.util.ItemUtils;
import com.chaosthedude.naturescompass.util.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

	private static final Minecraft mc = Minecraft.getInstance();

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		if (event.phase == Phase.END && mc.player != null && !mc.gameSettings.hideGUI && !mc.gameSettings.showDebugInfo && (mc.currentScreen == null || (ConfigHandler.CLIENT.displayWithChatOpen.get() && mc.currentScreen instanceof GuiChat))) {
			final EntityPlayer player = mc.player;
			final ItemStack stack = ItemUtils.getHeldNatureCompass(player);
			if (stack != null && stack.getItem() instanceof ItemNaturesCompass) {
				final ItemNaturesCompass compass = (ItemNaturesCompass) stack.getItem();
				if (compass.getState(stack) == EnumCompassState.SEARCHING) {
					System.out.println("rendering search");
					RenderUtils.drawLineOffsetStringOnHUD(I18n.format("string.naturescompass.status"), 5, 0, 0xFFFFFF, 0);
					RenderUtils.drawLineOffsetStringOnHUD(I18n.format("string.naturescompass.searching"), 5, 0, 0xAAAAAA, 1);

					RenderUtils.drawLineOffsetStringOnHUD(I18n.format("string.naturescompass.biome"), 5, 0, 0xFFFFFF, 3);
					RenderUtils.drawLineOffsetStringOnHUD(BiomeUtils.getBiomeName(compass.getBiomeID(stack)), 5, 0, 0xAAAAAA, 4);
				} else if (compass.getState(stack) == EnumCompassState.FOUND) {
					RenderUtils.drawLineOffsetStringOnHUD(I18n.format("string.naturescompass.status"), 5, 0, 0xFFFFFF, 0);
					RenderUtils.drawLineOffsetStringOnHUD(I18n.format("string.naturescompass.found"), 5, 0, 0xAAAAAA, 1);

					RenderUtils.drawLineOffsetStringOnHUD(I18n.format("string.naturescompass.biome"), 5, 0, 0xFFFFFF, 3);
					RenderUtils.drawLineOffsetStringOnHUD(BiomeUtils.getBiomeName(compass.getBiomeID(stack)), 5, 0, 0xAAAAAA, 4);

					RenderUtils.drawLineOffsetStringOnHUD(I18n.format("string.naturescompass.coordinates"), 5, 0, 0xFFFFFF, 6);
					RenderUtils.drawLineOffsetStringOnHUD(compass.getFoundBiomeX(stack) + ", " + compass.getFoundBiomeZ(stack), 5, 0, 0xAAAAAA, 7);

					RenderUtils.drawLineOffsetStringOnHUD(I18n.format("string.naturescompass.distance"), 5, 0, 0xFFFFFF, 9);
					RenderUtils.drawLineOffsetStringOnHUD(String.valueOf(BiomeUtils.getDistanceToBiome(player, compass.getFoundBiomeX(stack), compass.getFoundBiomeZ(stack))), 5, 0, 0xAAAAAA, 10);
				} else if (compass.getState(stack) == EnumCompassState.NOT_FOUND) {
					RenderUtils.drawLineOffsetStringOnHUD(I18n.format("string.naturescompass.status"), 5, 0, 0xFFFFFF, 0);
					RenderUtils.drawLineOffsetStringOnHUD(I18n.format("string.naturescompass.notFound"), 5, 0, 0xAAAAAA, 1);

					RenderUtils.drawLineOffsetStringOnHUD(I18n.format("string.naturescompass.biome"), 5, 0, 0xFFFFFF, 3);
					RenderUtils.drawLineOffsetStringOnHUD(BiomeUtils.getBiomeName(compass.getBiomeID(stack)), 5, 0, 0xAAAAAA, 4);

					RenderUtils.drawLineOffsetStringOnHUD(I18n.format("string.naturescompass.radius"), 5, 0, 0xFFFFFF, 6);
					RenderUtils.drawLineOffsetStringOnHUD(String.valueOf(compass.getSearchRadius(stack)), 5, 0, 0xAAAAAA, 7);

					RenderUtils.drawLineOffsetStringOnHUD(I18n.format("string.naturescompass.samples"), 5, 0, 0xFFFFFF, 9);
					RenderUtils.drawLineOffsetStringOnHUD(String.valueOf(compass.getSamples(stack)), 5, 0, 0xAAAAAA, 10);
				}
			}
		}
	}

}
