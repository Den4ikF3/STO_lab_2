package ua.sto.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

/**
 * Завдання 2.2. Табулювання функції y = x^0.2 на відрізку [a; b] з кроком h
 * та побудова графіка. Значення a, b, h передаються як параметри запиту:
 * /task2?a=0&b=5&h=0.5
 */
@WebServlet(name = "Task2Servlet", urlPatterns = "/task2")
public class Task2Servlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String body;
        try {
            double a = HtmlUtil.parseDouble(request.getParameter("a"), "a");
            double b = HtmlUtil.parseDouble(request.getParameter("b"), "b");
            double h = HtmlUtil.parseDouble(request.getParameter("h"), "h");
            body = calculate(a, b, h);
        } catch (IllegalArgumentException ex) {
            body = HtmlUtil.error(ex.getMessage());
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(HtmlUtil.page("Завдання 2.2 - результат",
                    "<h1>Завдання 2.2</h1><p class=\"muted\">Функція: y = x<sup>0.2</sup></p>"
                            + body + "<p><a href=\"index.html\">&larr; На головну</a></p>"));
        }
    }

    private String calculate(double a, double b, double h) {
        if (h <= 0) {
            throw new IllegalArgumentException("Крок h має бути додатним");
        }
        if (a > b) {
            throw new IllegalArgumentException("Має виконуватись a <= b");
        }
        int count = FunctionTabulator.pointsCount(a, b, h);
        if (count > FunctionTabulator.MAX_POINTS) {
            throw new IllegalArgumentException("Забагато точок (" + count + "). Збільште крок h: максимум "
                    + FunctionTabulator.MAX_POINTS + " точок");
        }
        List<FunctionTabulator.Point> points = FunctionTabulator.tabulate(a, b, h);

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"card\"><h2>Графік</h2>")
                .append(SvgChart.build(points, "y = x^0.2, x in [" + fmt(a) + "; " + fmt(b) + "], h = " + fmt(h)))
                .append("</div>");

        sb.append("<div class=\"card\"><h2>Таблиця значень (").append(points.size()).append(" точок)</h2><table>");
        sb.append("<tr><th>№</th><th>x</th><th>y = x^0.2</th></tr>");
        int i = 1;
        for (FunctionTabulator.Point p : points) {
            sb.append("<tr><td>").append(i++).append("</td>");
            sb.append(String.format(Locale.US, "<td>%.4f</td>", p.x()));
            if (p.defined()) {
                sb.append(String.format(Locale.US, "<td>%.6f</td></tr>", p.y()));
            } else {
                sb.append("<td>не визначена (x &lt; 0)</td></tr>");
            }
        }
        sb.append("</table></div>");
        return sb.toString();
    }

    private String fmt(double v) {
        return new java.math.BigDecimal(String.valueOf(v)).stripTrailingZeros().toPlainString();
    }
}
