/*
Idea:
From the start, we can see that the solution will be a combination of numberStack numbers with values between
[0-stackSize], where the sum of those numbers equal the maxPlates that the problem gives us. So two naive solutions
would be to look for all the combinations either using loops or recursion and pick up the max possible.
Yet those two ways are terribly slow once the number and size of the stacks increase.

Usually, problems like this are solved saving the mid calculations to an appropriate data structure from where we
can retrieve the already computed values. But what are the keys and values we want to save?

The key to the problem lies in that, at a given state, not all states can or will be valid states. Lets take for example
a really small problem example

maxPlates = 4
plates:
2 20 7 - stack 1
5 8 18 - stack 2

Let's try to minimize this problem. Let's focus only on the last stack, and the remaining plates.
* Let's think we are left with 3 plates when we are in the stack 2. The optimum choice would be to just pick the third
  plate, which will give us the most value.
* For 2, 1 and 0 plates the idea is the same, because in a given stack, the value of the plates increases strictly with
  the number of them.
* Finally, if we had 4 plates left, it wouldn't be a valid combination at all, so we should ignore it.

This is a nice start. But what about stack 1? The problem would be to know which is the max score with 4 plates left at
the first stack, right?

* Let's pretend we were left with 1 plate in the first stack. The max score would be to either take the value for the
  first plate on this stack, or trust in the next stack and pick the max possible score for them when they are left
  with one plate. Between 2 (value of 1 plate in this stack) and 5 ( the max possible value in the next stack with 1 plate),
  we would pick 0 plates in this stack, and 1 from the next stacks. Interesting. We can assume that any time we have
  1 plate left in the stack 1, we know which would be the max value - to pick the plate of the next stack.
* What about having 2 left? We know from before that two options would be to pick 2 plates in this stack, or pick the max
  possible score when having 2 plates remaining on the next. But what if we were to pick 1 plate here, and leave the other
  for the next stacks? That's an option too! You start seeing a pattern here: we should pick the max between the pairs
  (currStack, maxNextStack) from 0-remainingPlates. And this max is always the same, given the stack and the remaining plates!
* If we go directly to get the max on stack 1 when having 4 plates left, you already know the formula: max(f(0,4), f(1,3),
  f(2,2), f(3,1) and f(4,0)) where f is stackValue(pair[0]) + maxValueNextStack(pair[1]).
* Not all pairs are valid though! We shouldn't look at any f where either stackValue or maxValueNextStack are invalid
  (for example, stackValue(4) doesn't exist, plus maxValueNextStack(4) won't give us enough plates to reach the cap.

Furthermore, we will only have to calculate the maxValues of the partial maxes once, if we store them using the stack
and the remaining plates as keys. We know that they are set in stone, so just calculating them once will suffice.
We can do some precalculation to avoid having to sum all the plates to get stackValue, saving in a stack plate matrix
the value sum of all the plates that you would pick if you picked a given plate.

With the above formula, plus knowing what and how we should save the intermediate results to avoid recalculating, it is
fast enough to deal with the second data set.

Data Structure:
We will be using matrices extensively.
* We will not only have an initial matrix of stack-plate to store the initial data,
but we will precalculate on it to get an normalized matrix were we have the total value that plate would give instead
(which would be the sum of the plate and all before on the stack). For code sake, we add another column to this normalized
matrix adding the 0 column with 0s, which would mean
* Also, we will want another stack-remainingPlates matrix, which will save the partial maxes we could possibly obtain
given the [stack,remainingPlates] pair of conditions. This matrix should be initialized with sizes [stacks][maxPlates],
or you can add an extra column [maxPlates+1] to simplify the code (it will access a 0 when querying for 0 plates).

Algorithm:
We have 2 functions:

* Val(stack,plate) - Value of plate in the current stack
* Max(stack,plateRemaining) - Maximum possible on stack with plateRemaining left

After normalizing the plate values like explained in the data structure, Val is just the value in the matrix at the
given keys. Or INVALID if the keys are out of bounds.

For the Max(s,p) though, we generalize the pattern we found when exploring the idea:

max( f(0, p, s), f(1, p-1, s), ... f(p, 0, s))

Where f(plate, plateRemaining, stack) equals

Val(stack, plate) + Max(stackRemaining, plate) -> if Val and Max are not INVALID, else INVALID
[and the max[INVALID...] is INVALID as well]

We should store the calculated maxes in the other matrix we use to store the partial results, so we can try to access
the value before trying to calculate it.

For INVALID, I used -1 as a token number.

Given these two Val and Max functions, and with the appropiate two matrices for storing the summed plate values,
and the possible maxes, solving a given problem is just calling for Max(0, maxPlates) (given that your stacks
start with 0 index). This is fast enough to get the large datasets.


Solution passes all tests
 */

package kickstart.y2020.rA.B;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {
    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws Exception {
        int cases = Integer.parseInt(in.readLine());

        for (int i = 0; i < cases; i++) {
            var data = readInts();

            var numberStacks = data.get(0);
            var numberPlates = data.get(1);
            var maxPlates = data.get(2);

            var plates = getPlates(numberStacks, numberPlates);
            var normalizedPlates = normalizePlates(plates);
            var cachedMaxes = new int[numberStacks][maxPlates+1];

            var maxPossible = max(0, maxPlates, normalizedPlates, cachedMaxes);

            System.out.println("Case #"+(i+1)+": "+maxPossible);
        }
    }

    private static int max(int stack, int remainingPlates, int[][] normalizedPlates, int[][] cachedMaxes) {
        // Gets the max among all possible choices to the stack being checked and the remaining plates
        // If it's not cached, it will attempt to retrieve it recursively

        if (remainingPlates == 0) return 0;
        if (stack >= normalizedPlates.length) return -1;

        var cachedMax = cachedMaxes[stack][remainingPlates];
        if (cachedMax != 0) return cachedMax;

        var max = -1;

        for (int i = 0; i <= remainingPlates; i++) {

            var stackVal = val(stack, i, normalizedPlates);
            var maxVal = max(stack + 1,remainingPlates-i, normalizedPlates, cachedMaxes);

            if (stackVal != -1 && maxVal != -1) {
                var possibleMax = stackVal + maxVal;

                if (possibleMax > max) {
                    max = possibleMax;
                }
            }
        }

        cachedMaxes[stack][remainingPlates] = max;

        return max;
    }

    private static int val(int stack, int plate, int[][] normalizedPlates) {
        //Retrieves the value of the stack indicated at the plate given.

        if(plate >= normalizedPlates[stack].length) return -1;

        return normalizedPlates[stack][plate];
    }

    private static int[][] normalizePlates(int[][] plates) {
        // Normalize the stacks so you can retrieve the total value with all the plates added
        // (and adding the 0 index for 0 plates)

        var normalizedPlates = new int[plates.length][plates[0].length+1];

        for (int stack = 0; stack < normalizedPlates.length; stack++) {
            for (int plate = 0; plate < normalizedPlates[0].length; plate++) {
                if (plate == 0) normalizedPlates[stack][plate] = 0;
                else normalizedPlates[stack][plate] = normalizedPlates[stack][plate-1] + plates[stack][plate-1];
            }
        }

        return normalizedPlates;
    }

    private static int[][] getPlates(int numberStacks, int numberPlates) throws IOException {

        var plates = new int[numberStacks][numberPlates];

        for (int i = 0; i < numberStacks; i++) {
            var stack = readInts();
            for (int j = 0; j < numberPlates; j++) {
                plates[i][j] = stack.get(j);
            }
        }

        return plates;
    }

    private static List<Integer> readInts() throws IOException {
        return Arrays.stream(in.readLine().split(" "))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }


}
