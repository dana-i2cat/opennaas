package net.i2cat.mantychore.core.platformmanager;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.util.ArrayList;
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
	
	public static final String RESOURCES = "net.i2cat.mantychore.resources";
	
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
		memory.setCommitted(""+memoryUsage.getCommitted());
		memory.setInit(""+memoryUsage.getInit());
		memory.setMax(""+memoryUsage.getMax());
		memory.setUsage(""+memoryUsage.getUsed());
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
		root.setFreeSpace(""+fileRoot.getFreeSpace());
		root.setTotalSpace(""+fileRoot.getTotalSpace());
		root.setUsableSpace(""+fileRoot.getUsableSpace());
		
		return root;
	}
	
	private Network loadNetworkInformation(){
		Network network = new Network();
		try{
			InetAddress addr = InetAddress.getLocalHost();
			network.setIpAddress(addr.getHostAddress());
			network.setHostname(addr.getHostName());
		}catch(Exception ex){
			network.setHostname("unknown");
			network.setIpAddress("unknown");
		}
		
		return network;
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