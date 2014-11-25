package com.ankith;
import java.net.URL;

import CONFIG.SJSULAB;

import com.vmware.vim25.HostConfigInfo;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.HostNetworkInfo;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.ClusterComputeResource;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServerConnection;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;


public class AvailabilityManager extends Thread  {

	/**
	 * @param args
	 *///public static void main (String args[]) throws Exception{


	private Thread t1;

	final MyVM myvm = new MyVM("T14-VM07-Ubuntu12");
	final MyHost myhs= new MyHost("T14-vHost02-cum3_IP=.133.22");

	public void run(){
		try{
			//Print the details of all the VM's
			myvm.getVMDetails();

			//Check for the current VM status
			myvm.vmStatus();
			//myvm.createAlarmOnDC();


			//Take vHost Snapshot
			/*System.out.println("take vHost Snapshot..");
			if (myvm.pingVM()){
				System.out.println("Inside vHost Snapshot..");
				myhs.createHostSnapshot();
			}*/

			//Ping Every 10 seconds


			while (true){

				//myvm.createAlarmOnDC();
				/*System.out.println("Check VM state first....");
				myvm.checkStateOfVM();*/
				boolean status= myvm.pingVM();
				if(status){
					System.out.println("Vm is working fine.... ");					
					myvm.vmStatus();
				}
				else{
					status=myvm.pingHost();
					boolean status1 = false;
					System.out.println("ALARM STATUS CHECK ::: "+myvm.checkAlarmStatusVM());
					if(status){
						if(myvm.checkAlarmStatusVM()){
							System.out.println("User has powered off the Virtual Machine. Please wait until it is powered on again");
							//Power On VM if the ALARM status is True
							//myvm.powerOn();
							Thread.sleep(90000); //wait 1.5 minutes
							/*if(myvm.pingVM()){
								System.out.println("The VM is up and working .. ");
							}*/

							/*myvm.checkStateOfVM();
							status1 = myvm.pingVM();*/
						}
						/*else if (status1){
							System.out.println("Vm is working fine after power on.... ");
							myvm.vmStatus();
						}*/
						else{
							System.out.println("The VM is not avaialble. Trying to get it back.");
							System.out.println("Reverting VM to previous snapshot");
							myvm.revertVMSnapshot();
							Thread.sleep(120000); //2 minute wait after Revert
							if(myvm.pingVM()){
								System.out.println("Virtual Machine Ready and Available");
							}
						}
					}
					else{
						System.out.println("The parent vHost is not alive, trying to getting it live. Please Wait....");	
						myhs.revertHostSnapshot();
						System.out.println("Revert Host SnapShot takes Time (4 minutes). Please Wait....");
						Thread.sleep(240000); // 4 Minutes wait
						boolean checkHost=myvm.pingHost();
						if(checkHost==false){
							System.out.println("Not able to correct the current host, Try with other host of Data Center");	
							String ip=myvm.searchingHost();
							if(ip.isEmpty())
							{
								System.out.println("This was only one host so trying to add another host to datacenter");
								ip = myvm.addingNewHost();
								Thread.sleep(50000);
							}

							System.out.println("Migrating the vm to another available host. VM IP : "+ ip);
							myvm.migratingToNewHost(ip);
							Thread.sleep(240000); // 4 minutes wait 
						}


						//myvm.checkStateOfVM();
						boolean status_after = myvm.pingVM();
						System.out.println("Check VM state.... "+status_after);
						if(status_after == true){
							System.out.println("VM is up and Running and availble now");
							myvm.vmStatus();
						}


					}
				}
				try{// sleep
					System.out.println("Ping thread going to sleep for 5 minutes");
					Thread.sleep(300000); // Check Ping after 10 seconds
				}
				catch(Exception e){
					e.printStackTrace();
				}

			}
		}
		catch(Exception e){
			e.printStackTrace();
		}



	}
	//	public void start(){
	//		t1 = new Thread (this);
	//		t1.run();
	//	}

}




