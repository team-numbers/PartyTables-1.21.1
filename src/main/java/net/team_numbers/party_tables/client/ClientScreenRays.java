package net.team_numbers.party_tables.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.team_numbers.party_tables.util.Ray;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

public final class ClientScreenRays {

    private ClientScreenRays() {}

    public static Ray screenRay(double mouseX, double mouseY, int screenWidth, int screenHeight) {
        return screenRay(Minecraft.getInstance(), mouseX, mouseY, screenWidth, screenHeight, 5.0D);
    }

    public static Ray screenRay(Minecraft mc, double mouseX, double mouseY, int screenWidth, int screenHeight, double distance) {
        Camera camera = mc.gameRenderer.getMainCamera();

        // 1. 投影行列を取得
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
        // カメラ位置を始点にする(near点でも可)
        Vec3 rayEnd = camPos.add(dir.scale(distance));
        return new Ray(camPos, rayEnd);
    }
}
