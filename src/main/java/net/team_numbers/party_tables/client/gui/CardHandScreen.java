package net.team_numbers.party_tables.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.team_numbers.party_tables.attachment.ModAttachments;
import net.team_numbers.party_tables.client.ClientCardGameState;
import net.team_numbers.party_tables.util.Plane;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;

import java.util.List;
import java.util.Map;

public class CardHandScreen extends Screen {

    private boolean rightClicked;

    public CardHandScreen() {
        super(GameNarrator.NO_TITLE);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
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

        if (this.rightClicked) {
//            var hitResult = rayHitResult(this.minecraft, i, j, this.width, this.height);
//            if (hitResult.getType() != HitResult.Type.MISS) {
//                var v = hitResult.getLocation();
//                this.minecraft.level.addParticle(ParticleTypes.CLOUD, v.x, v.y, v.z, 0, 0.1F, 0);
//            }
            rayCard(this.minecraft, i, j, this.width, this.height);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        ClientCardGameState.deselect();
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
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            this.rightClicked = true;
        }
//        var hitResult = ray(this.minecraft, mouseX, mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            this.rightClicked = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private static void rayCard(Minecraft mc, double mouseX, double mouseY, int screenWidth, int screenHeight) {
        var player = mc.player;
        var cardHand = player.getData(ModAttachments.CARD_HAND);
        var partialTicks = mc.getTimer().getGameTimeDeltaTicks();

        var rot = Axis.YP.rotationDegrees(180.0F - Mth.rotLerp(partialTicks, player.yBodyRotO, player.yBodyRot));
        float tmp0 = -.5F;
        var centerPos = new Vec3(0F, 0F, tmp0);
        var planes = cardHand.cardPlanes(.875F, 1F, centerPos, new Vec3(0F, 0.5F, 0F));
        var ray = ray(mc, mouseX, mouseY, screenWidth, screenHeight);

        int i = planes.size() - 1;
        for (var plane : planes.reversed()) {
            var pos = plane.rot(rot).scale(0.9375F).add(player.position().add(0F, 1.501F, 0F));

            var res = pos.clip(ray.getKey(), ray.getValue());
            if (res.isPresent()) {
                break;
            }
            --i;
        }
        if (i >= 0) {
            ClientCardGameState.select(i);
        } else {
            ClientCardGameState.deselect();
        }
    }

    private static void particle(Minecraft mc, Plane p, int i) {
        var c = Vec3.fromRGB24(switch (i) {
            case 0 -> 0xff0000;
            case 1 -> 0x00ff00;
            case 2 -> 0x0000ff;
            case 3 -> 0xffff00;
            case 4 -> 0xff00ff;
            case 5 -> 0x00ffff;
            default -> 0xffffff;
        }).toVector3f();
        mc.level.addParticle(new DustParticleOptions(c, 1F), p.a().x, p.a().y, p.a().z, 0, 0.1F, 0);
        mc.level.addParticle(new DustParticleOptions(c, 1F), p.b().x, p.b().y, p.b().z, 0, 0.1F, 0);
        mc.level.addParticle(new DustParticleOptions(c, 1F), p.c().x, p.c().y, p.c().z, 0, 0.1F, 0);
        mc.level.addParticle(new DustParticleOptions(c, 1F), p.d().x, p.d().y, p.d().z, 0, 0.1F, 0);
    }

    private static HitResult rayHitResult(Minecraft mc, double mouseX, double mouseY, int screenWidth, int screenHeight) {
        Level level = mc.level;
        var ray = ray(mc, mouseX, mouseY, screenWidth, screenHeight);
        // 8. ブロックとのレイキャスト
        ClipContext ctx = new ClipContext(
            ray.getKey(),
            ray.getValue(),
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            mc.player
        );
        return level.clip(ctx);
    }

    private static Map.Entry<Vec3, Vec3> ray(Minecraft mc, double mouseX, double mouseY, int screenWidth, int screenHeight) {

//        float partialTick = mc.getTimer().getGameTimeDeltaTicks();
        Camera camera = mc.gameRenderer.getMainCamera();

        // 1. 投影行列を取得
//        double fov = mc.gameRenderer.getFov(camera, partialTick, true);
//        Matrix4f projectionMatrix = mc.gameRenderer.getProjectionMatrix(fov);
        float fov = mc.options.fov().get().floatValue();
        Matrix4f projectionMatrix = mc.gameRenderer.getProjectionMatrix(fov);

        // 2. ビュー行列(カメラの回転のみ。MCの描画はカメラを原点として扱うため平行移動は含めない)
        Matrix4f viewMatrix = new Matrix4f().rotation(camera.rotation().conjugate(new Quaternionf()));

        // 3. 投影×ビュー行列の逆行列
        Matrix4f invProjView = new Matrix4f(projectionMatrix).mul(viewMatrix).invert();

        // 4. スクリーン座標 → NDC(-1〜1)に正規化
        float ndcX = (float) ((mouseX / screenWidth) * 2.0 - 1.0);
        float ndcY = (float) (1.0 - (mouseY / screenHeight) * 2.0); // Y反転

        // 5. NDC上のnear/far点をワールド(カメラ相対)空間へ逆変換
        Vector4f nearPoint = new Vector4f(ndcX, ndcY, -1.0f, 1.0f).mul(invProjView);
        Vector4f farPoint  = new Vector4f(ndcX, ndcY,  1.0f, 1.0f).mul(invProjView);
        nearPoint.div(nearPoint.w);
        farPoint.div(farPoint.w);

        // 6. カメラのワールド座標を加算して、真のワールド座標にする
        Vec3 camPos = camera.getPosition();
        Vec3 near = new Vec3(nearPoint.x, nearPoint.y, nearPoint.z).add(camPos);
        Vec3 far  = new Vec3(farPoint.x, farPoint.y, farPoint.z).add(camPos);

        // 7. レイの方向を求め、5ブロック先までの終点を計算
        Vec3 dir = far.subtract(near).normalize();
        Vec3 rayStart = camPos; // カメラ位置を始点にする(near点でも可)
        Vec3 rayEnd = rayStart.add(dir.scale(5.0));
        return Map.entry(rayStart, rayEnd);
    }
}
