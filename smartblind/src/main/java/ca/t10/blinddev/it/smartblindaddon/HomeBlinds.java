package ca.t10.blinddev.it.smartblindaddon;
// this is where the blinds on the home page will have the data stored so it can be generated in the
// recyclerview on the homepage
// this will need to be filled with the relevant information.
public class HomeBlinds {
    String location;
    String blindkey;
    String userKey;

    // this will house data on the specific blind.
    public HomeBlinds(String test) {
        this.location =test;
    }
    public String getBlindkey(){return blindkey;}
    public String getUserKey(){return userKey;}
    public String getLocation() {
        return location;
    }
}
