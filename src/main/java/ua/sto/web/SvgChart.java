package ua.sto.web;

import java.util.List;
import java.util.Locale;

/**
 * Побудова графіка функції у вигляді вбудованого SVG-зображення
 * (без сторонніх бібліотек: браузер малює його самостійно).
 */
public final class SvgChart {

    private static final int W = 760;
    private static final int H = 420;
    private static final int LEFT = 60;
    private static final int RIGHT = 20;
    private static final int TOP = 34;
    private static final int BOTTOM = 40;

    private SvgChart() {
    }

    public static String build(List<FunctionTabulator.Point> points, String title) {
        double xMin = Double.POSITIVE_INFINITY, xMax = Double.NEGATIVE_INFINITY;
        double yMin = Double.POSITIVE_INFINITY, yMax = Double.NEGATIVE_INFINITY;
        for (FunctionTabulator.Point p : points) {
            xMin = Math.min(xMin, p.x());
            xMax = Math.max(xMax, p.x());
            if (p.defined()) {
                yMin = Math.min(yMin, p.y());
                yMax = Math.max(yMax, p.y());
            }
        }
        if (yMin == Double.POSITIVE_INFINITY) {
            return "<p>Функція не визначена на заданому відрізку, графік не будується.</p>";
        }
        if (xMax == xMin) {
            xMin -= 1;
            xMax += 1;
        }
        if (yMax == yMin) {
            yMin -= 1;
            yMax += 1;
        }
        // невеликі поля навколо графіка
        double dy = (yMax - yMin) * 0.08;
        yMin -= dy;
        yMax += dy;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US,
                "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 %d %d' width='100%%' style='max-width:%dpx;background:#fff;border:1px solid #d0d7de;border-radius:6px'>",
                W, H, W));
        sb.append("<text x='").append(W / 2).append("' y='14' text-anchor='middle' font-size='13' fill='#24292f'>")
                .append(title).append("</text>");

        // сітка та підписи осей
        int ticks = 6;
        for (int i = 0; i <= ticks; i++) {
            double xv = xMin + (xMax - xMin) * i / ticks;
            double yv = yMin + (yMax - yMin) * i / ticks;
            double px = mapX(xv, xMin, xMax);
            double py = mapY(yv, yMin, yMax);
            sb.append(String.format(Locale.US,
                    "<line x1='%.1f' y1='%d' x2='%.1f' y2='%d' stroke='#e5e7eb'/>", px, TOP, px, H - BOTTOM));
            sb.append(String.format(Locale.US,
                    "<line x1='%d' y1='%.1f' x2='%d' y2='%.1f' stroke='#e5e7eb'/>", LEFT, py, W - RIGHT, py));
            sb.append(String.format(Locale.US,
                    "<text x='%.1f' y='%d' text-anchor='middle' font-size='11' fill='#57606a'>%.2f</text>",
                    px, H - BOTTOM + 16, xv));
            sb.append(String.format(Locale.US,
                    "<text x='%d' y='%.1f' text-anchor='end' font-size='11' fill='#57606a'>%.2f</text>",
                    LEFT - 6, py + 4, yv));
        }
        // рамка області графіка
        sb.append(String.format(Locale.US,
                "<rect x='%d' y='%d' width='%d' height='%d' fill='none' stroke='#8c959f'/>",
                LEFT, TOP, W - LEFT - RIGHT, H - TOP - BOTTOM));

        // ламана лінія (розриви на недовизначених точках) та точки
        StringBuilder line = new StringBuilder();
        for (FunctionTabulator.Point p : points) {
            if (!p.defined()) {
                flush(sb, line);
                continue;
            }
            line.append(String.format(Locale.US, "%.2f,%.2f ", mapX(p.x(), xMin, xMax), mapY(p.y(), yMin, yMax)));
        }
        flush(sb, line);
        if (points.size() <= 200) {
            for (FunctionTabulator.Point p : points) {
                if (p.defined()) {
                    sb.append(String.format(Locale.US,
                            "<circle cx='%.2f' cy='%.2f' r='3' fill='#cf222e'/>",
                            mapX(p.x(), xMin, xMax), mapY(p.y(), yMin, yMax)));
                }
            }
        }
        sb.append("</svg>");
        return sb.toString();
    }

    private static void flush(StringBuilder sb, StringBuilder line) {
        if (line.length() > 0) {
            sb.append("<polyline fill='none' stroke='#0969da' stroke-width='2' points='")
                    .append(line.toString().trim()).append("'/>");
            line.setLength(0);
        }
    }

    private static double mapX(double x, double xMin, double xMax) {
        return LEFT + (x - xMin) / (xMax - xMin) * (W - LEFT - RIGHT);
    }

    private static double mapY(double y, double yMin, double yMax) {
        return H - BOTTOM - (y - yMin) / (yMax - yMin) * (H - TOP - BOTTOM);
    }
}
