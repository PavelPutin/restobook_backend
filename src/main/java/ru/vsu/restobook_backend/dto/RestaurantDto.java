package ru.vsu.restobook_backend.dto;

public class RestaurantDto {

    private Long id;
    private String name;
    private String address;
    private String phoneNumber;

    public RestaurantDto() {
    }

    public RestaurantDto(Long id, String name, String address, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
