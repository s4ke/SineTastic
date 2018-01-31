package com.github.sinetastic.entities;

import java.awt.Polygon;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class FxUtil {

	private FxUtil() {
		throw new AssertionError("can't touch this!");
	}

	public static Polygon createPolygonFromFunction(
			BiConsumer<double[], double[]> fx, int steps, double width,
			double height, double[] tmp, double tmp2[]) {
		Polygon polygon = new Polygon();
		double dX = width / steps;
		for (int i = 0; i < steps; ++i) {
			tmp[0] = i;
			tmp2[0] = dX * i;
			fx.accept(tmp, tmp2);
			double val = tmp[0] * height / 2;
			polygon.addPoint((int) (dX * i), (int) (height / 2 - val));
		}
		return polygon;
	}

	public static Polygon makeArea(Polygon polygon, int steps, int dX, int dY) {
		// make this an area!
		for (int i = steps - 1; i >= 0; --i) {
			polygon.addPoint(polygon.xpoints[i] + dX, polygon.ypoints[i] + dY);
		}
		return polygon;
	}

}
