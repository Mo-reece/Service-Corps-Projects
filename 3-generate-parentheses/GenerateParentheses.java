import java.util.ArrayList;
import java.util.List;

public class GenerateParentheses {

    public static List<String> generateParentheses(int n) {
        List<String> result = new ArrayList<>();
        build("", 0, 0, n, result);
        return result;
    }

    // Backtracking: grow the string one character at a time, only making moves that keep it valid.
    private static void build(String current, int open, int close, int n, List<String> result) {
        if (current.length() == 2 * n) {          // used all n pairs -> a complete, valid answer
            result.add(current);
            return;
        }
        if (open < n) {                            // rule 1: we can open while we still have '(' left
            build(current + "(", open + 1, close, n, result);
        }
        if (close < open) {                        // rule 2: we can only close a '(' that is still open
            build(current + ")", open, close + 1, n, result);
        }
    }

    public static void main(String[] args) {
        int n = args.length > 0 ? Integer.parseInt(args[0]) : 3;
        System.out.println(generateParentheses(n));
    }
}
