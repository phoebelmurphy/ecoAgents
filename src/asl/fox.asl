// Agent sample_agent in project ecoAgents

/* Initial beliefs and rules */


/* Initial goals */
!eat(rabbit).

/* Plans */

	
/*
 * the fox hunts its food so to eat it 
 * needs to hunt first
 */
+!eat(X) : true
<-	!hunt(X);
	?killed(X, Name);
	.kill_agent(Name);
	eatPrey(Name);
	!eat(X);
	.	

/*if we can see an X, stalk towards it  */
+!hunt(X) : visible(X) <-
	?visible(X, Name);
	.print("hunting!!");
	!stalk(Name, X).
	
/*if we don't have any better ideas, look around for X */
+!hunt(X) : true <-
	.print("not hunting!!");
	look(X);
	!hunt(X)
.
	

+!stalk(Name, X) : not canPounce(Name) <-
	moveTowards(Name);
	!stalk(Name, X).
	

+!stalk(Name, X) : canPounce(Name) <-
	pounce(Name).
	
-!stalk(Name, X) : true <-
	hunt(X).

+!move : space(P)  <-
	move(P);
	.
	