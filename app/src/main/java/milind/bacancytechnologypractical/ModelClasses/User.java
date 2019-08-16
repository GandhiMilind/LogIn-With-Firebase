package milind.bacancytechnologypractical.ModelClasses;

import java.util.ArrayList;

public class User {

    private String name, email, phonenumber;
    private ArrayList<LatLng> latlng = new ArrayList<>();

    public User() {
        super();
    }

    public User(String name, String email, String phonenumber) {
        super();
        this.name = name;
        this.email = email;
        this.phonenumber = phonenumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public ArrayList<LatLng> getLatlng() {
        return latlng;
    }

    public void setLatlng(ArrayList<LatLng> latlng) {
        this.latlng = latlng;
    }
}
