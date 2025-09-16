package net.yio;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RightClickEmptyPacket {
	private InteractionHand hand;

	public RightClickEmptyPacket(InteractionHand hand) {
		this.hand = hand;
	}

	public RightClickEmptyPacket(FriendlyByteBuf buffer) {
		this.hand = buffer.readEnum(InteractionHand.class);
	}

	public static void buffer(RightClickEmptyPacket message, FriendlyByteBuf buf) {
		buf.writeEnum(message.hand);
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

	public static void handleRightClickEmpty(Player player, InteractionHand hand) {
		if (!player.level().hasChunkAt(player.blockPosition())) {
			return;
		}
		int arrowCount = ((LivingEntity) player).getArrowCount();
		if (arrowCount > 0) {
			player.swing(InteractionHand.MAIN_HAND, true);
			((LivingEntity) player).setArrowCount(arrowCount - 1);
			ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.ARROW, 1));
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		YioMod.addNetworkMessage(RightClickEmptyPacket.class, RightClickEmptyPacket::buffer, RightClickEmptyPacket::new, RightClickEmptyPacket::handler);
	}
}
