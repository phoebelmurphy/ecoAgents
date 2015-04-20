// Agent sample_agent in project ecoAgents

/* Initial beliefs and rules */
eaten(grass, 0).


/* Initial goals */
!breed.

/* Plans */

/* +!eat(X, N) : eaten(X, Y) & Y<N <-
	!eat(X);
	!eat(X, N).

+!eat(X, N) : eaten(X, Y) & Y>= N <-
	!breed.*/
	
+!breed : eaten(grass, 5) <-
	haveBaby;
	?babyName(Name);
	.create_agent(Name, "rabbit.asl");
	-+eaten(grass, 3);
	!breed
	.
	
+!breed: true <-
	!eat(grass);
	!breed.
	
	
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
	?eaten(X, N);
	TotalEaten = N-1;
	-+eaten(X, TotalEaten);
	!move.
	
	
+!find(X) : X & not animal(fox, P)<-
	true.
	
+!find(X) : true <- 
	!move;
	?eaten(X, N);
	TotalEaten = N-1;
	-+eaten(X, TotalEaten);
	!find(X).

/*if there's grass go towards that */
+!move : space(P) & resource(grass, P) & not animal(fox, P) <-
	?eaten(X, N);
	TotalEaten = N-1;
	-+eaten(X, TotalEaten);
	move(P).
	
/*otherwise go anywhere */
+!move : space(P) & not animal(fox, P)<-
	?eaten(X, N);
	TotalEaten = N-1;
	-+eaten(X, TotalEaten);
	move(P).

/*if you can't avoid the fox try anyway */
+!move : space(P) <- 
	move(P).
	
+!move : true <-
	!breed.
	
-!move: true <-
	?eaten(X, N);
	TotalEaten = N-1;
	-+eaten(X, TotalEaten);
	!move.
	