/*
Idea:
The number of wasted days by the last bus will always be the modulo from his time frecuency against the max day.
If we then subtract this modulo to get the date we get this bus, we get a new "max day" from which we have to get the
bus from before.
We can keep repeating this until we get to the first bus, to know which is the max day we can take it.

Data structure:
We can use a stack to pop the last read bus, but any kind of array or arraylist will suffice.

Algorithm:
Starting in the last bus, decrease the maxDay counter by the modulo of maxDay and the bus frecuency. Repeat with all the
buses in reverse order. The solution is the remaining maxDay after finishing with the first bus.

Solution passes all tests
 */

package kickstart.y2020.rB.B;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {
    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws Exception {

        var cases = readInt();

        for (int caseN = 0; caseN < cases; caseN++) {
            var maxDay = readLongs().get(1);
            var busFrecuencies =  readLongs();
            System.out.println(reportCase(caseN, solve(busFrecuencies, maxDay)));
        }
    }

    private static long solve(final List<Long> busFrecuencies, final long maxDay) {

        var remainingDays = maxDay;

        for (int busIndex = busFrecuencies.size() - 1; busIndex >= 0; busIndex--) {
            remainingDays -= remainingDays % busFrecuencies.get(busIndex);
        }

        return remainingDays;
    }

    private static String reportCase(final int caseNumber, final long solution) {
        return "Case #"+(caseNumber+1)+": "+solution;
    }

    private static int readInt() throws IOException {
        return Integer.parseInt(in.readLine());
    }

    private static List<Long> readLongs() throws IOException {
        return Arrays.stream(in.readLine().split(" "))
                .map(Long::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
