package com.example.crud.models;

import jakarta.validation.*;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

public class ProductDto {

    @NotEmpty(message = "required")
    private String name;

    @Min(0)
    private double price;

    @Size(min = 1, message = "min text 1")
    @Size(max = 100, message = "max text 1")
    private String description;

    private MultipartFile image;

    public @NotEmpty(message = "required") String getName() {
        return name;
    }

    @Min(0)
    public double getPrice() {
        return price;
    }

    public @Size(min = 1, message = "min text 1") @Size(max = 100, message = "max text 1") String getDescription() {
        return description;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setName(@NotEmpty(message = "required") String name) {
        this.name = name;
    }

    public void setPrice(@Min(0) double price) {
        this.price = price;
    }

    public void setDescription(@Size(min = 1, message = "min text 1") @Size(max = 100, message = "max text 1") String description) {
        this.description = description;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
