# This is a Binary Addition program
# It accepts two binary numbers as inputs for tape 1 and tape 2,
# sums them together onto tape 3
input: 100000101, 1110;

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
t2, C, 1, 0, *<<, t3, Carry;
t2, C, _, 1, *<<, t3, endOfSecond;

# STATE Carry
t1, Carry, 0, 0, <, t1, Carry0;
t1, Carry, 1, 1, <, t1, Carry1;
t1, Carry, _, _, *, t1, endOfFirstCarry;

# STATE Carry0
t2, Carry0, 0, 1, *<<, t3, A;
t2, Carry0, 1, 0, *<<, t3, Carry;
t2, Carry0, _, 1, **<, t3, endOfSecond;

# STATE Carry1
t2, Carry1, 0, 0, *<<, t3, Carry;
t2, Carry1, 1, 1, *<<, t3, Carry;
t2, Carry1, _, 0, **<, t3, endOfSecondCarry;

# STATE endOfFirstCarry
t2, endOfFirstCarry, 0, 1, *<<, t3, endOfFirst;
t2, endOfFirstCarry, 1, 0, *<<, t3, endOfFirstCarry;
t2, endOfFirstCarry, _, 1, **<, t3, acceptHalt;

# STATE endOfSecondCarry
t1, endOfSecondCarry, 0, 1, <*<, t3, endOfSecond;
t1, endOfSecondCarry, 1, 0, <*<, t3, acceptHalt;

# STATE endOfFirst
t2, endOfFirst, 0, 0, *<<, t3, endOfFirst;
t2, endOfFirst, 1, 1, *<<, t3, endOfFirst;
t2, endOfFirst, _, _, ***, t2, acceptHalt;

# STATE endOfSecond
t1, endOfSecond, 0, 0, <*<, t3, endOfSecond;
t1, endOfSecond, 1, 1, <*<, t3, endOfSecond;
t1, endOfSecond, _, _, *, t1, acceptHalt;
