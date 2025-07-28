package com.brclys.thct.entity;


import jakarta.persistence.Embeddable;
import lombok.*;

@Data
@Embeddable
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Address {
    private String line1;
    private String line2;
    private String line3;
    private String town;
    private String county;
    private String postcode;
}