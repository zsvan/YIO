package net.yio;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RightClickEmptyPacket {
	private Hand hand;

	public RightClickEmptyPacket(Hand hand) {
		this.hand = hand;
	}

	public RightClickEmptyPacket(PacketBuffer buf) {
		this.hand = buf.readEnumValue(Hand.class);
	}

	public static void buf(RightClickEmptyPacket message, PacketBuffer buf) {
		buf.writeEnumValue(message.hand);
	}

	public static void handler(RightClickEmptyPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			if (context.getSender() != null) {
				handleRightClickEmpty(context.getSender(), message.hand);
			}
		});
		context.setPacketHandled(true);
	}

	public static void handleRightClickEmpty(PlayerEntity player, Hand hand) {
		//System.out.println("pong");
		World world = player.world;
		double x = player.getPosX();
		double y = player.getPosY();
		double z = player.getPosZ();
		if (!world.isBlockLoaded(new BlockPos(x, y, z))) {
			return;
		}
		int arrowCount = ((LivingEntity) player).getArrowCountInEntity();
		if (arrowCount > 0) {
			player.swing(Hand.MAIN_HAND, true);
			((LivingEntity) player).setArrowCountInEntity(arrowCount - 1);
			ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.ARROW, 1));
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		YioModElements yioModElements = new YioModElements();
		yioModElements.addNetworkMessage(RightClickEmptyPacket.class, RightClickEmptyPacket::buf, RightClickEmptyPacket::new,
				RightClickEmptyPacket::handler);
	}
}
