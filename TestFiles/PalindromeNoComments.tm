Input: 10101;

# State 0: read the leftmost symbol

t1, 0, 0, _, R, t1, A;
t1, 0, 1, _, >, t1, A2;
t1, 0, _, _, *, t1, acceptHalt;



# State A, A2: find the rightmost symbol

t1, A, _, _, <, t1, B;
t1, A, *, *, >, t1, A;

t1, A2, _, _, <, t1, B2;
t1, A2, *, *, >, t1, A2;



# State B, B2: check if the rightmost symbol matches the most recently read left-hand symbol

t1, B, 0, _, <, t1, C;
t1, B, _, _, *, t1, acceptHalt;
t1, B, *, *, *, t1, rejectHalt;

t1, B2, 1, _, <, t1, C;
t1, B2, _, _, *, t1, acceptHalt;
t1, B2, *, *, *, t1, rejectHalt;



# State C, D: return to left end of remaining input

t1, C, _, _, *, t1, acceptHalt;
t1, C, *, *, <, t1, D;
t1, D, *, *, <, t1, D;
t1, D, _, _, >, t1, 0;
