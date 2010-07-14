package net.i2cat.mantychore.models.router;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Email;

public class Location {

	protected String	city;
	protected String	country;
	protected String	address;
	protected String	telephone;
	@Email
	protected String	eMail;
	protected double	latitude;
	protected double	longitude;
	protected String	timeZone;

	public String getCity() {
		return city;
	}

	public void setCity(String value) {
		this.city = value;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String value) {
		this.country = value;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String value) {
		this.address = value;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String value) {
		this.telephone = value;
	}

	public String getEMail() {
		return eMail;
	}

	public void setEMail(String value) {
		this.eMail = value;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double value) {
		this.latitude = value;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double value) {
		this.longitude = value;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String value) {
		this.timeZone = value;
	}

}
