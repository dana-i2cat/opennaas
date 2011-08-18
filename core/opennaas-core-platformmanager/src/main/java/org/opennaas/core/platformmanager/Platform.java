package org.opennaas.core.platformmanager;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Provides information about the platform where the framework is executing
 * @author eduardgrasa
 *
 */
@XmlRootElement
public class Platform {
	
	public static final String NORMAL     = "\u001b[0m";
	public static final String BOLD       = "\u001b[1m";
	public static final String RESOURCES = "org.opennaas.core.resources";
	
	private OperatingSystem operatingSystem = null;
	private Java java = null;
	private Hardware hardware = null;
	/**
	 * The IaaS components that this platform has deployed (engines, resources)
	 */
	private List<String> iaasComponents = null;

	public Platform(){
		operatingSystem = new OperatingSystem();
		java = new Java();
		hardware = new Hardware();
		loadInformation();
	}
	
	public void reloadInformation(){
		loadInformation();
	}
	
	@XmlElement(name = "operatingSystem")
	public OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}
	
	public void setOperatingSystem(OperatingSystem operatingSystem){
		this.operatingSystem=operatingSystem;
	}
	
	@XmlElement(name = "java")
	public Java getJava() {
		return java;
	}
	
	public void setJava(Java java){
		this.java = java;
	}
	
	@XmlElement(name = "hardware")
	public Hardware getHardware(){
		return hardware;
	}
	
	public void setHardware(Hardware hw)
	{
		hardware = hw;
	}

	@XmlElement(name = "iaasComponents")
	public List<String> getIaasComponents() {
		return iaasComponents;
	}

	private void loadInformation(){
		loadOperatingSystemInformation();
		loadJavaInformation();
		loadHardwareInformation();
		loadIaaSComponentsInformation();
	}
	
	private void loadOperatingSystemInformation(){
		operatingSystem.setName(System.getProperty("os.name"));
		operatingSystem.setVersion(System.getProperty("os.version"));
		operatingSystem.setArchitecture("os.arch");
	}
	
	private void loadJavaInformation(){
		java.setJreVersion(System.getProperty("java.version"));
		java.setJreVendor(System.getProperty("java.vendor"));
		java.setJvmName(System.getProperty("java.vm.name"));
		java.setJvmVersion(System.getProperty("java.vm.version"));
		java.setJvmVendor(System.getProperty("java.vm.vendor"));
		java.setNonHeapMemory(
				loadMemoryInformation(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage()));
		java.setHeapMemory(
				loadMemoryInformation(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()));
	}
	
	private void loadHardwareInformation(){
		hardware.setCpus(""+Runtime.getRuntime().availableProcessors());
		hardware.setCpuLoad(""+ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()*100);
		hardware.setHardDisk(loadHardDiskInformation());
		hardware.setNetwork(loadNetworkInformation());
	}
	
	private Memory loadMemoryInformation(MemoryUsage memoryUsage){
		Memory memory = new Memory();
		memory.setCommitted(""+memoryUsage.getCommitted()/1048576);
		memory.setInit(""+memoryUsage.getInit()/1048576);
		memory.setMax(""+memoryUsage.getMax()/1048576);
		memory.setUsage(""+memoryUsage.getUsed()/1048576);
		return memory;
	}
	
	private HardDisk loadHardDiskInformation(){
		List<Root> roots = new ArrayList<Root>();
		File[] fileRoots = File.listRoots();
		for(int i=0; i<fileRoots.length; i++){
			roots.add(loadRootInformation(fileRoots[i]));
		}
		HardDisk hardDisk = new HardDisk();
		hardDisk.setRoots(roots);
		return hardDisk;
	}
	
	private Root loadRootInformation(File fileRoot){
		Root root = new Root();
		root.setAbsolutePath(fileRoot.getAbsolutePath());
		root.setFreeSpace(""+fileRoot.getFreeSpace()/1048576);
		root.setTotalSpace(""+fileRoot.getTotalSpace()/1048576);
		root.setUsableSpace(""+fileRoot.getUsableSpace()/1048576);
		
		return root;
	}
	
	private Network loadNetworkInformation(){
		List<NetInf> networkInterfaces = new ArrayList<NetInf>();
		Enumeration<NetworkInterface> nets = null;
		try {
			nets = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for (NetworkInterface netint : Collections.list(nets))
            networkInterfaces.add(createNetworkInterface(netint));
		
        Network network = new Network();
        network.setNeworkInterfaces(networkInterfaces);
		
		return network;
	}
	
	private NetInf createNetworkInterface(NetworkInterface networkInterface){
		NetInf netInf = new NetInf();
		
		netInf.setDisplayName(networkInterface.getDisplayName());
		netInf.setName(networkInterface.getName());

		try{
			netInf.setMtu(""+networkInterface.getMTU());
			netInf.setIpAddress(networkInterface.getInterfaceAddresses().toString());
			byte[] macAddress = networkInterface.getHardwareAddress();
			String printableMacAddress = "";
			for(int i=0; i<macAddress.length; i++){
				printableMacAddress = printableMacAddress + String.format("%02X ", macAddress[i]);
			}
			netInf.setMacAddress(printableMacAddress);
			netInf.setLoopback(networkInterface.isLoopback());
			netInf.setPointToPoint(networkInterface.isPointToPoint());
			netInf.setUp(networkInterface.isUp());
			netInf.setSupportsMulticast(networkInterface.supportsMulticast());
			netInf.setVirtual(networkInterface.isVirtual());
			netInf.setHostname(networkInterface.getInetAddresses().nextElement().getCanonicalHostName());
		}catch(Exception ex){
		}
		
		return netInf;
	}
	
	private void loadIaaSComponentsInformation(){
		//TODO, get the bundle context and search for the engines and resources bundles
		iaasComponents = new ArrayList<String>();
		iaasComponents.add(RESOURCES);
	}
	
	public String toString(){
		return operatingSystem.toString() 
			 + java.toString()
			 + hardware.toString();
	}
}