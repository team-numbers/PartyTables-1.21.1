package net.team_numbers.party_tables.util;

import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;

public record RotatablePlane(Vec3 center, Quaternionf rotation, double halfWidth, double halfHeight) {

    public static RotatablePlane of(Vec3 pos, float width, float height, float xAngle) {
        return new RotatablePlane(pos, Axis.XP.rotation(xAngle), width / 2F, height / 2F);
    }

    /**
     * レイ(始点〜終点)と平面の交差判定
     * @param start レイの始点(ワールド空間)
     * @param end レイの終点(ワールド空間)
     * @return 交差した場合はワールド空間の交点、しなければnull
     */
    public Optional<Vec3> clip(Vec3 start, Vec3 end) {
        Vec3 rayDelta = end.subtract(start);
        double rayLength = rayDelta.length();

        if (rayLength < 1e-8) {
            return Optional.empty(); // start と end が同じ点
        }

        Vec3 rayDirection = rayDelta.scale(1.0 / rayLength); // 正規化

        // 回転の逆(平面のローカル空間へ変換するため)
        Quaternionf invRotation = new Quaternionf(rotation).conjugate();

        // 始点・方向をローカル空間へ変換
        Vector3f localOrigin = toLocal(start, invRotation);
        Vector3f localDir = toLocalDirection(rayDirection, invRotation);

        // ローカル空間でのZ=0平面との交差判定
        if (Math.abs(localDir.z) < 1e-8f) {
            // レイが平面と平行
            return Optional.empty();
        }

        float t = -localOrigin.z / localDir.z;

        // tが負、またはrayLengthを超える場合は範囲外(start〜end間に収まらない)
        if (t < 0 || t > rayLength) {
            return Optional.empty();
        }

        // ローカル空間での交点
        float localX = localOrigin.x + t * localDir.x;
        float localY = localOrigin.y + t * localDir.y;

        // 平面の範囲内かチェック(矩形)
        if (Math.abs(localX) > halfWidth || Math.abs(localY) > halfHeight) {
            return Optional.empty();
        }

        // ワールド空間の交点を返す
        return Optional.of(start.add(rayDirection.scale(t)));
    }

    private Vector3f toLocal(Vec3 worldPos, Quaternionf invRotation) {
        Vector3f relative = new Vector3f(
            (float) (worldPos.x - center.x),
            (float) (worldPos.y - center.y),
            (float) (worldPos.z - center.z)
        );
        return invRotation.transform(relative);
    }

    private Vector3f toLocalDirection(Vec3 worldDir, Quaternionf invRotation) {
        Vector3f dir = new Vector3f((float) worldDir.x, (float) worldDir.y, (float) worldDir.z);
        return invRotation.transform(dir);
    }
}
