Input: 10101;

t1, 0, 0, _, R, A;
t1, 0, 1, _, >, A2;
t1, 0, _, _, *, acceptHalt;

t1, A, _, _, left, B;
t1, A, *, *, >, A;

t1, A2, _, _, <, B2;
t1, A2, *, *, >, A2;

t1, B, 0, _, <, C;
t1, B, _, _, *, acceptHalt;
t1, B, *, *, *, rejectHalt;

t1, B2, 1, _, <, C;
t1, B2, _, _, *, acceptHalt;
t1, B2, *, *, *, rejectHalt;

t1, C, _, _, *, acceptHalt;
t1, C, *, *, <, D;
t1, D, *, *, <, D;
t1, D, _, _, >, 0;