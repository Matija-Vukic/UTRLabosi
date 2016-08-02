Input example:

0|0,2,0|1,2,0       ;Input arrays separated with |,symbols are separated with comma
q1,q2,q3            ;States
0,1,2               ;Input Symbols
J,N,K               ;Stack symbols
q3                  ;Acceptable states
q1                  ;Initial state
K                   ;Initial stack symbol
q1,0,K->q1,NK       ;Transitions:
q1,1,K->q1,JK       ; state,inputSymbol,stackSymbol->newState,newStackSymbols
q1,0,N->q1,NN
q1,1,N->q1,JN
q1,0,J->q1,NJ
q1,1,J->q1,JJ
q1,2,K->q2,K
q1,2,N->q2,N
q1,2,J->q2,J
q2,0,N->q2,$
q2,1,J->q2,$
q2,$,K->q3,$