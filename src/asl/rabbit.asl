// Agent sample_agent in project ecoAgents

/* Initial beliefs and rules */
eaten(grass, 0).
/* Initial goals */
!eat(grass, 5).

/* Plans */

+!eat(X, N) : eaten(X, Y) & Y<N <-
	!eat(X);
	!eat(X, N).

+!eat(X, N) : eaten(X, Y) & Y>= N <-
	true.
	
+!eat(X) : true
<-	!find(X);
	eat(X);
	?eaten(X, N);
	TotalEaten = N + 1;
	-+eaten(X, TotalEaten);
	.	
	
+!find(X) : X <-
	true.
	
+!find(X) : true <- 
	!move;
	!find(X).

+!move : space(P)  <-
	.print(P);
	move(P);
	.
/*+!move : space(P) & P == r <-
	.print(P);
	move(p);
	.
+!move : space(P) & P == u <-
	.print(P);
	move(p);
	.
+!move : space(P) & P == d <-
	.print(P);
	move(p);
	.*/
	
+eaten(X, N) : true
<- .print("ate ", N, " ", X).