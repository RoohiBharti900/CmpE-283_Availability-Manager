package com.ankith;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vmware.vim25.ComputeResourceConfigSpec;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualMachineCapability;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSummary;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.VirtualMachineSnapshot;

import CONFIG.*;

/**
 * Write a description of class MyVM here.
 * 
 * @author Ankith Aiyar
 * @version (a version number or a date)
 */
public class MyVM
{
	// instance variables 
	private VirtualMachine vm;
	private  String vmname;
	private Folder rootFolder;
	private ServiceInstance si;
	private String snapshotname;
	boolean result =false;

	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date = new Date();

	/**
	 * Constructor for objects of class MyVM
	 */
	public MyVM( String vmname ) 
	{
		// Initialize variables -- vi variable for current VM instance
		try {
			this.vmname = vmname;
			this.si = new ServiceInstance(new URL(SJSULAB.getVmwareHostURL()),
					SJSULAB.getVmwareLogin(), SJSULAB.getVmwarePassword(), true);
			rootFolder = si.getRootFolder();

			this.vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vmname);

		} catch ( Exception e ) 
		{ System.out.println( e.toString() ) ; }

	}

	/* Print details of all VMs present on the vCenter (multiple hosts included) 
	 * Prints other details like CPU, Network and I/O
	 * */
	public void getVMDetails()
	{
		try {

			VirtualMachineConfigInfo vminfo = vm.getConfig();
			VirtualMachineCapability vmc = vm.getCapability();
			VirtualMachineRuntimeInfo vmri = vm.getRuntime();
			VirtualMachineSummary  vmsum = vm.getSummary();


			System.out.println("------------------------------------------");
			System.out.println("VM Information : ");

			System.out.println("VM Name: " + vminfo.getName());
			System.out.println("VM OS: " + vminfo.getGuestFullName());
			System.out.println("VM ID: " + vminfo.getGuestId());
			System.out.println("VM Guest IP Address is " +vm.getGuest().getIpAddress());

			// Resource Pool Statistics
			System.out.println("------------------------------------------");
			System.out.println("Resource Pool Informtion : ");

			System.out.println("Resource pool: " +vm.getResourcePool());

			System.out.println("VM Parent: " +vm.getParent());
			System.out.println("Multiple snapshot supported: "	+ vmc.isMultipleSnapshotsSupported());
			System.out.println("Powered Off snapshot supported: "+vmc.isPoweredOffSnapshotsSupported());
			System.out.println("Connection State: " + vmri.getConnectionState());
			System.out.println("Power State: " + vmri.getPowerState());


			//CPU Statistics

			System.out.println("------------------------------------------");
			System.out.println("CPU and Memory Statistics" );

			System.out.println("CPU Usage: " +vmsum.getQuickStats().getOverallCpuUsage());
			System.out.println("Max CPU Usage: " + vmri.getMaxCpuUsage());
			System.out.println("Memory Usage: "+vmsum.getQuickStats().getGuestMemoryUsage());
			System.out.println("Max Memory Usage: " + vmri.getMaxMemoryUsage());
			System.out.println("------------------------------------------");

		} catch (InvalidProperty e) {
			e.printStackTrace();
		} catch (RuntimeFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Power On the Virtual Machine
	 */
	public void powerOn() 
	{
		try {
			System.out.println("Vm is powered on '"+vm.getName() +"'. Please wait...");     
			Task t=vm.powerOnVM_Task(null);
			if(t.waitForTask()== Task.SUCCESS)
			{
				System.out.println("Virtual machine powered on.");
			} 
		}catch ( Exception e ) 
		{ System.out.println( e.toString() ) ; }
	}

	/*
	 * Check for the status of the VM
	 */
	public void vmStatus(){
		try{

			long start = System.currentTimeMillis();
			URL url = new URL("https://130.65.132.114/sdk");
			ServiceInstance si = new ServiceInstance(url, "administrator", "12!@qwQW", true);
			long end = System.currentTimeMillis();
			System.out.println("time taken:" + (end-start));
			Folder rootFolder = si.getRootFolder();
			String name = rootFolder.getName();
			System.out.println("root:" + name);
			//ManagedEntity mes = new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vmname);

			//VirtualMachine vm = (VirtualMachine) mes; 

			VirtualMachineConfigInfo vminfo = vm.getConfig();
			VirtualMachineCapability vmc = vm.getCapability();

			vm.getResourcePool();
			System.out.println("==========="+vm.getName()+"===========");

			System.out.println("GuestOS: " + vminfo.getGuestFullName());
			System.out.println("Multiple snapshot supported: " + vmc.isMultipleSnapshotsSupported());
			System.out.println("VM Guest IP: " +vm.getGuest().getIpAddress());
			System.out.println("======================================");

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void createAlarmOnDC()
	{
		try {
			//vmname=this.vmname;
			System.out.println(si);
			System.out.println(vm);
			System.out.println(vmname);

			CreateVmAlarm.createAlarm(si,vm,vmname);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	/*
	 * Common Ping - Called inside other pings
	 */
	public boolean pingCommon(String ip)
	{
		boolean result= false;
		String cmd = "ping "+ ip;
		String consoleResult="";
		try
		{
			if(ip!=null)
			{
				Runtime r=Runtime.getRuntime();
				Process p= r.exec(cmd);

				BufferedReader input= new BufferedReader(new InputStreamReader(p.getInputStream()));
				while(input.readLine()!=null)
				{
					System.out.println(input.readLine());
					consoleResult+=input.readLine();	    				
				}
				input.close();

				if(consoleResult.contains("Request timed out"))
				{
					System.out.println("Packets Dropped");
					result=false;
					//flag=false;
				}
				else
				{
					//ping successful
					System.out.println("ping successful ");
					result=true;
					//notifyAll();
				}

			} 
			else 
			{
				System.out.println("IP is not found!");
				result = false; //ip = null
				//flag=false;
			}
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		return result;
	}

	/*
	 * Pings VM continuously
	 */
	public boolean pingVM(){
		result=false;
		String ip;

		try
		{
			ip=vm.getGuest().getIpAddress();
			System.out.println("Trying to ping the Virtual Machine :"+ip );
			result=pingCommon(ip);

		}	
		catch(Exception e){
			return result;
		}
		return result;
	}

	/*
	 * Check for the Alarm 
	 * Yes - User powered off the machine
	 * No - error event
	 */
	public boolean checkAlarmStatusVM(){
		boolean alarm= false;
		if(vm.getTriggeredAlarmState()!=null)
		{
			alarm= true;
		}

		return alarm;
	}

	/*
	 * Checks the Host for Ping
	 */
	public boolean pingHost() {
		result=false;
		try
		{
			ManagedEntity[] hosts = new InventoryNavigator(rootFolder).searchManagedEntities("HostSystem");

			VirtualMachineRuntimeInfo vmri = vm.getRuntime();
			String hostB=vmri.getHost().get_value();

			for(int i=0;i<hosts.length;i++)
			{
				//System.out.println("value=" + hosts[i].getMOR().get_value());

				String hostA=hosts[i].getMOR().get_value();
				if(hostA.equalsIgnoreCase(hostB))
				{ 
					String ip= hosts[i].getName();
					System.out.println("The IP of the host is  " + ip);
					System.out.println("Trying to ping the host available...");
					result= pingCommon(ip);
					return result;
				}
				else
				{
					System.out.println("Not able to find the corresponding host...");
					result=false;
				}
			}

		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}

		return result;

	}

	/*
	 * Checks State of VM.
	 * If Powered Off -- Turn on the VM
	 */
	public void checkStateOfVM(){
		try{


			VirtualMachineRuntimeInfo vmri=vm.getRuntime();
			String state=vmri.getPowerState().toString();
			if(state.contains("poweredOn")){
				System.out.println("powered on virtual Machine");
			}

			else {
				powerOn();
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/*Creates SnapShots of VM
	 * If there is error in creation throws error and pings Hosts
	 */
	public void createVMSnapshot() throws Exception{ 
		try { 
			this.snapshotname=vmname + "_Snapshot1";
			System.out.println("VM snapshot is being created . wait...."); 
			VirtualMachineSnapshot vmSnap= vm.getCurrentSnapShot();
			if(vmSnap !=null) {
				Task task = vmSnap.removeSnapshot_Task(true);

				Task t = vm.createSnapshot_Task(snapshotname, "VM Snapshot taken at : "+dateFormat.format(date), true, false); 
				if(t.waitForTask()==t.SUCCESS) { 
					System.out.println("Snapshot is created successfully..."); 
					System.out.println(".....................................................");
				} else { 
					System.out.println("not yet created........................");
				} 
			}
			//For creating the fresh snapshot
			else{
				Task t = vm.createSnapshot_Task(snapshotname, "VM Snapshot taken at : "+dateFormat.format(date), true, false); 
				if(t.waitForTask()==t.SUCCESS) { 
				System.out.println("fresh snapshot created............."); 
				} else { 
					System.out.println("not yet created........................");
				} 
			}

		} 
		catch(Exception e) {
			System.out.println(e.toString()); } }

	// check the state of machine -- is it powering on??
	public void revertVMSnapshot() throws Exception {

		try 
		{
			Task task= vm.getCurrentSnapShot().revertToSnapshot_Task(null);


			if (task.waitForTask() == Task.SUCCESS) 
			{
				System.out.println("Revert VM to latest available SnapShot...");	
				System.out.println("........................................................");
				checkStateOfVM();
			}


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	/*else{
			createOneSnapshot();
			revertToSnapshot(); // calling itself
		}*/

	public String addingNewHost()
	{
		String ret = "";
		try 
		{	

			ManagedEntity [] mes =  new InventoryNavigator(rootFolder).searchManagedEntities("Datacenter");
			Datacenter dc = new Datacenter(rootFolder.getServerConnection(),  mes[0].getMOR());
			HostConnectSpec hs = new HostConnectSpec();
			String ip= "130.65.133.26";
			hs.hostName= ip;
			hs.userName ="root";
			hs.password = "12!@qwQW";
			hs.managementIp = "130.65.132.114";
			//hs.setSslThumbprint("C5:EF:CA:98:96:80:6D:2E:46:CB:B1:D2:BB:87:4A:18:AF:26:83:20");
			//hs.setSslThumbprint("90:BD:8C:C1:4E:F6:E9:A3:1A:DF:4B:FA:16:6B:9A:0D:73:DC:6A:F7");
			ComputeResourceConfigSpec crcs = new ComputeResourceConfigSpec();
			Task t = dc.getHostFolder().addStandaloneHost_Task(hs,crcs, true);
			if(t.waitForTask() == t.SUCCESS)
			{
				ret = ip;
			}
			else
			{
				ret = "";
			}


		}   
		catch (Exception re)
		{
			System.out.println(re.toString());
			System.out.println("Unable to connect to Vsphere server");
		}
		return ret;
	}

	/*
	 * Search Host
	 */
	public String searchingHost()
	{
		//boolean present=false;
		String ip="";
		try
		{
			ManagedEntity[] hosts = new InventoryNavigator(rootFolder).searchManagedEntities("HostSystem");
			if(hosts.length <=1) 
			{
				System.out.println("There is only one host present");
				//return false;
			}
			else
			{
				System.out.println("Multiple hosts present.. Searching in vCenter..");
			}


			VirtualMachineRuntimeInfo vmri = vm.getRuntime();
			String hostB=vmri.getHost().get_value();

			for(int i=0;i<hosts.length;i++)
			{
				String hostA=hosts[i].getMOR().get_value();

				if(!(hostA.equalsIgnoreCase(hostB)))
				{ 
					String hostIp= hosts[i].getName();
					System.out.println("Host "+(i+1) + " : "+hostIp);
					System.out.println("Ping new host...");
					boolean res = pingCommon(hostIp);
					if(res)
					{
						System.out.println("New host is live! Migrating now...");
						ip=hostIp;
						return ip;
					}
					else
					{
						System.out.println("New host not live.. Should try to ping another host...");
					}
				}

			}

		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}

		return ip;


	}

	/* 
	 * Migration to New Host
	 */
	public void migratingToNewHost( String ip)
	{
		String newHostIp= ip;
		HostSystem newHost;

		try
		{
			VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vmname);

			newHost = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem",newHostIp);
			ComputeResource cr = (ComputeResource) newHost.getParent();

			Task task = vm.migrateVM_Task(cr.getResourcePool(),newHost,	VirtualMachineMovePriority.highPriority,
					VirtualMachinePowerState.poweredOff);

			if(task.waitForTask() == task.SUCCESS)
			{
				System.out.println("Migration of VM to new host is complete.");				
			}

		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}

