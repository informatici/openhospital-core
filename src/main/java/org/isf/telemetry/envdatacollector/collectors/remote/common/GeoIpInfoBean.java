package org.isf.telemetry.envdatacollector.collectors.remote.common;

public class GeoIpInfoBean {
	
	private String ip;
	private String countryCode;
	private String countryName;
	private String regionName;
	private String city;
	private String postalCode;
	private String timeZone;
	private Double latitude;
	private Double longitude;
	private String currencyCode;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	@Override
	public String toString() {
		return "GeoIpInfoBean [ip=" + ip + ", countryCode=" + countryCode + ", countryName=" + countryName
				+ ", regionName=" + regionName + ", city=" + city + ", postalCode=" + postalCode + ", timeZone="
				+ timeZone + ", latitude=" + latitude + ", longitude=" + longitude + ", currencyCode=" + currencyCode
				+ "]";
	}
	
	
	

}
