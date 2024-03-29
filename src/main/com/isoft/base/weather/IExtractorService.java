package main.com.isoft.base.weather;

import java.util.List;

import main.com.isoft.base.property.CityConfig;
import main.com.isoft.rest.db.model.WeatherData;

/*
 *    Creates a fixed-length thread pool to run http requests asynchronously. 
 *    Creates weather objects from the retrieved data and adds them to a list.
 */
public interface IExtractorService {
    
    /*
     * Creates the thread pool, starts the http requests and fills the weather objects.
     * 
     * @param city    the configuration data for the http requests
     */
    public void runService(CityConfig city);
    
    /*
     * Awaits all threads to finish working and stops them.
     */
    public void shutdownService();

    public List<WeatherData> getWeatherList();
    

}
