// Agent sample_agent in project ecoAgents

/* Initial beliefs and rules */
eaten(grass, 0).

/* Initial goals */
!eat(grass, 10).

/* Plans */

+!eat(X, N) : eaten(X, Y) & Y<N <-
	!eat(X);
	!eat(X, N).

+!eat(X, N) : eaten(X, Y) & Y>= N <-
	!breed.
	
+!breed : true <-
	haveBaby;
	?babyName(Name);
	.create_agent(Name, "rabbit.asl");
	-+eaten(grass, 0);
	!eat(grass, 50);
	.
	
+!eat(X) : not animal(fox, P)
<-	!find(X);
	eat(X);
	?eaten(X, N);
	TotalEaten = N + 1;
	-+eaten(X, TotalEaten);
	.
	
-!eat(X) : not animal(fox, P) <-
	!find(X)
.

+!eat(X) : animal(fox, P) <-
	!run;
	!eat(X).
	
+!run : not animal(fox, P) <-
	true.

+!run : animal(fox, P) <-
	!move.
	
	
+!find(X) : X & not animal(fox, P)<-
	true.
	
+!find(X) : true <- 
	!move;
	!find(X).

/*if there's grass go towards that */
+!move : space(P) & resource(grass, P) & not animal(fox, P) <-
	.print("moving to grass ", P);
	move(P).
	
/*otherwise go anywhere */
+!move : space(P) & not animal(fox, P)<-
	.print("moving ", P);
	move(P).

/*stuck or frozen in terror */
+!move : not animal(fox, P) <- 
	.print("didn't move").
	
-!move: true <-
	!move.
	
+eaten(X, N) : true
<- .print("ate ", N, " ", X).