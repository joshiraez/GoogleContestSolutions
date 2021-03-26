/*
Idea:

We will have to implement a very simple parser, and just executing every letter as you see them seems fit.
In this parser we will have:
N,E,W,S - Movements
2-9     - Start subroutine, where we will multiply the movement by this number
)       - End subroutine, returning to the previous multiplier

This should allow us for an O(n) solution, and we can use a Stack (or recursive functions) to return to the previous
multiplier. A solution like that will easily net the first test correctly.

The problem in this program lies in the subroutine multiplications. They can get VERY large, to the point of overflowing
longs. I have to thank this post https://discuss.codechef.com/t/google-kickstart-round-b-problem-name-robot-path-decoding/62918/13
to see some examples where it will crash.

You can try running

1
9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N9(N)))))))))))))))))))))))

And see if you get Case #1: 1 936267082 (or check the rest of the examples in the post)

Another problem comes when calculating the movement. 10^9 fits in an int, and you should always be calculating the
modulo of the movement in case you loop over the planet. But is close enough that even any moderate size multiplication
could overflow it. We must ensure that we always do the additions and multiplications using long (or any kind of
integer representation with sufficient memory) before doing the modulo.

And one last problem is that some languages implement the remainder, not the modulo, but that is not a problem with java.

The second and third problem are ... simple enough? As long as you realize them or debug them (although that's part of
the difficulty). But we still need to change how we deal with subprograms. How could we do that?

We just need to change the paradigm a little. With the above idea, we are directly applying the effects of a subprogram
into its contents. But that can become too large, too fast.

Instead, we will delay the multiplication until we exit the subprogram.

This means we have to save how much movement we had before entering a subprogram, and what multiplier the subprogram has.
We restart the "movement" of each subprogram to 0,0, and execute the letters just like if it were a brand new program.
Once we get to ')', we exit the subprogram, and THEN is when we multiply the movement of that subprogram by its multiplier.
This ensures our multiplier will never be higher than 9. As a last step, we add the previous' subprogram movement to the
result of the subprogram we just exited, and then we modulo it by the planet length, to take into account the looping
and ensuring our "movement" will always be in the range [0, 10^9]. We continue doing this until we finish the input,
and the resulting movement is our answer (after doing the necessary adjustments to properly count with the difference
with the 1-index coordinates, and -1 being the 10^9 position).

To make it more simpler, let's see a trace. (Notice we can use a stack, or use a recursive function just the same)

N2(SE4(W)S)E  | Action | Movement | Stack <mult, prev. movement>
N-------------| y -= 1 |  ( 0,-1) | []
 2------------| SUBRx2 |  ( 0, 0) | [{2,(0,-1)}]  -> We push the old movement, and the mult. to the stack
  (-----------|  Skip  |  ( 0, 0) | [{2,(0,-1)}]
   S----------| y += 1 |  ( 0, 1) | [{2,(0,-1)}]
    E---------| x += 1 |  ( 1, 1) | [{2,(0,-1)}]
     4--------| SUBRx4 |  ( 0, 0) | [{2,(0,-1)},{4,(1,1)}] -> We push again, with the movement we had in that subroutine
      (-------|  Skip  |  ( 0, 0) | [{2,(0,-1)},{4,(1,1)}]
       W------| x -= 1 |  (-1, 0) | [{2,(0,-1)},{4,(1,1)}]
        )-----| Return |  (-3, 1) | [{2,(0,-1)}] --------> | We pop the last elem with the previous routine movement and the mult.
         S----| y += 1 |  (-3, 2) | [{2,(0,-1)}]           | Now we calculate the resulting movement from the subroutine.
          )---| Return |  (-6, 3) | [] -----------|        | (-3, 1) = ( (1+-1*4)%10^9, (1+0*4)%10^9)
           E--| x += 1 |  (-5, 3) | []            |        | ((OldM.x + RetM.x * Mult) % Max, (OldM.y + RetM.y * Mult) % Max)
                                                  |
  (-5, 3) which is 99999996 4                     |------> | (-6, 3) = ( (0+-3*2)%10^9, (-1+2*2)%10^9 )
   -5 -> 10^9-5 + 1
    3  -> 3 + 1

Data Structure:

We can use a stack, but we can also use the system's stack just making a recursive function.

Algorithm:

Starting from a movement of (0,0)
Read a letter, then:
N,S,E,W -> Change movement accordingly
2-9     -> Store current movement and multiplier in the stack. Reset movement to 0,0.
)       -> Pop last element from the stack. Movement becomes ( (oldM.x + currM.x * mult)%MAX, (oldM.y + currM.t * mult)%MAX)

 */
package kickstart.y2020.rB.C;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

public class Solution {
    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static final int PLANET_LENGTH = (int)Math.pow(10,9);
    public static void main(String[] args) throws IOException {
        var cases = readInt();

        for (int caseN = 0; caseN < cases; caseN++) {
            var solution = solve(in.readLine().toCharArray());
            System.out.println(reportCase(caseN, solution));
        }
    }

    private static int[] solve(final char[] program) {

        var currentMovement = new Movement();
        var subprogramsWithMovement = new Stack<Subprogram>();

        for (char read : program) {
            //Handle subprograms multipliers
            if (read >= '2' && read <= '9') {
                var number = read - '0';

                subprogramsWithMovement.push(new Subprogram(number, currentMovement));

                currentMovement = new Movement();
            }

            //Handle subprogram finish
            if (read == ')') {
                currentMovement = subprogramsWithMovement.pop().executeSubprogram(currentMovement);
            }

            //Handle generic movement
            currentMovement.executeMove(read);
        }

        return currentMovement.getCoordinates();
    }

    private static String reportCase(final int caseNumber, final int[] solution) {
        return "Case #"+(caseNumber+1)+": "+solution[0]+" "+solution[1];
    }

    private static int readInt() throws IOException {
        return Integer.parseInt(in.readLine());
    }

    private static class Subprogram {
        public int multiplier;
        public Movement previousProgramMovement;

        public Subprogram(final int multiplier, final Movement previousProgramStoredMovement) {
            this.multiplier = multiplier;
            this.previousProgramMovement = previousProgramStoredMovement;
        }

        public Movement executeSubprogram(final Movement subprogramMovement) {
            // We need to cast the multiplier * movement to long because it can overflow int.
            // We later can safely downcast to int after the modulo.
            previousProgramMovement.x = (int)(((long)multiplier * subprogramMovement.x + previousProgramMovement.x) % PLANET_LENGTH);
            previousProgramMovement.y = (int)(((long)multiplier * subprogramMovement.y + previousProgramMovement.y) % PLANET_LENGTH);

            return previousProgramMovement;
        }
    }

    public static class Movement {
        public int x;
        public int y;

        public int[] getCoordinates() {
            var coordinates = new int[] {x,y};
            //Handle negative values
            for (int i = 0; i < coordinates.length; i++) {
                if (coordinates[i] < 0) {
                    coordinates[i] = PLANET_LENGTH + coordinates[i] + 1;
                } else {
                    coordinates[i] += 1;
                }
            }
            return coordinates;
        }

        public void executeMove(char move) {
            switch(move) {
                case 'N':
                    this.moveNorth();
                    break;
                case 'S':
                    this.moveSouth();
                    break;
                case 'W':
                    this.moveWest();
                    break;
                case 'E':
                    this.moveEast();
                    break;
            }
        }

        public void moveNorth() {
            this.y = (this.y - 1) % PLANET_LENGTH;
        }

        public void moveSouth() {
            this.y = (this.y + 1) % PLANET_LENGTH;
        }

        public void moveWest() {
            this.x = (this.x - 1) % PLANET_LENGTH;
        }

        public void moveEast() {
            this.x = (this.x + 1) % PLANET_LENGTH;
        }
    }
}
