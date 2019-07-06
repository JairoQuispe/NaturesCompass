package com.chaosthedude.naturescompass.network;

import java.util.function.Supplier;

import com.chaosthedude.naturescompass.items.ItemNaturesCompass;
import com.chaosthedude.naturescompass.util.ItemUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketCompassSearch {

	private int biomeID;

	private int x;
	private int y;
	private int z;

	public PacketCompassSearch() {}

	public PacketCompassSearch(int biomeID, BlockPos pos) {
		this.biomeID = biomeID;

		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}

	public PacketCompassSearch(PacketBuffer buf) {
		biomeID = buf.readInt();

		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(biomeID);

		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}


	public void handle(Supplier<NetworkEvent.Context> ctx) {
		System.out.println("enqueue");
		ctx.get().enqueueWork(() -> {
			final ItemStack stack = ItemUtils.getHeldNatureCompass(ctx.get().getSender());
			if (!stack.isEmpty()) {
				final ItemNaturesCompass natureCompass = (ItemNaturesCompass) stack.getItem();
				final World world = ctx.get().getSender().world;
				System.out.println("searching");
				natureCompass.searchForBiome(world, ctx.get().getSender(), biomeID, new BlockPos(x, y, z), stack);
			}
		});
		ctx.get().setPacketHandled(true);
	}

}
