package infrastructure;

import infrastructure.jason.RunCentralisedMAS;
import jason.JasonException;

public class CustomRunMAS extends RunCentralisedMAS implements Runnable {

	private String[] args;
	
	public CustomRunMAS(String[] args){
		this.args=args;
	}
	
	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}
	

	public void run() {
		runner = this;
		
        runner.init(args);
        try {
			runner.create();
		} catch (JasonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        runner.start();
        runner.waitEnd();
        runner.finish();
	}
	
	
	
}
