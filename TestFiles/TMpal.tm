Input: 10101;

t1,state1,0,_,>,state2;
t1,state1,1,_,>,state3;

t1,state2,0,_,>,state2;
t1,state2,_,_,>,state4;

t1,state3,_,_,>,state4;
t1,state3,1,_,>,state3;

t1,state4,*,*,*,acceptHalt;