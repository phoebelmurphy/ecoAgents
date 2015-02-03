package ecoAgents;

import java.util.Random;
import java.util.logging.Logger;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.*;

public class EcoSystem extends Environment {

	boolean[] grass = {true, true};
	char rabbitPosition = 0;
	Random random = new Random();
	Logger logger = Logger.getLogger("env.rabbit");
	
	public void init(String[] args){
		updatePercepts();
	}
	
	public boolean executeAction(String ag, Structure action){
		boolean result = false;
		if(action.getFunctor().equals("eat")){
			result = eatGrass(rabbitPosition);
		}
		else if(action.getFunctor().equals("move")){
			char dest = action.getTerm(0).toString().charAt(0);
			if(dest=='l'){
				rabbitPosition = 0;
			}
			else {
				rabbitPosition = 1;
			}
			result = true;
		}
		if(result){
			updatePercepts();
		}
		return result;
	}
	
	private void updatePercepts(){
		if(random.nextInt(100) < 20 ){
			grass[random.nextInt(2)] = true;
		}
		clearPercepts();
		if (grass[rabbitPosition]) {
			addPercept(Literal.parseLiteral("grass"));
		}
		else {
			addPercept(Literal.parseLiteral("mud"));
		}
		if (rabbitPosition == 0){
			addPercept(Literal.parseLiteral("pos(l)"));
		}
		else if(rabbitPosition == 1){
			addPercept(Literal.parseLiteral("pos(r)"));
		}
	}
	
	public boolean eatGrass(char position){
		if(grass[position] && rabbitPosition==position){
			grass[position] = false;
			return true;
		}
		return false;
	}
}
