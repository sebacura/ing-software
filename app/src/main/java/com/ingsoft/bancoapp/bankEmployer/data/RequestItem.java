package com.ingsoft.bancoapp.bankEmployer.data;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RequestItem implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String ci;
    private String address;
    private String salary;
    private String deliveryAddress;
    private String productId;
    private String stateId;
    private String salaryPhoto;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    private String productName;

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    private String birth;

    public String getDate() {

        String startTime = date;
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date dateValue = null;

        try {
            dateValue = input.parse(startTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateValue);
        calendar.add(Calendar.HOUR_OF_DAY, -3); //Timezone MDEO

        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        return (output.format(calendar.getTime())+"hs");
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getSalaryPhoto() {
        return salaryPhoto;
    }

    public void setSalaryPhoto(String salaryPhoto) {
        String photo = salaryPhoto.replace("http", "https");
        this.salaryPhoto = photo;
    }

}