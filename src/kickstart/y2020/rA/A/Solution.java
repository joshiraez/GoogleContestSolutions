/*
Idea:
To know how many houses we can buy, we just need to buy the cheapest houses until we can't afford it.

Data Structure:
Storing the data in an array or list will suffice.

Algorithm:
Because we are looking for the minimum data, the best strategy is to sort the list in ascending order.
After that, we just start from the cheapest house and continue until we can't buy anymore.

Solution passes all tests
 */

package kickstart.y2020.rA.A;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws Exception {
        var cases = readInt();

        for (int i = 0; i < cases; i++) {
            var money = readInts().get(1);
            var houses = readInts().stream().sorted().collect(Collectors.toList());

            var moneyLeft = money;
            var housesBought = 0;
            for (Integer house : houses) {
                if (moneyLeft >= house){
                    moneyLeft -= house;
                    housesBought++;
                }
            }

            System.out.println("Case #"+(i+1)+": "+housesBought);

        }
    }

    private static int readInt() throws Exception{
        return Integer.parseInt(in.readLine());
    }

    private static List<Integer> readInts() throws Exception {
        return Arrays.stream(in.readLine().split(" ")).map(Integer::valueOf).collect(Collectors.toList());
    }
}
