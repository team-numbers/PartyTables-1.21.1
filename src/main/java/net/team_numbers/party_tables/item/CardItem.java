package net.team_numbers.party_tables.item;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.team_numbers.party_tables.attachment.ModAttachments;

import java.util.List;

public class CardItem extends Item {

    public CardItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var itemstack = player.getItemInHand(usedHand);
        var cardHand = player.getData(ModAttachments.CARD_HAND);

        if (usedHand == InteractionHand.MAIN_HAND) {
//            if (!level.isClientSide) {
            if (true) {
                if (cardHand.toggleShow()) {
                    cardHand.set(itemstack);
                } else {
                    cardHand.resetCards();
                }
            }
//            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide);
            return InteractionResultHolder.success(itemstack);
        }
        return InteractionResultHolder.pass(itemstack);
    }

    /**
     * このアイテムをつかんで他のアイテムに対してクリック or 右クリックした際の動作
     */
    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (stack.getCount() != 1 || action != ClickAction.SECONDARY) {
            return false;
        } else {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData == null) {
                return false;
            }
            ItemStack itemstack = slot.getItem();
            CompoundTag tag = customData.copyTag();
            var selfType = tag.getString("Type");
            var selfCards = tag.getList("Cards", Tag.TAG_STRING);

            if (itemstack.isEmpty()) {
                if (selfCards.size() > 1) {
                    String card = selfCards.removeLast().getAsString();
                    ItemStack newStack = onlyType(this.getDefaultInstance(), selfType, card);
                    slot.safeInsert(newStack);
                    tag.put("Cards", selfCards);
                }
            } else if (itemstack.is(this)) {
                var slotCustomData = itemstack.get(DataComponents.CUSTOM_DATA);
                if (slotCustomData != null && slotCustomData.copyTag().getString("Type").equals(selfType)) {
                    slot.safeTake(itemstack.getCount(), 1, player);
                    var slotTag = slotCustomData.copyTag();
                    var cards = slotTag.getList("Cards", Tag.TAG_STRING);
                    if (!cards.isEmpty()) {
                        selfCards.addAll(cards);
                    }
                    tag.put("Cards", selfCards);
                }
            }
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

            return true;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            var tag = data.copyTag();
            var type = tag.getString("Type");
            var cards = tag.getList("Cards", Tag.TAG_STRING);

            tooltipComponents.add(Component.literal("Type: ")
                .append(Component.literal(type).withStyle(ChatFormatting.AQUA))
            );
            tooltipComponents.add(Component.literal("Cards: ")
                .append(Component.literal("[Size: " + cards.size() + "]").withStyle(ChatFormatting.YELLOW))
            );
            for (var card : cards) {
                tooltipComponents.add(Component.literal("Card: " + card.getAsString()).withStyle(ChatFormatting.LIGHT_PURPLE)
                );
            }
        }
    }

    public static ItemStack onlyType(ItemStack stack, String type, String card) {
        var data =  stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, compoundTag -> {
                compoundTag.putString("Type", type);
                var list = new ListTag();
                list.add(StringTag.valueOf(card));
                compoundTag.put("Cards", list);
            });
        }
        return stack;
    }

    public static List<CardData> get(ItemStack itemStack) {
        List<CardData> cards = Lists.newArrayList();
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return cards;
        }
        CompoundTag tag = customData.copyTag();
        var selfType = tag.getString("Type");
        var selfCards = tag.getList("Cards", Tag.TAG_STRING);
        for (var card : selfCards) {
            cards.add(new CardData(selfType, card.getAsString()));
        }
        return cards;
    }

    public record CardData(String type, String card) {}
}
