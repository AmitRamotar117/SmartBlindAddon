package ca.t10.blinddev.it.smartblindaddon;

public class Monitoring {
    private String  operation;
    private String light;
    private String temp;
    private String maxTemp;
    private String minTemp;
    private String maxLight;
    private String minLight;
    private String location;

    public Monitoring(){

    }


    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxLight() {
        return maxLight;
    }

    public void setMaxLight(String maxLight) {
        this.maxLight = maxLight;
    }

    public String getMinLight() {
        return minLight;
    }

    public void setMinLight(String minLight) {
        this.minLight = minLight;
    }
}
