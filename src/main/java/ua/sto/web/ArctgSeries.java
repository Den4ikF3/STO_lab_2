package ua.sto.web;

/**
 * Ряд для арктангенса (варіант 4):
 *
 *   arctg x = x - x^3/3 + x^5/5 - x^7/7 + x^9/9 - ...   (R = 1)
 *
 * k-й доданок (k = 0, 1, 2, ...):  a_k = (-1)^k * x^(2k+1) / (2k+1).
 * Ряд збігається для |x| < 1 (інтервал (-R; R), R = 1).
 */
public final class ArctgSeries {

    /** Захист від надто довгих обчислень (наприклад, при дуже малому e). */
    public static final int MAX_TERMS = 20_000_000;

    /** Результат підсумовування: сума та кількість використаних доданків. */
    public record Result(double sum, int termsCount) {
    }

    private ArctgSeries() {
    }

    /** k-й доданок ряду (k починається з 0). */
    public static double term(double x, int k) {
        double sign = (k % 2 == 0) ? 1.0 : -1.0;
        return sign * Math.pow(x, 2L * k + 1) / (2L * k + 1);
    }

    /** а) Сума перших n доданків. */
    public static Result sumFirstN(double x, int n) {
        double sum = 0.0;
        for (int k = 0; k < n; k++) {
            sum += term(x, k);
        }
        return new Result(sum, n);
    }

    /**
     * б) Сума тих доданків, які за модулем більші за e.
     * Для |x| < 1 модулі доданків монотонно спадають, тому підсумовування
     * завершується на першому доданку, що не перевищує e.
     */
    public static Result sumTermsGreaterThan(double x, double e) {
        double sum = 0.0;
        int count = 0;
        while (count < MAX_TERMS) {
            double t = term(x, count);
            if (Math.abs(t) <= e) {
                break;
            }
            sum += t;
            count++;
        }
        return new Result(sum, count);
    }

    /** "Точне" значення функції для порівняння. */
    public static double exact(double x) {
        return Math.atan(x);
    }
}
