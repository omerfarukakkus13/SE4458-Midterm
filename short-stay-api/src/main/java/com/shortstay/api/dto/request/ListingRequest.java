package com.shortstay.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ListingRequest {

    @NotBlank(message = "Baslik bos birakilamaz")
    @Size(min = 5, max = 100)
    private String title;

    @Min(value = 1, message = "Kisi sayisi en az 1 olmalidir")
    private Integer noOfPeople;

    @NotBlank(message = "Ulke bilgisi zorunludur")
    private String country;

    @NotBlank(message = "Sehir bilgisi zorunludur")
    private String city;

    @Positive(message = "Fiyat pozitif bir deger olmalidir")
    private Double price;
}