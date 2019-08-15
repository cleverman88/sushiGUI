package comp1206.sushi.common;

import java.util.HashMap;
import java.util.Map;

import comp1206.sushi.common.Postcode;

public class Postcode extends Model {

	private String name;
	private Map<String,Double> latLong;
	private Number distance;

	
	
	
	public Postcode(String code) {
		this.name = code;
		calculateLatLong();
		calculateDistance();
	}
	
	public Postcode(String code, Restaurant restaurant) {
		this.name = code;
		calculateLatLong();
		calculateDistance(restaurant);
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Number getDistance() {
		return this.distance;
	}

	public Map<String,Double> getLatLong() {
		return this.latLong;
	}
	
	protected void calculateDistance(Restaurant restaurant) {
		Postcode destination = restaurant.getLocation();
		double lat = Double.parseDouble(GeoCode.getLat(destination.getName()));
		double lon = Double.parseDouble(GeoCode.getLong(destination.getName()));
		double lat2 = latLong.get("lat");
		double lon2 = latLong.get("lon");
		this.distance = GeoCode.haversine(lat, lon, lat2, lon2);
	}
	
	protected void calculateDistance() {
		String destination = "SO17 1BJ";
		double lat = Double.parseDouble(GeoCode.getLat(destination));
		double lon = Double.parseDouble(GeoCode.getLong(destination));
		double lat2 = latLong.get("lat");
		double lon2 = latLong.get("lon");
		this.distance = GeoCode.haversine(lat, lon, lat2, lon2);
	}
	
	protected void calculateLatLong() {
		//This function needs implementing
		this.latLong = new HashMap<String,Double>();
		latLong.put("lat", Double.parseDouble(GeoCode.getLat(name)));
		latLong.put("lon", Double.parseDouble(GeoCode.getLong(name)));
	}
	
}
