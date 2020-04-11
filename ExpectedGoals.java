/**
 * This application computes the expected goals and distribution of the outcome of an example given in the
 * book <i>The Expected Goals Philosophy</i> by James Tippett. In the book, the distribution is computed by
 * simulating the game many times, but here we compute it analytically.
 *
 * Written by Jonas Lindstrøm (mail@jonaslindstrom.dk).
 */
import java.util.Arrays;

public class ExpectedGoals {

    /**
     * Given the parameters of two independent Poisson binomial distributed random variables <i>A</i> and <i>B</i>,
     * this methods computes the probabilities that <i>A > B</i>, <i>A = B</i> and <i>A < B</i> resp.
     *
     * @param a The parameters for <i>A</i>.
     * @param b The parameters for <i>B</i>.
     * @return An array <i>{P(A > B), P(A = B), P(A < B)}</i>.
     */
    public static double[] computeOutcomeDistribution(double[] a, double[] b) {
        double[] aDistribution = computeDistribution(a);
        double[] bDistribution = computeDistribution(b);

        // The below loop assumes that B has more experiments (shots). If that is not the case, we switch the distributions.
        if (a.length > b.length) {
            double[] reverseOutcome = computeOutcomeDistribution(b, a);
            return new double[] {reverseOutcome[2], reverseOutcome[1], reverseOutcome[0]};
        }

        // Probability of A > B, A = B and A < B resp.
        double w = 0;
        double d = 0;
        double l = 0;

        for (int i = 0; i < aDistribution.length; i++) {
            d += aDistribution[i] * bDistribution[i];

            for (int j = i - 1; j >= 0; j--) {
                w += aDistribution[i] * bDistribution[j];
            }

            for (int j = i + 1; j < b.length; j++) {
                l += aDistribution[i] * bDistribution[j];
            }
        }

        return new double[] {w, d, l};
    }

    /**
     * Compute the probability mass function for a Poisson binomial distribution with the given parameters.
     *
     * @param p The parameters for the given distribution.
     * @return The probability mass function as an array with the <i>k</i>'th entry representing the probability that
     * a random variable with the given distribution equals <i>k</i>.
     */
    public static double[] computeDistribution(double[] p) {
        // Compute the probability mass function according to the recursive method from
        // https://en.wikipedia.org/wiki/Poisson_binomial_distribution#Probability_mass_function.

        double[] T = computeT(p);

        double[] P = new double[p.length + 1];

        P[0] = 1.0;
        for (double pi : p) {
            P[0] *= 1.0 - pi;
        }

        for (int k = 1; k < P.length; k++) {
            for (int i = 1; i <= k; i++) {
                double term = T[i] * P[k - i];
                P[k] += i % 2 == 1 ? term : -term;
            }
            P[k] /= k;
        }

        return P;
    }

    /**
     * The <i>T</i>-function from the recursive formula in
     * https://en.wikipedia.org/wiki/Poisson_binomial_distribution#Probability_mass_function.
     *
     * @param p The parameters for the given Poisson binomial distribution.
     * @return The <i>T</i>-function for the given distribution.
     */
    private static double[] computeT(double[] p) {
        double[] T = new double[p.length + 1];

        for (int i = 0; i < T.length; i++) {
            for (double pj : p) {
                T[i] += Math.pow(pj / (1.0 - pj), i);
            }
        }

        return T;
    }

    public static void main(String[] arguments) {

        // Arsenals shots
        double[] a = new double[] {0.02, 0.02, 0.03, 0.04, 0.04, 0.05, 0.06, 0.07, 0.09, 0.10, 0.12, 0.13, 0.76};
        double xGa = 0.0;
        for (double ai : a) {
            xGa += ai;
        }

        // Manchester Uniteds shots
        double[] mu = new double[] {0.01, 0.02, 0.02, 0.02, 0.03, 0.05, 0.05, 0.05, 0.06, 0.22, 0.30, 0.43, 0.48, 0.63};
        double xGmu = 0.0;
        for (double mui : mu) {
            xGmu += mui;
        }

        System.out.println("Arsenal xG = " + xGa);
        System.out.println("Manchester United xG = " + xGmu);

        // Compute the probability of each possible outcome of the game
        double[] outcome = computeOutcomeDistribution(a, mu);
        System.out.println("W/D/L distribution for Arsenal: " + Arrays.toString(outcome));

        // Compute expected points
        double xPa = outcome[0] * 3 + outcome[1] * 1;
        double xPmu = outcome[2] * 3 + outcome[1] * 1;

        System.out.println("Arsenal xP: " + xPa);
        System.out.println("Manchester United xP: " + xPmu);
    }

}
