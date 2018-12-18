package xyz.deepwave.db;

import java.io.Serializable;

public class WeatherAddress implements Serializable {
    private String addressCode;
    private String addressName;

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddressCode(){
        return addressCode;
    }

    public String getAddressName(){
        return addressName;
    }
}
