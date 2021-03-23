/*
Idea:
Whenever common prefixes or searching for a word comes in a problem, a trie is a good start.
But how to use it? Which could be a good strategy to decide which are the groups based on a trie?

Let's build the Trie for this problem

Group of 2
ABC
AB
C
BD
A
BA

* - A - B - C
  |
  - B -- D
  |    |
  - C  - A

Uh, okay. This is not very useful. Let's try adding which words are in each node.

* {all} - A {ABC, AB, A} - B {ABC, AB} - C {ABC}
          |
          - B {BD, BA} - D {BD}
          |            |
          - C {C}      - A {BA}

This not only looks better, we can already see groups naturally forming. We know we should be picking groups of 2.
Which ones though? The deepest one. We know, for sure, that will be the most optimal grouping for these words to
ensure the highest common prefix. Lets start picking {ABC, AB} then. We can remove these words from the trie as well.

{ABC, AB} - score 2
* {all} - A {A}
          |
          - B {BD, BA} - D {BD}
          |            |
          - C {C}      - A {BA}

We repeat this. The deepest group of 2 would be {BD, BA}, so we take them and remove them from the trie.

{ABC, AB} - score 2 (AB)
{BD, BA}  - score 1 (B)
* {A, C} - A {A}
          |
          - C {C}

Look how neat it ends. The last remaining words will be grouped for no score, but we know that the other groups
ensured the maximum score possible for those words, because they were the deepest in the tree - they had the largest
prefix in common possible. We can keep using the same algorithm - get the deepest group of 2 available, and remove from
the trie, which would be {A, C} at the root.

{ABC, AB} - score 2 (AB)
{BD , BA} - score 1 (B)
{A  , C } - score 0 ()

The answer would be 3. Wait, we don't even "care" about the words, so we can simplify this further and just store the
number of coinciding words. Any time there is enough coinciding words to form a group, we get it, and remove that number
from all parents in the trie, simbolizing the words we removed.

*(6) - A(3) - B(2) - C(1)
     |
     - B(2) - D(1)
     |      |
     - C(1) - A(1)

The algorithm is the same: go from the leafs to the root, taking any group that has enough words, scoring for them and
removing them from the parents.

---
Scores: 2

*(4) - A(1) - B(0) - C(1)
     |
     - B(2) - D(1)
     |      |
     - C(1) - A(1)
---
Scores: 2,1

*(2) - A(1) - B(0) - C(1)
     |
     - B(0) - D(1)
     |      |
     - C(1) - A(1)
---
Scores: 2,1,0 -> Final score is 3

*(0) - A(1) - B(0) - C(1)
     |
     - B(0) - D(1)
     |      |
     - C(1) - A(1)
---

So so long as we look for the score of the root, that being the sum of the score of their leafs plus the score of the node.
The score of a node would be the following: number of groups that can be done * depth in the trie.
And finally, remembering that any time we score, we have to "remove" the used words from the parents (they are spent) in
a group, should arrive us at the solution.

Data structure:

A trie, that will store in each node the number of coinciding words with that prefix.

Algorithm:

First, we will build the trie with all the words that arrive. We will be adding one to every node visited, to signal
that there is another word with a common prefix.

After that, we just have to calculate the score of the root. To calculate the score, we traverse the trie using post-order,
looking for the deepest possible (the groups with the most points) before going upwards.
* Any time we get into a leaf that has less words than the group required size, we can return 0 - none of its leaf will ever score
* If a node has enough words for a group, after having checked it's leaves, it can saffely assume he is the max possible
  scorer for those words. Add its score (groups possible * depth) to the score of the leaves, substract the number of
  words used from all the node's parents, and then return the total score of the node.

You can even ignore the root's own score, but it leaves the algorithm tidy.

The return value of the score of the root will be the max possible score grouping these words.


This solution passes all test cases.
 */


package kickstart.y2020.rA.D;

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
            var data = readInts();
            var words = data.get(0);
            var groupSize = data.get(1);
            var coincidingLetters = new CoincidingLetters();

            for (int i = 0; i < words; i++) {
                coincidingLetters.addWord(in.readLine());
            }

            System.out.println(reportCase(caseN, coincidingLetters.solve(groupSize)));
        }
    }

    private static int readInt() throws IOException {
        return Integer.parseInt(in.readLine());
    }

    private static String reportCase(int caseNumber, int solution) {
        return "Case #"+(caseNumber+1)+": "+solution;
    }

    private static List<Integer> readInts() throws IOException {
        return Arrays.stream(in.readLine().split(" "))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    private static class CoincidingLetters {
        public TrieNode root = new TrieNode(null);

        public void addWord(String word) {
            var charArray = word.toCharArray();
            var currentNode = root;
            char currentChar;
            currentNode.coincidingWords++;

            for (final char c : charArray) {
                currentNode = currentNode.getCharNode(c);
                currentNode.coincidingWords++;
            }
        }

        public int solve(int groupSize) {
            return root.getMaximumScoreForNode(groupSize, 0);
        }
    }

    private static class TrieNode {
        public final TrieNode parent;
        public final HashMap <Character, TrieNode> next = new HashMap<>();
        public int coincidingWords = 0;

        public TrieNode(TrieNode parent){
            this.parent = parent;
        }

        public TrieNode getCharNode(char character) {
            if (!next.containsKey(character)) next.put(character, new TrieNode(this));
            return next.get(character);
        }

        public void removeWords(int groupSize) {
            var currentNode = this;
            currentNode.coincidingWords -= groupSize;

            while(currentNode.parent != null) {
                currentNode = currentNode.parent;
                currentNode.coincidingWords -= groupSize;
            }
        }

        public int getMaximumScoreForNode(final int groupSize, final int currentDepth) {

            if (this.coincidingWords < groupSize) return 0;

            var maxScore = 0;

            for (TrieNode node : this.next.values()) {
                maxScore += node.getMaximumScoreForNode(groupSize, currentDepth+1);
            }

            var groups = this.coincidingWords/groupSize;

            maxScore += groups * currentDepth;
            this.removeWords(groups * groupSize);

            return maxScore;
        }
    }
}
