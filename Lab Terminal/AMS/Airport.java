import java.io.Serializable;
// Airport.java

public class Airport implements Serializable {
    private static final long serialVersionUID = 1L;

    private String airportCode;
    private String name;
    private String city;
    private String country;
    private String status;       // Strictly "Active" or "Inactive"

    public Airport() { }

    public Airport(String airportCode, String name, String city,
                   String country, String status) {
        this.airportCode = airportCode;
        this.name = name;
        this.city = city;
        this.country = country;
        // Data sanitization gate
        setStatus(status);
    }

    // ---------- Getters / Setters ----------
    public String getAirportCode() { return airportCode; }
    public String getName()        { return name; }
    public String getCity()        { return city; }
    public String getCountry()     { return country; }
    
    public String getStatus() { 
        // Graceful fallback fallback if legacy bad data exists
        if (status == null || (!status.equals("Active") && !status.equals("Inactive"))) {
            return "Inactive";
        }
        return status; 
    }

    public void setStatus(String status) { 
        if (status != null && (status.equalsIgnoreCase("Active") || status.equals("Active"))) {
            this.status = "Active";
        } else {
            this.status = "Inactive"; // Graceful fallback rule
        }
    }

    public void setName(String name)       { this.name = name; }
    public void setCity(String city)       { this.city = city; }
    public void setCountry(String country) { this.country = country; }

    @Override
    public String toString() {
        return airportCode + " - " + name + " (" + city + ", " + country
             + ") [" + getStatus() + "]";
    }
}