package net.yio;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.util.Hand;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;

public class ArrowYank {
	@Mod.EventBusSubscriber
	public static class Trigger {
		@SubscribeEvent
		public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
			if (event.getHand() == Hand.MAIN_HAND && event.getEntity().isSneaking()) {
				//System.out.println("ping");
				YioMod.PACKET_HANDLER.sendToServer(new RightClickEmptyPacket(event.getHand()));
			}
		}

		@SubscribeEvent
		public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
			if (event.getHand() == Hand.MAIN_HAND && !event.getEntity().world.isRemote() && event.getEntity().isSneaking()) {
				PlayerEntity player = (PlayerEntity) event.getEntity();
				int arrowCount = ((LivingEntity) player).getArrowCountInEntity();
				ItemStack mainHandItem = player.getHeldItemMainhand();
				if ((mainHandItem.getItem() == Items.ARROW) && arrowCount > 0) {
					player.swing(Hand.MAIN_HAND, true);
					((LivingEntity) player).setArrowCountInEntity(arrowCount - 1);
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.ARROW, 1));
				}
			}
		}

		@SubscribeEvent
		public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
			if (event.getHand() == Hand.MAIN_HAND && !event.getEntity().world.isRemote() && event.getEntity().isSneaking()) {
				PlayerEntity player = (PlayerEntity) event.getEntity();
				int arrowCount = ((LivingEntity) player).getArrowCountInEntity();
				ItemStack mainHandItem = player.getHeldItemMainhand();
				if (mainHandItem.isEmpty() && arrowCount > 0) {
					player.swing(Hand.MAIN_HAND, true);
					((LivingEntity) player).setArrowCountInEntity(arrowCount - 1);
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.ARROW, 1));
				}
			}
		}
	}
}
