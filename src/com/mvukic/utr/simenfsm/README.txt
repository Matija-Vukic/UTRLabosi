Input file definition:

a,pnp,a|pnp,lab2|pnp,a|pnp,lab2,utr,utr     ;First line are input arrays separated with |
p5,s3,s4,st6,stanje1,stanje2                ;Second line all states separated with commas
a,lab2,pnp,utr                              ;Third line are all symbols separated with commas (alphabet)
p5                                          ;Fourth line are final states separated with commas
stanje1                                     ;Fifth line is initial state
s3,a->stanje2                               ;From Sixth line to the end are transitions
s3,lab2->p5,s4                              ;   current_state,current_symbol->next_state
s4,$->st6                                   ;   $ means E-Transition ($ is the same as no symbol)
s4,utr->p5,s3                               ;   # means there are no next states
stanje1,a->stanje2
stanje1,pnp->s3
stanje2,$->st6
stanje2,a->#