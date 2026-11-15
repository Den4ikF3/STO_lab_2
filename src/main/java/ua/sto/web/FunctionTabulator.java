package ua.sto.web;

import java.util.ArrayList;
import java.util.List;

/**
 * Табулювання функції варіанта 4:  y = x^0.2  на відрізку [a; b] з кроком h.
 * Областю визначення вважаємо x >= 0 (степінь із дробовим показником).
 * Для x < 0 функція не визначена — такі точки позначаються як відсутні.
 */
public final class FunctionTabulator {

    public static final int MAX_POINTS = 5000;

    /** Одна точка таблиці. defined = false, якщо x поза областю визначення. */
    public record Point(double x, double y, boolean defined) {
    }

    private FunctionTabulator() {
    }

    public static double f(double x) {
        return Math.pow(x, 0.2);
    }

    public static boolean isDefined(double x) {
        return x >= 0;
    }

    /** Кількість точок на відрізку [a; b] з кроком h (з допуском на похибку double). */
    public static int pointsCount(double a, double b, double h) {
        return (int) Math.floor((b - a) / h + 1e-9) + 1;
    }

    public static List<Point> tabulate(double a, double b, double h) {
        int n = pointsCount(a, b, h);
        List<Point> points = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            double x = a + i * h;
            if (isDefined(x)) {
                points.add(new Point(x, f(x), true));
            } else {
                points.add(new Point(x, Double.NaN, false));
            }
        }
        return points;
    }
}
