package db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import logging.PropLogger;
import model.WeatherData;

public class DBWeatherUpdater {
	
	DBConnector db = null;
	
	private final static Logger logger = Logger.getLogger(PropLogger.class.getName());

	private static final String SQL_UPDATE_WIND = "UPDATE \"Wind\" SET wind_spd = ?, wind_status = ?, wind_direction = ? WHERE wind_id = ? \n";
    private static final String SQL_UPDATE_ADDITIONAL = "UPDATE \"Additional\" SET humidity = ?, visibility = ?, pressure = ? WHERE add_id = ?\n";
    private static final String SQL_UPDATE_CONFIG = "UPDATE \"Config\" SET city = ?, country = ?, site = ? WHERE config_id = ?\n";
    private static final String SQL_UPDATE_WEATHER = "UPDATE \"Weather\" SET temp = ?, feel_temp = ?, status = ?, "
    		+ "weather_add_id = ?, weather_config_id = ?, weather_wind_id = ? WHERE weather_id = ?\n";
    
    public DBWeatherUpdater(DBConnector db) {
		this.db = db;
	}
    
    public Long updateWind(String SQL_WIND, WeatherData weather_data, Long wind_id)
	{
		long affectedRows = 0;
		
		if (weather_data != null)
		{
			try (PreparedStatement pstmt = db.getConnection().prepareStatement(SQL_WIND,Statement.RETURN_GENERATED_KEYS)) {
				 
	            pstmt.setDouble(1, weather_data.getWind_data().getWind_spd());
	            pstmt.setString(2, weather_data.getWind_data().getWind_status());
	            pstmt.setString(3, weather_data.getWind_data().getWind_direction());
	            pstmt.setLong(4, wind_id);
	 
	            affectedRows = pstmt.executeUpdate();
	            
	        } catch (SQLException ex) {
	            logger.log(Level.SEVERE, this.getClass().getName().toString() + ": " + ex.getMessage());
	            return null;
	        } 
		}
		return affectedRows;
	}
	
	public Long updateConfig(String SQL_CONFIG, WeatherData weather_data, Long config_id)
	{
		long affectedRows = 0;
		
		if (weather_data != null)
		{
		 try (PreparedStatement pstmt = db.getConnection().prepareStatement(SQL_CONFIG,Statement.RETURN_GENERATED_KEYS)) {
	       	 
	            pstmt.setString(1, weather_data.getConfig_data().getCity());
	            pstmt.setString(2, weather_data.getConfig_data().getCountry());
	            pstmt.setString(3, weather_data.getConfig_data().getSite());
	            pstmt.setLong(4, config_id);
	 
	            affectedRows = pstmt.executeUpdate();
	        } catch (SQLException ex) {
	            logger.log(Level.SEVERE, this.getClass().getName().toString() + ": " + ex.getMessage());
	            return null;
	        } 
		}
		 return affectedRows;
	}
	
	public Long updateAdditional(String SQL_ADD, WeatherData weather_data, Long add_id)
	{
		long affectedRows = 0;
		if (weather_data != null)
		{
			try (PreparedStatement pstmt = db.getConnection().prepareStatement(SQL_ADD,Statement.RETURN_GENERATED_KEYS)) {
				 
				if(weather_data.getAdditional_data().getHumidity() != null) {
					pstmt.setDouble(1, weather_data.getAdditional_data().getHumidity());
				}
				else {
					pstmt.setNull(1, Types.INTEGER);
				}
				if(weather_data.getAdditional_data().getVisibility() != null) {
					pstmt.setDouble(2, weather_data.getAdditional_data().getVisibility());
				}
				else {
					pstmt.setNull(2, Types.DOUBLE);
				}
				if(weather_data.getAdditional_data().getPressure() != null) 
				{
					pstmt.setInt(3, weather_data.getAdditional_data().getPressure());
				}
				else {
					pstmt.setNull(3, Types.INTEGER);
				}
	            pstmt.setLong(4, add_id);
	 
	            affectedRows = pstmt.executeUpdate();
	        } catch (SQLException ex) {
	            logger.log(Level.SEVERE, this.getClass().getName().toString() + ": " + ex.getMessage());
	            return null;
	        } 
		}
		return affectedRows;
	}
	
	public Long updateWeather(String SQL_WEATHER, WeatherData weather_data, Long weather_id, Long add_id, Long config_id, Long wind_id) {
		long affectedRows = 0;
		
		if (weather_data != null)
		{
			try (PreparedStatement pstmt = db.getConnection().prepareStatement(SQL_WEATHER,Statement.RETURN_GENERATED_KEYS)) {
		       	 
	            pstmt.setInt(1, weather_data.getTemp());
	            if(weather_data.getFeel_temp() != null) 
				{
					pstmt.setInt(2, weather_data.getFeel_temp());
				}
				else {
					pstmt.setNull(2, Types.INTEGER);
				}
	            pstmt.setString(3, weather_data.getStatus());
	            pstmt.setLong(4, add_id);
	            pstmt.setLong(5, config_id);
	            pstmt.setLong(6, wind_id);
	            pstmt.setLong(7, weather_id);
	 
	            affectedRows = pstmt.executeUpdate();
	
	        } catch (SQLException ex) {
	            logger.log(Level.SEVERE, this.getClass().getName().toString() + ": " + ex.getMessage());
	            return null;
	        }
		}
		return affectedRows;
	}
	
	public void updateWeatherList(List<WeatherData> weather_list)
	{
		
		long[] wind_upd = {0}, config_upd = {0}, weather_upd = {0}, add_upd = {0};
		
		if(weather_list != null)
		{
			Long timeStarted = System.currentTimeMillis();
			
			weather_list.parallelStream().forEach(weather_data -> {
				long wind_id = 0, config_id = 0, weather_id = 0, add_id = 0;
				
				wind_id = weather_data.getWind_data().getId();
				config_id = weather_data.getConfig_data().getId();
				add_id = weather_data.getAdditional_data().getId();
				weather_id = weather_data.getId();
				
				
		        wind_upd[0] += updateWind(SQL_UPDATE_WIND, weather_data, wind_id);
		        config_upd[0] += updateConfig(SQL_UPDATE_CONFIG, weather_data, config_id);
		        add_upd[0] += updateAdditional(SQL_UPDATE_ADDITIONAL, weather_data, add_id); 
		        weather_upd[0] += updateWeather(SQL_UPDATE_WEATHER, weather_data, weather_id, add_id, config_id, wind_id);
			});
			
			System.out.println("Parallel stream db update time: " + (System.currentTimeMillis() - timeStarted) + "ms");
			
			logger.info(this.getClass().getName().toString() + ": Update:\n Wind:" + wind_upd[0] + " rows.\n" +
				    "Additional:" + add_upd[0] + " rows.\n" + "Config:" + config_upd[0] + " rows.\n" + "Weather:" + weather_upd[0] + " rows.\n" +
				    "Data updated successfully.");
		}
	}
}