package net.team_numbers.party_tables.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.team_numbers.party_tables.attachment.ModAttachments;

public class LayerCardHand<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public LayerCardHand(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, T entity, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        var cardHand = entity.getData(ModAttachments.CARD_HAND);

        if (cardHand.isEmpty()) {
            return;
        }

        matrixStack.pushPose();
        matrixStack.scale(0.025F, 0.025F, 0.025F);
        matrixStack.translate(0.0D, 0.0D, -16.0D);

        var font = Minecraft.getInstance().font;

        int j = (int)(255.0F) << 24;
        int i = 0;
        for (var card : cardHand.getCards()) {
            font.drawInBatch(card, 0, i * 8, 0xFFFFFFFF, false, matrixStack.last().pose(), bufferIn, Font.DisplayMode.NORMAL, j, lightIn);
            ++i;
        }
        matrixStack.popPose();
    }
}
