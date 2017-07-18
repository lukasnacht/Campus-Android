package de.tum.in.tumcampusapp.models.barrierfree;

import de.tum.in.tumcampusapp.adapters.SimpleStickyListHeadersAdapter;

/**
 * The model used to display contact infromation in barrier free page
 */
public class BarrierfreeContact implements SimpleStickyListHeadersAdapter.SimpleStickyListItem{
//    private int id;
    private String name;
    private String telephone;
    private String email;
    private String faculty;
    private String tumID = "";

    public BarrierfreeContact(String name, String phone, String email, String faculty, String tumonlineID){
        this.name = name;
        this.telephone = phone;
        this.email = email;
        this.faculty = faculty;
        this.tumID = tumonlineID;
    }

    public boolean ifValid(){
        return name != null && !name.equals("null");
    }

    public boolean ifHaveTumID(){
        return !(tumID.equals("null") || tumID.equals(""));
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getTumID() {
        return tumID;
    }

    @Override
    public String getHeadName() {
        return getFaculty();
    }

    @Override
    public String toString() {
        return "BarrierfreeContact{" +
                "name='" + name + '\'' +
                ", phone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", faculty='" + faculty + '\'' +
                ", tumonlineID='" + tumID + '\'' +
                '}';
    }
}
