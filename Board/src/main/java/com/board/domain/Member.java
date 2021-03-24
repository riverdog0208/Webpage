package com.board.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String company;

    @Builder
    public Member(Long id, String email, String password, String company) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.company = company;
    }
}

