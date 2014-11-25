package com.ankith;

public class Snapshot_thread extends Thread {

	public Snapshot_thread(){

	}
	public void run(){
		final MyVM myvm = new MyVM("T14-VM07-Ubuntu12");
		while(true)
		{
			try {
				if(myvm.pingVM()){
					//myvm.createVMSnapshot();
				}
				System.out.println("Snapshot thread going to sleep for 10 minutes...");
				// sleep for 10 minutes
				Thread.sleep(600000);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} // end of while loop
	}
}
