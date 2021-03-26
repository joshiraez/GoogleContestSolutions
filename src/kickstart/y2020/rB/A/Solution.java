/*
Idea:
You can get the slopes getting the difference between the checkpoints, and then check for a change between ascending
and descending multiplying their values to check if they have different signs.

Sadly, although I like the idea, it is overengineered. A simple loop checking the neighbours checkpoints would suffice.
On top of that, we don't want to count the floors, only the peaks, so there is really no reason to get the slopes.

Data structures:
An array, array list, or anything where we can have arbitrary access in O(1) would suffice.

Algorithm:
Loop through the array, starting on the second element and finishing one before the last. At each element, check if
both sides have a lower value, and add one to an acumulator if that is the case.

Solution passes all test cases
 */
package kickstart.y2020.rB.A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {
    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws Exception {

        var cases = readInt();

        for (int caseN = 0; caseN < cases; caseN++) {
            in.readLine(); //skip number peaks
            var data =  readInts();
            System.out.println(reportCase(caseN, solve(data)));
        }
    }

    private static long solve(List<Integer> data) {
        // (I wish we had a zip operation in Java)
        // To find where are the peaks, we have to first calculate the slopes(differences), and then see which ones go
        // from positive to negative. We could multiply to look for sign change, but then we would get all changes
        // of upwards to downwards slopes, including the floors, and we are only interested in the peaks

        var slopes = new ArrayList<Integer>(data.size()-1);
        for (int checkpoint = 1; checkpoint < data.size(); checkpoint++) {
            slopes.add(data.get(checkpoint) - data.get(checkpoint-1));
        }

        var peaks = 0;
        for (int slope = 1; slope < slopes.size(); slope++) {
            if (slopes.get(slope-1) > 0 && slopes.get(slope) < 0) {
                peaks++;
            }
        }

        return peaks;
    }

    private static String reportCase(int caseNumber, long solution) {
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