package symbolics.division.armistice.util;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;

/**
 * Easing functions for animations
 *
 * @see <a href="https://easings.net/">Easing functions</a>
 */
public enum Easing {
    LINEAR(t -> t),

    QUAD(t -> t * t),
    CUBIC(t -> t * t * t),
    QUART(t -> t * t * t * t),
    QUINT(t -> t * t * t * t * t),

    SINE(t -> 1 - Math.cos((t * Math.PI) / 2)),
    CIRC(t -> 1 - Math.sqrt(1 - t * t)),
    EXP(t -> Math.pow(2, 10 * (t - 1))),

    /**
     * Back easing function with an overshoot of 1.70158
     *
     * @see #back(double)
     */
    BACK(back(1.70158)),

    /**
     * Elastic easing function with an amplitude of 1 and a period of 0.3
     *
     * @see #elastic(double, double)
     */
    ELASTIC(elastic(1, 0.3)),
    BOUNCE(t -> {
        t = 1 - t;
        if (t < 1 / 2.75F) return 1 - 7.5625 * t * t;
        else if (t < 2 / 2.75F) return 1 - (7.5625 * (t -= 1.5F / 2.75F) * t + 0.75F);
        else if (t < 2.5F / 2.75F) return 1 - (7.5625 * (t -= 2.25F / 2.75F) * t + 0.9375F);
        else return 1 - (7.5625 * (t -= 2.625F / 2.75F) * t + 0.984375F);
    }); // TODO: Make this customizable and clean up the if mess

    final Double2DoubleFunction function;

    Easing(Double2DoubleFunction function) {
        this.function = function;
    }

    /**
     * Creates a polynomial easing function (e.g. t^2, t^3, t^4, etc.)
     *
     * @param power The power of the polynomial
     * @return The polynomial easing function
     * @apiNote Only use this for powers above 5, otherwise use the predefined easing functions
     * @see #QUAD
     * @see #CUBIC
     * @see #QUART
     * @see #QUINT
     */
    public static Double2DoubleFunction polynomial(int power) {
        return t -> Math.pow(t, power);
    }

    /**
     * Creates a back easing function with a custom overshoot
     *
     * @see #BACK
     */
    public static Double2DoubleFunction back(double s) {
        return t -> t * t * ((s + 1) * t - s);
    }

    /**
     * Creates an elastic easing function with custom amplitude and period
     *
     * @see #ELASTIC
     */
    public static Double2DoubleFunction elastic(double amplitude, double period) {
        double s;
        double a;

        if (amplitude < 1) {
            a = 1;
            s = period / 4;
        } else {
            a = amplitude;
            s = period / (2 * Math.PI) * Math.asin(1 / amplitude);
        }

        return t -> a * Math.pow(2, -10 * t) * Math.sin((t - s) * (2 * Math.PI) / period) + 1;
    }

    public double in(double t) {
        return function.get(t);
    }

    public double out(double t) {
        return 1 - in(1 - t);
    }

    public double inOut(double t) {
        return t < 0.5F ? in(t * 2) / 2 : 1 - in((1 - t) * 2) / 2;
    }

    public double in(double t, double start, double end, double duration) {
        return start + (end - start) * in(t / duration);
    }

    public double out(double t, double start, double end, double duration) {
        return start + (end - start) * out(t / duration);
    }

    public double inOut(double t, double start, double end, double duration) {
        return start + (end - start) * inOut(t / duration);
    }

    /**
     * Eases out from the start value to the middle value, then eases in to the end value
     *
     * @param t        The current time
     * @param start    The start value
     * @param mid      The middle value
     * @param duration The duration
     * @return The eased value
     */
    public double yoyoOutIn(double t, double start, double mid, double duration) {
        if (t < duration / 2) return out(t, start, mid, duration / 2);
        else return in(t - duration / 2, mid, start, duration / 2);
    }

    /**
     * Eases in from the start value to the middle value, then eases out to the end value
     *
     * @param t        The current time
     * @param start    The start value
     * @param mid      The middle value
     * @param duration The duration
     * @return The eased value
     */
    public double yoyoInOut(double t, double start, double mid, double duration) {
        if (t < duration / 2) return in(t, start, mid, duration / 2);
        else return out(t - duration / 2, mid, start, duration / 2);
    }
}