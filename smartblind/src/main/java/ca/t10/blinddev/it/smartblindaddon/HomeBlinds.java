package ca.t10.blinddev.it.smartblindaddon;
// this is where the blinds on the home page will have the data stored so it can be generated in the
// recyclerview on the homepage
// this will need to be filled with the relevant information.
public class HomeBlinds {
    private String string;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public HomeBlinds(String test) {
        this.string =test;
    }
}
