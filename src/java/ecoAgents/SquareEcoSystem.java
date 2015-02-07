package ecoAgents;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SquareEcoSystem extends Environment {

	Coordinates rabbitPos;
	Random random = new Random();
	Logger logger = Logger.getLogger("env.rabbit");
	int x, y;
	Grid grid ;
	
	public void init(String[] args){
		x =5;
		y = 5;
		grid=new Grid(x, y);
		rabbitPos = new Coordinates(0,0);
		logger.setLevel(Level.ALL);
		updatePercepts();
	}
	
	public boolean executeAction(String ag, Structure action){
		logger.logp(Level.INFO, "SquareEcoSystem", "executeAction", "executing");
		boolean result = false;
		if(action.getFunctor().equals("eat")){
			result = eatGrass(rabbitPos);
		}
		else if(action.getFunctor().equals("move")){
			
			char dest = action.getTerm(0).toString().charAt(0);
			int dx = 0;
			int dy = 0;
			
			logger.logp(Level.INFO, "SquareEcoSystem", "executeAction", "dest: " + dest);
			
			if(dest=='l'){
				dx = -1;
			}
			else if (dest=='r'){
				dx = 1;
			}
			else if(dest=='d'){
				dy = -1;
			}
			else if(dest=='u'){
				dy = 1;
			}
			
			int newx =rabbitPos.getX()+dx;
			int newy = rabbitPos.getY()+dy;
			
			if( (newx < 0) || (newy < 0) || (newx >= x) || (newy >= y)){
				String message =  "can't move. dest " + dest + " newx: " + newx + " newy:" + newy + " grid x:" + x + " grid y:" + y;
				logger.logp(Level.SEVERE, "SquareEcoSystem", "executeAction",message);
				result = false;
			}
			else{
				logger.logp(Level.INFO, "SquareEcoSystem", "executeAction", "can move");
				result = true;
				rabbitPos.setX(newx);
				rabbitPos.setY(newy);	
			}
		}
		if(result){
			updatePercepts();
		}
		return result;
	}
	
	private void updatePercepts(){
		grid.growGrass();
		clearPercepts();
		
		updateGrassPercept();
		updatePositionPercept();
	}
	
	private void updateGrassPercept(){
		if (grid.getSquare(rabbitPos).isGrass()) {
			addPercept(Literal.parseLiteral("grass"));
		}
		else {
			addPercept(Literal.parseLiteral("mud"));
		}
	}
	
	private void updatePositionPercept(){
		if (rabbitPos.getX() > 0){
			addPercept(Literal.parseLiteral("space(l)"));
		}
		if(rabbitPos.getX() < x ){
			addPercept(Literal.parseLiteral("space(r)"));
		}
		if(rabbitPos.getY() > 0) {
			addPercept(Literal.parseLiteral("space(d)"));
		}
		if(rabbitPos.getY() < y ){
			addPercept(Literal.parseLiteral("space(u)"));
		}
	}
	
	public boolean eatGrass(Coordinates position){
		if(grid.getSquare(rabbitPos).isGrass()){
			grid.getSquare(rabbitPos).setGrass(false);
			return true;
		}
		return false;
	}
}
