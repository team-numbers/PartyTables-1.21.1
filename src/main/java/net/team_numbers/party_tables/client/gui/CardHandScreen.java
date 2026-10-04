package net.team_numbers.party_tables.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.team_numbers.party_tables.PartyTables;
import net.team_numbers.party_tables.attachment.CardHand;
import net.team_numbers.party_tables.attachment.ModAttachments;
import net.team_numbers.party_tables.client.ClientScreenRays;
import net.team_numbers.party_tables.client.layer.LayerCardHand;
import net.team_numbers.party_tables.entity.CardRayCasts;
import net.team_numbers.party_tables.entity.SelectCard;
import net.team_numbers.party_tables.network.payload.SCloseCardHandPayload;
import net.team_numbers.party_tables.network.payload.SPickCardPayload;
import net.team_numbers.party_tables.network.payload.SPickCardSlidePayload;
import net.team_numbers.party_tables.network.payload.SSelectCardPayload;
import net.team_numbers.party_tables.util.Plane;
import net.team_numbers.party_tables.util.Ray;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;
import java.util.OptionalInt;

public class CardHandScreen extends Screen {

    private boolean leftClicked;
    private boolean rightClicked;

    private double clickedX;
    private double clickedY;
    private SelectCard pickedCard;
    private SelectCard selectedCard;

    public CardHandScreen() {
        super(GameNarrator.NO_TITLE);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        var cardHand = minecraft.player.getData(ModAttachments.CARD_HAND);
        if (cardHand.isEmpty()) {
            return;
        }
        float scale = 75F;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.width / 2F, this.height / 2F, 0F);
        guiGraphics.pose().translate(0, this.height / 4F, 0F);
        guiGraphics.pose().scale(scale, scale, -scale);
        var planes = cardHand.cardPlanes(new Vec3(0F, 0F, 0F));
        var cards = cardHand.getCards();
        int index = 0;
        RenderSystem.depthMask(false);
        for (var plane : planes) {
            var card = cards.get(index);
            int j = -1;
            if (card.selector() != null) {
                j = 0xffff4444;
            }
            LayerCardHand.renderPlaneFront(guiGraphics.pose(), guiGraphics.bufferSource(), card.type(), card.card(), plane, j, 15728880);
            ++index;
        }
        guiGraphics.flush();
        guiGraphics.pose().popPose();
        RenderSystem.depthMask(true);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        int i = (int)(
            this.minecraft.mouseHandler.xpos()
                * (double)this.minecraft.getWindow().getGuiScaledWidth()
                / (double)this.minecraft.getWindow().getScreenWidth()
        );
        int j = (int)(
            this.minecraft.mouseHandler.ypos()
                * (double)this.minecraft.getWindow().getGuiScaledHeight()
                / (double)this.minecraft.getWindow().getScreenHeight()
        );
//        var hitResult = ray(this.minecraft, i, j);
//        System.out.println("hit?" + hitResult.getType() + "," + hitResult.getLocation());
//        if (hitResult.getType() != HitResult.Type.MISS) {
//            var v = hitResult.getLocation();
//            System.out.println(v);
//            this.minecraft.level.addParticle(ParticleTypes.CLOUD, v.x, v.y, v.z, 0, 1F, 0);
//        }

        if (this.leftClicked && this.pickedCard != null) {
            double x = getMouseX(this.minecraft);
            double y = getMouseY(this.minecraft);
            double x1 = x - this.clickedX;
            double y1 = y - this.clickedY;
            double mouseAngle = Mth.wrapDegrees(Mth.atan2(y1, x1) * Mth.RAD_TO_DEG + 90.0F);
            double angle = CardHand.getAngle(this.pickedCard.index(), this.pickedCard.cardCount());
            double d = Mth.length(x1, y1);
            if (mouseAngle - angle < 60D) {
                if (d < 50.0D) {
                    PacketDistributor.sendToServer(new SPickCardSlidePayload(pickedCard.entity().getId(), pickedCard.index(), d));
                } else {
                    var entityCardData = pickedCard.entity().getData(ModAttachments.CARD_HAND);
                    entityCardData.pick(pickedCard.index());
                    PacketDistributor.sendToServer(new SPickCardPayload(pickedCard.entity().getId(), pickedCard.index()));
                    pickedCard = null;
                }
            }
        }

        if (this.rightClicked) {
//            var hitResult = rayHitResult(this.minecraft, i, j, this.width, this.height);
//            if (hitResult.getType() != HitResult.Type.MISS) {
//                var v = hitResult.getLocation();
//                this.minecraft.level.addParticle(ParticleTypes.CLOUD, v.x, v.y, v.z, 0, 0.1F, 0);
//            }
            var ray = ClientScreenRays.screenRay(minecraft, i, j, this.width, this.height, 5.0D);
            var result = CardRayCasts.rayCardPlayers(minecraft.level, minecraft.player, ray);
            result.ifPresent(selectCard -> {
                PacketDistributor.sendToServer(new SSelectCardPayload(selectCard.entity().getId(), OptionalInt.of(selectCard.index())));
            });
//            rayCard(this.minecraft, i, j, this.width, this.height);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        PacketDistributor.sendToServer(new SCloseCardHandPayload());
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
//        var hitResult = rayClick(this.minecraft, mouseX, mouseY, this.width, this.height);
//        if (hitResult.getType() != HitResult.Type.MISS) {
//            var v = hitResult.getLocation();
//            this.minecraft.level.addParticle(ParticleTypes.CLOUD, v.x, v.y, v.z, 0, 0.1F, 0);
//            return true;
//        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        switch (button) {
            case GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                this.leftClicked = true;
                this.clickedX = mouseX;
                this.clickedY = mouseY;
                var result = getSelectedCardFromScreenRay();
                result.ifPresent(selectCard -> {
                    this.pickedCard = selectCard;
                });
            }
            case GLFW.GLFW_MOUSE_BUTTON_RIGHT ->  this.rightClicked = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        switch (button) {
            case GLFW.GLFW_MOUSE_BUTTON_LEFT -> this.leftClicked = false;
            case GLFW.GLFW_MOUSE_BUTTON_RIGHT ->  this.rightClicked = false;
        }
        if (pickedCard != null) {
            PacketDistributor.sendToServer(new SPickCardSlidePayload(pickedCard.entity().getId(), pickedCard.index(), 0));
            this.pickedCard = null;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private Optional<SelectCard> getSelectedCardFromScreenRay() {
        return CardRayCasts.rayCardPlayers(minecraft.level, minecraft.player, getScreenRay());
    }

    private Ray getScreenRay() {
        int i = (int)getMouseX(this.minecraft);
        int j = (int)getMouseY(this.minecraft);
        return ClientScreenRays.screenRay(minecraft, i, j, this.width, this.height, 5.0D);
    }

    private static double getMouseX(Minecraft mc) {
        return mc.mouseHandler.xpos()
            * (double)mc.getWindow().getGuiScaledWidth()
            / (double)mc.getWindow().getScreenWidth();
    }

    private static double getMouseY(Minecraft mc) {
        return mc.mouseHandler.ypos()
            * (double)mc.getWindow().getGuiScaledHeight()
            / (double)mc.getWindow().getScreenHeight();
    }
}
