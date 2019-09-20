package com.example.barcode;

public class User {
    String name,rollNumber,branch,college,semester,idImage,userID,gender,verify;

    public User() {
    }

    public User(String name, String rollNumber, String branch, String college, String semester, String idImage, String userID, String gender, String verify) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.branch = branch;
        this.college = college;
        this.semester = semester;
        this.idImage = idImage;
        this.userID = userID;
        this.gender = gender;
        this.verify = verify;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getIdImage() {
        return idImage;
    }

    public void setIdImage(String idImage) {
        this.idImage = idImage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
