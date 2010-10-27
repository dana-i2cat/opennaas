package cat.i2cat.manticore.test.demo.values;
/**
 * This class represents a static route for a router instance
 * 
 * @author Xavi Barrera
 *
 */
public class StaticRoute {
	private String destinationIP;
	private String nextHopIP;
	private boolean ipv6;
	
	public StaticRoute(String destinationIP, String nextHopIP, boolean ipv6) {
		super();
		this.destinationIP = destinationIP;
		this.nextHopIP = nextHopIP;
		this.ipv6 = ipv6;
	}
	
	public String getDestinationIP() {
		return destinationIP;
	}

	public String getNextHopIP() {
		return nextHopIP;
	}
	
	public StaticRoute clone(){
		StaticRoute newStaticRoute = new StaticRoute(getDestinationIP(), getNextHopIP(), ipv6);
		return newStaticRoute;
	}
	
	public boolean isIpv6() {
		return ipv6;
	}

	public void setIpv6(boolean ipv6) {
		this.ipv6 = ipv6;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof StaticRoute))
			return false;
		StaticRoute other = (StaticRoute) obj;
		if (destinationIP == null) {
			if (other.destinationIP != null)
				return false;
		} else if (!destinationIP.equals(other.destinationIP))
			return false;
		if (ipv6 != other.ipv6)
			return false;
		if (nextHopIP == null) {
			if (other.nextHopIP != null)
				return false;
		} else if (!nextHopIP.equals(other.nextHopIP))
			return false;
		return true;
	}
	

	
}
