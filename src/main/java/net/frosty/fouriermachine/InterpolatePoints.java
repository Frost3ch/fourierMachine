package net.frosty.fouriermachine;

import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class InterpolatePoints {

    public static Vector2d[] interpolate(Vector2d[] original, int iCount) {
        List<Vector2d> result = new ArrayList<>();

        for (int i = 0; i < original.length - 1; i++) {
            Vector2d p1 = original[i];
            Vector2d p2 = original[i + 1];
            result.add(p1);

            double dx = (p2.x - p1.x) / (iCount + 1);
            double dy = (p2.y - p1.y) / (iCount + 1);

            for (int j = 1; j <= iCount; j++) {
                double x = p1.x + j * dx;
                double y = p1.y + j * dy;
                result.add(new Vector2d(x, y));
            }
        }
        if (result.size()%2==1) {
            result.add(original[original.length - 1]);
        }
        return result.toArray(new Vector2d[0]);
    }
}

