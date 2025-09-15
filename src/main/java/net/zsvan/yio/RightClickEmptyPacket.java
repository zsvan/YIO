package net.zsvan.yio;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.chat.Component;

public record RightClickEmptyPacket(InteractionHand hand) implements CustomPacketPayload {
	public static final Type<RightClickEmptyPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(YioMod.MODID, "right_click_empty"));

	public static final StreamCodec<FriendlyByteBuf, RightClickEmptyPacket> STREAM_CODEC = StreamCodec.composite(
		StreamCodec.of((buf, hand) -> buf.writeEnum(hand), buf -> buf.readEnum(InteractionHand.class)),
		RightClickEmptyPacket::hand,
		RightClickEmptyPacket::new
	);

	@Override
	public Type<RightClickEmptyPacket> type() {
		return TYPE;
	}

	public static void handleData(final RightClickEmptyPacket packet, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				Player player = context.player();
				if (player != null && player.level().isLoaded(player.blockPosition())) {
					handleRightClickEmpty(player, packet.hand());
				}
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void handleRightClickEmpty(Player player, InteractionHand hand) {
		int vanillaArrowCount = player.getArrowCount();
		if (vanillaArrowCount > 0) {
			player.swing(InteractionHand.MAIN_HAND, true);
			player.setArrowCount(vanillaArrowCount - 1);

			ItemStack retrievedArrow = ArrowTracker.retrieveArrow(player);
			if (!retrievedArrow.isEmpty()) {
				ItemHandlerHelper.giveItemToPlayer(player, retrievedArrow);
			}
			// If empty, it was an infinity arrow that crumbled (message already shown)
		}
	}
}
