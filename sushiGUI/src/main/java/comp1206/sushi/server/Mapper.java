package comp1206.sushi.server;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import comp1206.sushi.common.Drone;
import comp1206.sushi.common.GeoCode;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Supplier;

public class Mapper {

    static final String URL = "https://assets.mapquestapi.com/icon/v2/circle-P&marker@2x.png";
    
    public static void buildURL(ServerInterface server) {
    	StringBuilder builder = new StringBuilder();
    	for(Supplier s: server.getSuppliers()) {
    		builder.append(GeoCode.getLat(s.getPostcode().toString())+","+GeoCode.getLong(s.getPostcode().toString()));
    		builder.append("|circle-sup");
    		builder.append("||");
    	}
    	for(Postcode p: server.getPostcodes()) {
    		boolean add = true;
    		for(Supplier s: server.getSuppliers()) { 
    			if((s.getPostcode().getName().equals(p.getName()))) {
    				add = false;
    				break;
    			}
    		}
    		if(add) {
    		builder.append(GeoCode.getLat(p.getName())+","+GeoCode.getLong(p.getName()));
    		builder.append("|circle-post");
    		builder.append("||");
    		}
    	}
    	builder.append(GeoCode.getLat(server.getRestaurantPostcode().toString())+","+GeoCode.getLong(server.getRestaurantPostcode().toString()));
    	builder.append("|circle-R");
    	JFrame test = new JFrame("Maps");
    	BufferedImage img = null;
        try {
	        String imageUrl = "https://open.mapquestapi.com/staticmap/v5/map?locations="+builder.toString()+"&size=500,500@2x&key=YDCBsfT1NZQGc4GM0ZwGFkoNfUzjaBOI";           
            URL url = new URL(imageUrl);
            InputStream is = url.openStream();
            img = ImageIO.read(url);
            is.close();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        test.add(new JLabel(new ImageIcon((new ImageIcon(img)).getImage().getScaledInstance(630, 600,
                java.awt.Image.SCALE_SMOOTH))));
        
        
        test.setResizable(false);
        test.setVisible(true);
        test.pack();

    }
    
    public static void buildURLDrone(Drone drone) {
    	StringBuilder builder = new StringBuilder();
    	builder.append(GeoCode.getLat(drone.getSource().getName())+","+GeoCode.getLong(drone.getSource().getName()));
    	builder.append("|");
    	builder.append(GeoCode.getLat(drone.getDestination().getName())+","+GeoCode.getLong(drone.getDestination().getName()));
    	
    	JFrame test = new JFrame("Maps");
    	BufferedImage img = null;
        try {
	        String imageUrl = "https://open.mapquestapi.com/staticmap/v5/map?shape="+builder.toString()+"&size=500,500@2x&key=YDCBsfT1NZQGc4GM0ZwGFkoNfUzjaBOI";           
            URL url = new URL(imageUrl);
            InputStream is = url.openStream();
            img = ImageIO.read(url);
            is.close();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        test.add(new JLabel(new ImageIcon((new ImageIcon(img)).getImage().getScaledInstance(630, 600,
                java.awt.Image.SCALE_SMOOTH))));
        test.setResizable(false);
        test.setVisible(true);
        test.pack();
    	
    }
}
    
    
    
   