package tests.model;

import events.UpdateListener;

public class TestUpdateListener implements UpdateListener {

	private boolean updated = false;
	
	public boolean updated() {
		return updated;
	}
	
	public void reset(){
		updated = false;
	}
	
	public void modelUpdated() {
		updated = true;
	}

}
