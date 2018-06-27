package vv3ird.populatecard.control;

import vv3ird.populatecard.gui.StatusListener;

public class Task implements StatusListener {
	
	private String description = null;
	
	private Runnable payload = null;
	
	private StatusListener listener = null;
	
	private String status = null;

	public Task(String description, Runnable payload, StatusListener listener) {
		super();
		this.description = description;
		this.payload = payload;
		this.listener = listener;
	}

	@Override
	public void setText(String status) {
		if (this.listener != null)
			listener.setText(status);
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Runnable getPayload() {
		return payload;
	}

	public void setPayload(Runnable payload) {
		this.payload = payload;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String[] getLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearLog() {
		// TODO Auto-generated method stub
		
	}

}
