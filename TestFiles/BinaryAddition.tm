# This is a Binary Addition program
# It accepts two binary numbers as inputs, and
# sums them together

# Start State for Tape 1 (start1) - get to the rightmost bit of tape1

t1, start, _, _, <, t1, start2;
t1, start, *, *, >, t1, start;

# Start State for Tape 2 (start2) - get to the rightmost bit of tape2

t2, start2, _, _, *<, t2, A;
t2, start2, *, *, *>, t2, start2;


# STATE A
t1, A, 0, 0, <, t1, B;
t1, A, 1, 1, <, t1, C;
t1, A, _, _, *, t1, endOfFirst;

# STATE B
t2, B, 0, 0, *<<, t3, A;
t2, B, 1, 1, *<<, t3, A;
t2, B, _, 0, **<, t3, endOfSecond;

# STATE C
t2, C, 0, 1, *<<, t3, A;
t2, C, 1, 0, *<<, t3, F;
t2, C, _, 1, *<<, t3, endOfSecond;

# STATE endOfFirst
t2, endOfFirst, 0, 0, *<<, t3, endOfFirst;
t2, endOfFirst, 1, 1, *<<, t3, endOfFirst;
t2, endOfFirst, _, _, ***, t2, done;

# STATE endOfSecond
t1, endOfSecond, 0, 0, <*<, t3, endOfSecond;
t1, endOfSecond, 1, 1, <*<, t3, endOfSecond;
t1, endOfSecond, _, _, *, t1, done;

# STATE done
t1, done, *, *, *, t1, acceptHalt;