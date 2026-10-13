# Generate Parentheses

Given `n`, generate every combination of `n` pairs of well-formed parentheses.

```
generateParentheses(3)
→ ["((()))", "(()())", "(())()", "()(())", "()()()"]
```

## Run

Requires Java 11 or newer. No build tool is needed.

```bash
java GenerateParentheses.java 3
```

```
[((())), (()()), (())(), ()(()), ()()()]
```

Pass any `n` as the argument (the default is 3).

## Approach: backtracking

Build each string one character at a time, and only make moves that keep it valid:

1. **Add `(`** if fewer than `n` have been opened.
2. **Add `)`** only if it closes an open `(`, i.e. `close < open`.
3. **When the length reaches `2n`**, the string is a complete, valid answer.

Invalid strings are never built, so there's nothing to generate and throw away afterwards.

```
                    ""
                    (
            ((              ()
        (((    (()          ()(
        ...    ...          ...
```

## Complexity

The number of valid combinations is the n-th [Catalan number](https://en.wikipedia.org/wiki/Catalan_number)
(1, 2, 5, 14, 42, …).

- **Time:** O(4ⁿ / √n), the size of the output
- **Space:** O(n) recursion depth, not counting the output

## Verified

| n | Results | Expected (Catalan) |
|---|---------|--------------------|
| 1 | 1       | 1  |
| 3 | 5 (matches the example exactly) | 5  |
| 4 | 14      | 14 |
