Input file definition:

q0,q1,q2,q3,q4                                                           ;All states separated with commas
0,1                                                                      ;Input alphabet
0,1,X,Y,                                                                 ;Tape alphabet
B                                                                        ;Empty cell symbol
0011BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB   ;Tape
q4                                                                       ;Acceptable states
q0                                                                       ;Initial state
0                                                                        ;Initial head position
q0,0->q1,X,R                                                             ;Transitions:
q1,0->q1,0,R                                                             ; state,tapeSymbol->newState,replaceWith,headDirection
q2,0->q2,0,L
q1,1->q2,Y,L
q2,X->q0,X,R
q0,Y->q3,Y,R
q1,Y->q1,Y,R
q2,Y->q2,Y,L
q3,Y->q3,Y,R
q3,B->q4,B,R