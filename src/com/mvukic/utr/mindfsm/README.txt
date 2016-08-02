Input file definition:

p1,p2,p3,p4,p5,p6,p7            ; List of states
c,d                             ; Alphabet
p5,p6,p7                        ; Final states
p1                              ; Initial state
p1,c->p6                        ; Transitions:
p1,d->p3                        ;  CurrentState,Symbol->NextState
p2,c->p7
p2,d->p3
p3,c->p1
p3,d->p5
p4,c->p4
p4,d->p6
p5,c->p7
p5,d->p3
p6,c->p4
p6,d->p1
p7,c->p4
p7,d->p2