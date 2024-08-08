package com.example.simpe_restapi.item;

import jakarta.persistence.*;

@Entity
@Table(name = "item") // 테이블 이름을 명시적으로 지정
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    // 기본 생성자
    public Item() {
    }

    // 생성자
    public Item(String name) {
        this.name = name;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
