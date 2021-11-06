package com.example.minimarket.model.bindings;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddressAddBindingModel {

    @NotBlank(message = "Street name cannot be null or empty String!!!")
    @Size(min = 1, message = "Street name must be more dan 1 character!!!")
    private String streetName;

    @NotNull(message = "Street number cannot be null!!!")
    @Min(value = 0, message = "Street number cannot be negativ number!!!")
    private int streetNumber;

    private String apartmentNumber;

    @NotBlank(message = "City name cannot be null or empty String!!!")
    @Size(min = 1, message = "City name must be more than 1 character!!!")
    private String city;

    @NotBlank(message = "Country name cannot be null or empty String!!!")
    @Size(min = 1, message = "City name must be more than 1 character!!!")
    private String country;

    @NotBlank(message = "Zip code cannot be null or empty String!!!")
    @Size(min = 1, message = "City name must be more than 1 character!!!")
    private String zipCode;

    public AddressAddBindingModel() {
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
