package net.team_numbers.party_tables.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Optional;

public record Plane(Vec3 a, Vec3 b, Vec3 c, Vec3 d) {

    public static Plane fromVector3f(Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
        return new Plane(new Vec3(a), new Vec3(b), new Vec3(c), new Vec3(d));
    }

    public static Plane ofXY(Vec3 pos, float xSize, float ySize, Vec3 rotPos, Quaternionf rotation) {
        float xh = xSize / 2.0F;
        float yh = ySize / 2.0F;
        var mat = new Matrix4f();
        mat.translate(rotPos.reverse().toVector3f());
        mat.rotate(rotation);
        mat.translate(rotPos.toVector3f());
        var a = new Vector4f((float)pos.x - xh, (float)pos.y - yh, (float)pos.z, 1F).mulProject(mat, new Vector3f());
        var b = new Vector4f((float)pos.x + xh, (float)pos.y - yh, (float)pos.z, 1F).mulProject(mat, new Vector3f());
        var c = new Vector4f((float)pos.x + xh, (float)pos.y + yh, (float)pos.z, 1F).mulProject(mat, new Vector3f());
        var d = new Vector4f((float)pos.x - xh, (float)pos.y + yh, (float)pos.z, 1F).mulProject(mat, new Vector3f());
        return fromVector3f(a, b, c, d);
    }

    public Plane add(Vec3 v) {
        return new Plane(a.add(v), b.add(v), c.add(v), d.add(v));
    }

    public Plane sub(Vec3 v) {
        return new Plane(a.subtract(v), b.subtract(v), c.subtract(v), d.subtract(v));
    }

    public Plane mul(Vec3 v) {
        return new Plane(a.multiply(v), b.multiply(v), c.multiply(v), d.multiply(v));
    }

    public Plane scale(float scale) {
        return new Plane(a.scale(scale), b.scale(scale), c.scale(scale), d.scale(scale));
    }

    public Plane rot(Quaternionf rot) {
        var a1 = new Vec3(rot.transform(a.toVector3f()));
        var b1 = new Vec3(rot.transform(b.toVector3f()));
        var c1 = new Vec3(rot.transform(c.toVector3f()));
        var d1 = new Vec3(rot.transform(d.toVector3f()));
        return new Plane(a1, b1, c1, d1);
    }

    public Optional<Vec3> clip(Vec3 vs, Vec3 ve) {
        // ----------------------------------------------------
        // ステップ1: 平面の法線ベクトルと、線分の方向ベクトルを計算
        // ----------------------------------------------------
        Vec3 ab = b.subtract(a);
        Vec3 ac = c.subtract(a);
        Vec3 n = ab.cross(ac); // 平面の法線ベクトル

        Vec3 v = ve.subtract(vs); // 線分の方向ベクトル

        // ----------------------------------------------------
        // ステップ2: 平面と線分が平行かどうかチェック
        // ----------------------------------------------------
        double dotVN = v.dot(n);

        // 内積がほぼゼロなら、線分と平面は平行（交点なし、または平面上に完全に入っている）
        if (Math.abs(dotVN) < 1e-6f) {
            return Optional.empty();
        }

        // ----------------------------------------------------
        // ステップ3: 交点パラメータ t の計算と範囲チェック (0 <= t <= 1)
        // ----------------------------------------------------
        Vec3 avs = a.subtract(vs);
        double t = avs.dot(n) / dotVN;

        // tが0未満、または1より大きい場合は、線分の範囲外で交わっている
        if (t < 0.0 || t > 1.0) {
            return Optional.empty();
        }

        // 具体的な交点 P の座標を算出
        Vec3 p = vs.add(v.multiply(t, t, t));

        // ----------------------------------------------------
        // ステップ4: 交点 P が四角形の内側にあるか判定 (内外判定)
        // ----------------------------------------------------
        // 各辺のベクトルと、各頂点からPへのベクトルの外積を計算
        Vec3 c1 = b.subtract(a).cross(p.subtract(a));
        Vec3 c2 = c.subtract(b).cross(p.subtract(b));
        Vec3 c3 = d.subtract(c).cross(p.subtract(c));
        Vec3 c4 = a.subtract(d).cross(p.subtract(d));

        // 外積ベクトルと平面の法線ベクトルの方向を内積で比較
        double d1 = c1.dot(n);
        double d2 = c2.dot(n);
        double d3 = c3.dot(n);
        double d4 = c4.dot(n);

        // 全てが同じ符号（すべて0以上、またはすべて0以下）なら内側にある
        // 計算誤差を考慮して -EPSILON で判定
        if ((d1 >= -1e-6f && d2 >= -1e-6f && d3 >= -1e-6f && d4 >= -1e-6f) ||
            (d1 <=  1e-6f && d2 <=  1e-6f && d3 <=  1e-6f && d4 <=  1e-6f)) {
            return Optional.of(p); // 交差しているため、交点Pを返す
        }

        return Optional.empty(); // 外側にあるため交差しない
    }
}
