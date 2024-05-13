package com.example.demo.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Customer2 {
    private long id;
    private String fullName;
    private LocalDateTime createdAt;

    public Customer2(String fullName) {
        this.fullName = fullName;
        this.createdAt = LocalDateTime.now();
    }
}
