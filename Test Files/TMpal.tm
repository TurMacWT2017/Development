Input: 10101;

t1,state1,0,_,>,state2;
t1,state1,1,_,>,state4;

t1,state2,0,_,>,state2;
t1,state2,_,_,>,state3;

t1,state4,_,_,>,state3;
t1,state4,1,_,>,state4;

t1,state3,*,*,*,halt;