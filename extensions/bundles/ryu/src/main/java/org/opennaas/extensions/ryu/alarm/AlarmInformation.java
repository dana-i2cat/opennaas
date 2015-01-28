package org.opennaas.extensions.ryu.client.monitoringmodule;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AlarmInformation implements Serializable {

	private static final long	serialVersionUID	= 4248674117834322401L;

	private String				threshold;									// bytes/seconds
	private String				host;
	private String				port;
	@XmlElement(name = "url_prefix")
	private String				urlPrefix;

	public AlarmInformation() {
	}

	public AlarmInformation(String threshold, String host, String port, String urlPrefix) {
		this.threshold = threshold;
		this.host = host;
		this.port = port;
		this.urlPrefix = urlPrefix;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUrlPrefix() {
		return urlPrefix;
	}

	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((threshold == null) ? 0 : threshold.hashCode());
		result = prime * result + ((urlPrefix == null) ? 0 : urlPrefix.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlarmInformation other = (AlarmInformation) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (threshold == null) {
			if (other.threshold != null)
				return false;
		} else if (!threshold.equals(other.threshold))
			return false;
		if (urlPrefix == null) {
			if (other.urlPrefix != null)
				return false;
		} else if (!urlPrefix.equals(other.urlPrefix))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AlarmInformation [threshold=" + threshold + ", host=" + host + ", port=" + port + ", urlPrefix=" + urlPrefix + "]";
	}

}
