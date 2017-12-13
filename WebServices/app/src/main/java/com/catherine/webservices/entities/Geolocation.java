package com.catherine.webservices.entities;

/**
 * Created by Catherine on 2017/12/13.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class Geolocation
{
    private String time_zone;

    private String region_name;

    private String metro_code;

    private String zip_code;

    private String region_code;

    private String longitude;

    private String latitude;

    private String country_code;

    private String country_name;

    private String city;

    private String ip;

    public String getTime_zone ()
    {
        return time_zone;
    }

    public void setTime_zone (String time_zone)
    {
        this.time_zone = time_zone;
    }

    public String getRegion_name ()
    {
        return region_name;
    }

    public void setRegion_name (String region_name)
    {
        this.region_name = region_name;
    }

    public String getMetro_code ()
    {
        return metro_code;
    }

    public void setMetro_code (String metro_code)
    {
        this.metro_code = metro_code;
    }

    public String getZip_code ()
    {
        return zip_code;
    }

    public void setZip_code (String zip_code)
    {
        this.zip_code = zip_code;
    }

    public String getRegion_code ()
    {
        return region_code;
    }

    public void setRegion_code (String region_code)
    {
        this.region_code = region_code;
    }

    public String getLongitude ()
    {
        return longitude;
    }

    public void setLongitude (String longitude)
    {
        this.longitude = longitude;
    }

    public String getLatitude ()
    {
        return latitude;
    }

    public void setLatitude (String latitude)
    {
        this.latitude = latitude;
    }

    public String getCountry_code ()
    {
        return country_code;
    }

    public void setCountry_code (String country_code)
    {
        this.country_code = country_code;
    }

    public String getCountry_name ()
    {
        return country_name;
    }

    public void setCountry_name (String country_name)
    {
        this.country_name = country_name;
    }

    public String getCity ()
    {
        return city;
    }

    public void setCity (String city)
    {
        this.city = city;
    }

    public String getIp ()
    {
        return ip;
    }

    public void setIp (String ip)
    {
        this.ip = ip;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [time_zone = "+time_zone+", region_name = "+region_name+", metro_code = "+metro_code+", zip_code = "+zip_code+", region_code = "+region_code+", longitude = "+longitude+", latitude = "+latitude+", country_code = "+country_code+", country_name = "+country_name+", city = "+city+", ip = "+ip+"]";
    }
}