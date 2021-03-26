/*
Idea:

Note: This took me two days of drawing until I realized that it was a binomial distribution. Wops. But was
a nice learning opportunity.

First, let's think what's the probability of reaching to any given square.

The number of ways to get to a Square, is the number of times we can decide to move Right instead of down over the
total movements we have to do to get to that square (which will be the same across a diagonal). This in combinatory
is a binomial of n over k, where n is nothing more than y (or x), and k is y + x.

Next, we have to divide by the total ways to get what is the probability of each square. Painting around we quickly
find that we have to divide by 2^(x+y) (or if you realize is a binomial distribution much earlier :DDD)

Now that we know what's the probability of reaching any given square, what is the total probability of not falling?

Imagine an example square:

[][][][]
[][][][]
[]XXXX[]
[][][][]

Because we know that, once we are past the hole, there is no need to turn back, we only care about the probability of
getting through the hole. And we can do that going to the right of it, or to the bottom of it.

[====][]        --[][][]
[====][]  or    ||[][][]
[]XXXX[]        --XXXX[]
[][][][]        [][][][]

For the horizontal rectangle, the probability of getting through is the sum of the probabilities of getting to the last
squares divided by 2. And for the vertical triangle, is the same. We sum the 2 probabilities and we have the total prob.
So we can get the probabilities of visiting those squares, add them and divide by 2. Nice.

The only edge cases is if the hole touches the right or bottom edge. In that case, we ignore its corresponding prob and
that's all.

...

That's all if it wasn't for a QUITE HUMONGOUS FORMULA that just coding it was making me see Time Limit Exceeded instead of
letters in my notebook. Although I think it could pass the first test case, well, I was not going to be really satisfied lol.
I tried implement Binomial probabilities with BigDecimal and BigInteger but that was going to be slow as hell...

So for the math part, I googled and found this amazing article
https://eliasnodland.wordpress.com/2020/05/09/google-kickstart-round-b-problem-d-wandering-robot/ where you can see
the rest of the history and what I'll be implementing here.

Once I have a good way to calculate every position probability, the rest of the algorithm is just following the idea I
explained before. The algorithm ends being very speedy (yey O(1) probability calculation) and the complexity is O(n)
therefore (you only have to check the 2 lines in the right and in the bottom and sum them).

Nice trick to know about how to cleverly use logarithms to escape from factorials, something new I learnt today :D

Test passes all test cases
 */

package kickstart.y2020.rB.D;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {
    private static final int MAX_X = (int)Math.pow(10,5);
    private static final int MAX_Y = (int)Math.pow(10,5);

    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static final double[] logFact = new double[MAX_X + MAX_Y];

    public static void main(String[] args) throws IOException {
        initializeLogFactNAccum();

        var cases = readInt();

        for (int caseN = 0; caseN < cases; caseN++) {
            var data = readInts();
            var maxX = data.get(0) - 1;
            var maxY = data.get(1) - 1;
            var topX = data.get(2) - 1;
            var topY = data.get(3) - 1;
            var botX = data.get(4) - 1;
            var botY = data.get(5) - 1;

            var probability = solve(maxX, maxY, topX, topY, botX, botY);
            System.out.println(reportCase(caseN, probability));
        }
    }

    private static void initializeLogFactNAccum() {
        logFact[0] = 0;
        logFact[1] = 0;
        logFact[2] = Math.log(2);

        for (int i = 3; i < logFact.length; i++) {
            logFact[i] = logFact[i-1] + Math.log(i);
        }
    }

    private static double solve(final int maxX, final int maxY, final int topX, final int topY, final int botX, final int botY) {

        var escapingRightSideProb = 0.;
        var escapingBottomSideProb= 0.;

        if (botX != maxX) {
            escapingRightSideProb = calculateProbabilityOfPath(botX,0,botX,topY-1);
        }
        if (botY != maxY) {
            escapingBottomSideProb= calculateProbabilityOfPath(0,botY,topX-1,botY);
        }

        return (escapingBottomSideProb+escapingRightSideProb)/2;
    }

    private static double calculateProbabilityOfPath(final int topX, final int topY, final int botX, final int botY) {
        var prob = 0.;

        for (var x = topX; x <= botX; x++) {
            for (var y = topY; y <= botY; y++) {
                prob += probabilityOfLandingInSquare(x,y);
            }
        }

        return prob;
    }

    private static double probabilityOfLandingInSquare(int x, int y) {
        return Math.exp(logFact[x+y] - logFact[y] - logFact[x] - (x+y)*Math.log(2));
    }


    private static String reportCase(int caseNumber, double solution) {
        return "Case #"+(caseNumber+1)+": "+solution;
    }

    private static int readInt() throws IOException {
        return Integer.parseInt(in.readLine());
    }

    private static List<Integer> readInts() throws IOException {
        return Arrays.stream(in.readLine().split(" "))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }
}
