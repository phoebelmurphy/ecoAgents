// Agent sample_agent in project ecoAgents

/* Initial beliefs and rules */
eaten(grass, 0).


/* Initial goals */
!breed.

/* Plans */
	
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

/* the java code decides the direction */
+!move : true<-
	?eaten(X, N);
	TotalEaten = N-1;
	-+eaten(X, TotalEaten);
	move.
	
	
-!breed : true <-
	!breed.
	