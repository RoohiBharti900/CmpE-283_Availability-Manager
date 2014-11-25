package com.ankith;

public class MainMethod {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("check");
		AvailabilityManager av =new AvailabilityManager();
		av.start(); // ping and status check of VM and host
		
		System.out.println("Starting snapshot thread parallely");
		Snapshot_thread snap= new Snapshot_thread(); // create snapshot thread
		snap.start(); // start snapshot thread. it will take snapshot and sleep for 10 minutes.
		System.out.println("Checking ping and snapshot threads simultaneously");
	}

}
