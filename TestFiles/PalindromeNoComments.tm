Input: 10101;

t1,state1,0,_,R,state2;
t1,state1,1,_,>,state3;
t1,state1,_,_,*,acceptHalt;

t1,state2,_,_,left,state4;
t1,state2,*,*,>,state2;

t1,state3,_,_,<,state5;
t1,state3,*,*,>,state3;

t1,state4,0,_,<,state6;
t1,state4,_,_,*,acceptHalt;
t1,state4,*,*,*,rejectHalt;

t1,state5,1,_,<,state6;
t1,state5,_,_,*,acceptHalt;
t1,state5,*,*,*,rejectHalt;

t1,state6,_,_,*,acceptHalt;
t1,state6,*,*,<,state7;
t1,state7,*,*,<,state7;
t1,state7,_,_,>,state1;