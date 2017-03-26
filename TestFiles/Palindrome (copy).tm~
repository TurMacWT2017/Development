# This example program checks if the input string is a binary palindrome.
# Input: a string of 0's and 1's, eg '1001001'

# Machine starts in state 0.



# State 0: read the leftmost symbol

t1, 0, 0, _, R, *, A;
t1, 0, 1, _, >, *, A2;
t1, 0, _, _, *, *, acceptHalt;



# State A, A2: find the rightmost symbol

t1, A, _, _, left, *, B;
t1, A, *, *, >, *, A;

t1, A2, _, _, <, *, B2;
t1, A2, *, *, >, *, A2;



# State B, B2: check if the rightmost symbol matches the most recently read left-hand symbol

t1, B, 0, _, <, *, C;
t1, B, _, _, *, *, acceptHalt;
t1, B, *, *, *, *, rejectHalt;

t1, B2, 1, _, <, *, C;
t1, B2, _, _, *, *, acceptHalt;
t1, B2, *, *, *, *, rejectHalt;



# State C, D: return to left end of remaining input

t1, C, _, _, *, *, acceptHalt;
t1, C, *, *, <, *, D;
t1, D, *, *, <, *, D;
t1, D, _, _, >, *, 0;
