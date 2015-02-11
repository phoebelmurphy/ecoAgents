// Agent sample_agent in project ecoAgents

/* Initial beliefs and rules */
eaten(grass, 0).
/* Initial goals */
!eat(grass, 100).

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

/*if there's grass and no fox, go towards that */
+!move : space(P) & resource(grass, P)  <-
	.print("moving to grass ", P);
	move(P).
	
/*never move towards a fox */
+!move : space(P) <-
	.print("moving ", P);
	move(P).

/*stuck or frozen in terror */
+!move : true <- 
	.print("didn't move").
	
+eaten(X, N) : true
<- .print("ate ", N, " ", X).