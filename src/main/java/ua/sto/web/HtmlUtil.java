package ua.sto.web;

/** Спільні фрагменти HTML-сторінок і допоміжні методи для сервлетів. */
public final class HtmlUtil {

    private HtmlUtil() {
    }

    public static String page(String title, String body) {
        return "<!DOCTYPE html>\n<html lang=\"uk\">\n<head>\n<meta charset=\"UTF-8\">\n"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
                + "<title>" + title + "</title>\n<style>\n"
                + "body{font-family:Segoe UI,Arial,sans-serif;margin:0;background:#f6f8fa;color:#24292f}\n"
                + "main{max-width:820px;margin:0 auto;padding:24px 16px}\n"
                + "h1{font-size:26px;margin:0 0 4px}\n"
                + "h2{font-size:18px;margin:24px 0 8px}\n"
                + ".card{background:#fff;border:1px solid #d0d7de;border-radius:8px;padding:16px 20px;margin:16px 0}\n"
                + "label{display:block;margin:10px 0 4px;font-size:14px}\n"
                + "input{width:100%;max-width:320px;padding:7px 9px;border:1px solid #8c959f;border-radius:6px;font-size:15px;box-sizing:border-box}\n"
                + "button{margin-top:14px;padding:8px 18px;border:0;border-radius:6px;background:#0969da;color:#fff;font-size:15px;cursor:pointer}\n"
                + "table{border-collapse:collapse;width:100%;font-size:14px}\n"
                + "th,td{border:1px solid #d0d7de;padding:6px 10px;text-align:right}\n"
                + "th{background:#f6f8fa}\n"
                + "td:first-child,th:first-child{text-align:left}\n"
                + ".err{background:#ffebe9;border:1px solid #ff8182;color:#82071e;padding:10px 14px;border-radius:6px}\n"
                + ".muted{color:#57606a;font-size:13px}\n"
                + "a{color:#0969da}\n"
                + "</style>\n</head>\n<body>\n<main>\n" + body + "\n</main>\n</body>\n</html>\n";
    }

    /** Розбір дійсного числа; дозволено і кому, і крапку як роздільник. */
    public static double parseDouble(String raw, String fieldName) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Не заповнено поле \"" + fieldName + "\"");
        }
        try {
            double v = Double.parseDouble(raw.trim().replace(',', '.'));
            if (Double.isNaN(v) || Double.isInfinite(v)) {
                throw new NumberFormatException();
            }
            return v;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Поле \"" + fieldName + "\": \"" + raw + "\" - не є числом");
        }
    }

    public static int parseInt(String raw, String fieldName) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Не заповнено поле \"" + fieldName + "\"");
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Поле \"" + fieldName + "\": \"" + raw + "\" - не є цілим числом");
        }
    }

    /** Екранування спеціальних символів HTML (захист від XSS при виводі введених даних). */
    public static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    public static String error(String message) {
        return "<div class=\"err\">" + escape(message) + "</div>";
    }
}
