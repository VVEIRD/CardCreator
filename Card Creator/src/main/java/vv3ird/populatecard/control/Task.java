package vv3ird.populatecard.control;

import vv3ird.populatecard.gui.StatusListener;

public class Task implements StatusListener {
	
	private String description = null;
	
	private Runnable payload = null;
	
	private StatusListener listener = null;
	
	private String status = null;
	
	private boolean noParallel = false;
	public Task(String description, Runnable payload, StatusListener listener) {
		this(description, payload, listener, false);
	}

	public Task(String description, Runnable payload, StatusListener listener, boolean noParallel) {
		super();
		this.description = description;
		this.payload = payload;
		this.listener = listener;
		this.noParallel = noParallel;
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
	}
	
	public boolean noParallel() {
		return noParallel;
	}
	
	public void setNoParallel(boolean noParallel) {
		this.noParallel = noParallel;
	}
}
