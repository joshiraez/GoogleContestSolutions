/*
Idea:
Is clear that we don't care about the actual exercises, but their differences, so that should be the data unit we will
be working with. A first approach is to add exercises to the biggest difference to ease it, using a priority queue
to dequeue the hardest exercise, splitting it up in 2 halves, and enqueuing the 2 halves back.

This is good and dandy until you see the examples or take pen and paper. If you have 2 exercises with 4 extra exercises
left to place, your program would go like this

2  12   - Exercises
10      - Differences
[10]    - Priority queue
[5,5]   - First split
[5,3,2] - Second split
[3,3,2,2] - Third split
[3,2,2,1] - Fourth split

Huh, ok, the difficulty would be 3? Wrong. You take pen and paper, or check the examples, and you would see the optimal
would have been to have [2,2,2,2] (2, 4, 6, 8, 10, 12 for exercises). Panic ensues. O(n^n^ntimes). Destruction.

Wait. Let's toy something more. We don't care about the actual exercises, but what the max of them will be. How about
we just store the number of added exercises to a given difficulty, and every time we ease it, we add another exercise
and recalculate the max?

2 12    - Exercises
10      - Differences
[(10,0)]- Priority queue
[(5,1)] - First split (10 / (1+1) = 2)
[(3,2)] - Second split ( [10 / (1+2)] + 1 (because 10 % (1+2) != 0 so we have to add the remainder extra to our max)
[(3,3)] - Third split ( [10 / 4] + 1 )
[(2,4)] - Fourth split ( 10 / 5 )

Hooray! We now know that for that original difference, we can ease it down to a 2 difficulty by using 4 exercises. The
question is, What about when interacting with other exercises? Well, because in the end we only care about the max pos.
difference, ignoring the rest of the splits makes sense, because we still need to be easing only the hardest split.

Caveat: and what happens if we have many splits with the same max difficulty? The order doesn't matter: if we can't ease
all the splits that are tied for max, the difficulty of the set will still end being the difficulty of any splits not eased
in the tie, because they will remain being the max difficulty of the entire set. For example:

Given max 4 extra exercises
2 12 18     - Exercises
10 6        - Differences
[(10,0),(6,0)] - Priority queue
[(6,0),(5,1)] - First split
[(5,1),(3,1)] - Second split
[(3,2),(3,1)] - Third split
-- Notice that we have two with the same difficulty. We don't care the order we tackle them, because we don't have enough
splits to reduce them both to 2.
[(3,3),(3,1)] - Possible 4th split
[(3,2),(2,2)] - Another possible 4th split. You can notice that you are still left with the other 3.
-- Even if we had a theoretical 5th split, and went with the first option
[(3,1),(2,4)] - Yey, we managed to ease the packed one! But the other one lays still at difficulty 3.

So that's that. We just have to keep easing the first difficulty on the priority queue until we have no extra exercises left.
The top value at the queue will be the set difficulty, the hardest split left.

Data Structure:

We will be using a priority queue that has as the first element the difference with the max difficulty left after splitting
the difference with extra exercises.
For that end, we will use an object that stores the original difficulty, the added exercises, and the new calculated difficulty
(the bigger split after adding the extra exercises). We will use this calculated difficulty for sorting.

Algorithm:

Pick all the exercises and create the difference objects for them. Add all the differences to the priority queue.

EXCEPT: Don't add any who has a calculated difficulty of 1 now, or after easing. Why? Because you can't ease it, and you
can always add extra exercises at the end, or not add any more exercises, so those are not useful. If the priority
queue ends empty, you know the difficulty is one, without need for any extra calculations.

After doing so, just pick the first in the queue, ease it, and add it again to the queue. Continue doing so until
the queue is empty (difficulty is 1), or you have ran out of extra exercises to ease. If you ran out of exercises,
the difficulty will be whatever is the difficulty at the top of the queue.

Easing should use this formula for calculating the max difficulty in the created splits:

originalDifficulty / (1 + addedExercises)  [ +1 if the remainder of the division is not 0 ]



Solution passes all the tests
 */
package kickstart.y2020.rA.C;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class Solution {
    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException{
        var cases = readInt();

        for (int caseNumber = 0; caseNumber < cases; caseNumber++) {
            var maxAddedExercises = readInts().get(1);
            var exercises = readInts();
            var difficultyPriority = getDifficultyPriorityFromExercises(exercises);

            var difficulty = easeDifficulty(difficultyPriority, maxAddedExercises);

            System.out.println(solutionReport(caseNumber, difficulty));
        }
    }

    private static int easeDifficulty(final PriorityQueue<Difficulty> difficultyPriority, final int maxAddedExercises) {

        var exercisesLeft = maxAddedExercises;

        while (!difficultyPriority.isEmpty() && exercisesLeft != 0) {
            var difficultyToEase = difficultyPriority.poll();
            var easedDifficulty = difficultyToEase.addExercise();
            exercisesLeft = exercisesLeft - 1;

            if(easedDifficulty.currentDifficulty != 1) {
                difficultyPriority.add(easedDifficulty);
            }
        }

        if (difficultyPriority.isEmpty()) {
            return 1;
        }

        return difficultyPriority.peek().currentDifficulty;
    }

    private static PriorityQueue<Difficulty> getDifficultyPriorityFromExercises(List<Integer> exercises) {

        var difficulties = new ArrayList<Difficulty>(exercises.size());

        for (int currentExercise = 1; currentExercise < exercises.size(); currentExercise++) {
            var difficulty = exercises.get(currentExercise) - exercises.get(currentExercise-1);

            if (difficulty != 1) {
                difficulties.add(new Difficulty(difficulty, difficulty, 0));
            }
        }

        return new PriorityQueue<>(difficulties);
    }

    private static int readInt() throws IOException {
        return Integer.parseInt(in.readLine());
    }

    private static List<Integer> readInts() throws IOException {
        return Arrays.stream(in.readLine().split(" "))
                .map(Integer::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static String solutionReport(final int caseNumber, final int result) {
        return "Case #"+(caseNumber+1)+": "+result;
    }

    private static class Difficulty implements Comparable<Difficulty>{
        public final int originalDifficulty;
        public final int currentDifficulty;
        public final int numberAddedExercises;

        public Difficulty(final int originalDifficulty, final int currentDifficulty, final int numberAddedExercises) {
            this.originalDifficulty = originalDifficulty;
            this.currentDifficulty = currentDifficulty;
            this.numberAddedExercises = numberAddedExercises;
        }

        public Difficulty addExercise() {

            var newNumberAddedExercises = this.numberAddedExercises+1;

            var newDifficulty = this.originalDifficulty / (newNumberAddedExercises + 1);
            if (this.originalDifficulty % (newNumberAddedExercises + 1) != 0)
                newDifficulty++;

            return new Difficulty(this.originalDifficulty,
                    newDifficulty,
                    newNumberAddedExercises);
        }

        public int compareTo(Difficulty o) {
            return -Integer.compare(this.currentDifficulty, o.currentDifficulty);
        }
    }
}
