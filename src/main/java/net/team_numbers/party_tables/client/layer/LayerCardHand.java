package net.team_numbers.party_tables.client.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.team_numbers.party_tables.PartyTables;
import net.team_numbers.party_tables.attachment.ModAttachments;
import net.team_numbers.party_tables.util.Plane;

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
        var b0 = bufferIn.getBuffer(RenderType.lines());
        var p0 = matrixStack.last();
        float tmp0 = -.5F;

        b0.addVertex(p0, -.5F, 0F, tmp0).setColor(1F, 1F, 1F, 1F).setNormal(p0, -1F, 0F, 0F);
        b0.addVertex(p0, +.5F, 0F, tmp0).setColor(1F, 1F, 1F, 1F).setNormal(p0, -1F, 0F, 0F);
        b0.addVertex(p0, 0F, -.5F, tmp0).setColor(1F, 1F, 1F, 1F).setNormal(p0, -1F, 0F, 0F);
        b0.addVertex(p0, 0F, +.5F, tmp0).setColor(1F, 1F, 1F, 1F).setNormal(p0, -1F, 0F, 0F);

//        matrixStack.scale(0.025F, 0.025F, 0.025F);
//        matrixStack.scale(0.05625F, 0.05625F, 0.05625F);
//        matrixStack.translate(0.0D, 0.0D, -8.0D);
//        matrixStack.scale(1/8F, 1/8F, 1/8F);
        var planes = cardHand.cardPlanes(new Vec3(0F, 0F, tmp0));
        var cards = cardHand.getCards();
        int index = 0;
        RenderSystem.depthMask(false);
        for (var plane : planes) {
            var card = cards.get(index);
            int j = -1;
            if (card.selector() != null) {
                j = 0xffff4444;
            }
            renderPlaneBack(matrixStack, bufferIn, card.type(), plane, j, lightIn);
            ++index;
            matrixStack.translate(0F, 0.0F, -index * 0.001F);
        }
        RenderSystem.depthMask(true);
        matrixStack.popPose();
    }

    public static void renderPlaneBack(PoseStack poseStack, MultiBufferSource buffer, String type, Plane plane, int color, int light) {
        renderPlane(poseStack, buffer, type + "_back", plane, color, light);
    }

    public static void renderPlaneFront(PoseStack poseStack, MultiBufferSource buffer, String type, String card, Plane plane, int color, int light) {
        renderPlane(poseStack, buffer, type + "_front_" + card.toLowerCase(), plane, color, light);
    }

    public static void renderPlane(PoseStack poseStack, MultiBufferSource buffer, String texture, Plane plane, int color, int light) {
        var vc = buffer.getBuffer(RenderType.entityCutoutNoCull(PartyTables.id("textures/card/" + texture + ".png")));
        PoseStack.Pose pose = poseStack.last();

        vertexWithColor(pose, vc, plane.a(), 0F, 0F, color, light, -1, 0, 0);
        vertexWithColor(pose, vc, plane.b(), 1F, 0F, color, light, -1, 0, 0);
        vertexWithColor(pose, vc, plane.c(), 1F, 1F, color, light, -1, 0, 0);
        vertexWithColor(pose, vc, plane.d(), 0F, 1F, color, light, -1, 0, 0);
    }

    private void renderCard(PoseStack poseStack, MultiBufferSource buffer, String type, String card, int index, int count, int light) {
        float c = -count * 16 / 2F;
        float f = c + index * 16;
        float f1 = f + 16;
        float f2 = 0;
        float f3 = 16;

        poseStack.pushPose();
        var vc = buffer.getBuffer(RenderType.entityCutout(PartyTables.id("textures/card/" + type + "_front_" + card.toLowerCase() + ".png")));
        PoseStack.Pose pose = poseStack.last();

        this.vertex(pose, vc, f, f2, 0, 0F, 0F, light, -1, 0, 0);
        this.vertex(pose, vc, f1, f2, 0, 1F, 0F, light, -1, 0, 0);
        this.vertex(pose, vc, f1, f3, 0, 1F, 1F, light, -1, 0, 0);
        this.vertex(pose, vc, f, f3, 0, 0F, 1F, light, -1, 0, 0);

        var vc1 = buffer.getBuffer(RenderType.entityCutout(PartyTables.id("textures/card/" + type + "_back.png")));

        this.vertex(pose, vc1, f, f2, 0, 0F, 0F, light, -1, 0, 0);
        this.vertex(pose, vc1, f, f3, 0, 0F, 1F, light, -1, 0, 0);
        this.vertex(pose, vc1, f1, f3, 0, 1F, 1F, light, -1, 0, 0);
        this.vertex(pose, vc1, f1, f2, 0, 1F, 0F, light, -1, 0, 0);

        poseStack.popPose();
    }

    private static void vertexWithColor(
                        PoseStack.Pose pose, VertexConsumer consumer,
                        Vec3 pos, float u, float v, int color, int light,
                        int normalX,
                        int normalY,
                        int normalZ) {
        consumer.addVertex(pose, (float)pos.x, (float)pos.y, (float)pos.z)
            .setColor(color)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, (float)normalX, (float)normalZ, (float)normalY);
    }

    private void vertex(PoseStack.Pose pose, VertexConsumer consumer,
                        Vec3 pos, float u, float v, int light,
                        int normalX,
                        int normalY,
                        int normalZ) {
        vertex(pose, consumer, (float)pos.x, (float)pos.y, (float)pos.z, u, v, light, normalX, normalY, normalZ);
    }

    private void vertex(PoseStack.Pose pose, VertexConsumer consumer,
                              float x, float y, float z, float u, float v, int light,
                              int normalX,
                              int normalY,
                              int normalZ) {
        consumer.addVertex(pose, x, y, z)
            .setColor(-1)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, (float)normalX, (float)normalZ, (float)normalY);
    }
}
