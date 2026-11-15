package ua.sto.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * Завдання 2.1. Обчислення суми ряду arctg x для x, n, e, що вводяться через форму.
 * Параметри приходять як параметри GET-запиту: /task1?x=0.5&n=10&e=0.001
 */
@WebServlet(name = "Task1Servlet", urlPatterns = "/task1")
public class Task1Servlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String body;
        try {
            double x = HtmlUtil.parseDouble(request.getParameter("x"), "x");
            int n = HtmlUtil.parseInt(request.getParameter("n"), "n");
            double e = HtmlUtil.parseDouble(request.getParameter("e"), "e");
            body = calculate(x, n, e);
        } catch (IllegalArgumentException ex) {
            body = HtmlUtil.error(ex.getMessage());
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(HtmlUtil.page("Завдання 2.1 - результат",
                    "<h1>Завдання 2.1</h1><p class=\"muted\">Ряд: arctg x = x - x<sup>3</sup>/3 + x<sup>5</sup>/5 - x<sup>7</sup>/7 + ... (R = 1)</p>"
                            + body + "<p><a href=\"index.html\">&larr; На головну</a></p>"));
        }
    }

    private String calculate(double x, int n, double e) {
        if (Math.abs(x) >= 1) {
            throw new IllegalArgumentException("x має належати інтервалу (-1; 1), бо радіус збіжності ряду R = 1");
        }
        if (n < 1 || n > ArctgSeries.MAX_TERMS) {
            throw new IllegalArgumentException("n має бути цілим числом від 1 до " + ArctgSeries.MAX_TERMS);
        }
        if (e <= 0 || e >= 1) {
            throw new IllegalArgumentException("e має бути додатним числом, меншим за 1 (наприклад, 0.001)");
        }
        double e2 = e / 10; // друге значення e, що відрізняється від першого на порядок

        double exact = ArctgSeries.exact(x);
        ArctgSeries.Result a = ArctgSeries.sumFirstN(x, n);
        ArctgSeries.Result b1 = ArctgSeries.sumTermsGreaterThan(x, e);
        ArctgSeries.Result b2 = ArctgSeries.sumTermsGreaterThan(x, e2);

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"card\"><h2>Вхідні дані</h2>");
        sb.append(String.format(Locale.US, "<p>x = %s; n = %d; e<sub>1</sub> = %s; e<sub>2</sub> = e<sub>1</sub>/10 = %s</p></div>",
                fmt(x), n, fmt(e), fmt(e2)));

        sb.append("<div class=\"card\"><h2>Результати</h2><table>");
        sb.append("<tr><th>Пункт</th><th>Сума ряду</th><th>Кількість доданків</th><th>|похибка|</th></tr>");
        row(sb, "а) перші n доданків (n = " + n + ")", a, exact);
        row(sb, "б) |доданок| &gt; e<sub>1</sub> = " + fmt(e), b1, exact);
        row(sb, "б) |доданок| &gt; e<sub>2</sub> = " + fmt(e2), b2, exact);
        sb.append(String.format(Locale.US,
                "<tr><td>в) \"точне\" значення Math.atan(x)</td><td>%.12f</td><td>-</td><td>-</td></tr>", exact));
        sb.append("</table>");
        sb.append("<p class=\"muted\">Похибка = |сума ряду - arctg x|. Зі зменшенням e кількість доданків зростає, а похибка зменшується.</p></div>");
        return sb.toString();
    }

    private void row(StringBuilder sb, String title, ArctgSeries.Result r, double exact) {
        sb.append(String.format(Locale.US, "<tr><td>%s</td><td>%.12f</td><td>%d</td><td>%.3e</td></tr>",
                title, r.sum(), r.termsCount(), Math.abs(r.sum() - exact)));
    }

    private String fmt(double v) {
        return new java.math.BigDecimal(String.valueOf(v)).stripTrailingZeros().toPlainString();
    }
}
