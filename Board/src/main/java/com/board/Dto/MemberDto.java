package com.board.Dto;

import com.board.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String email;
    private String password;
    private String company;

    public Member toEntity() {
        return Member.builder()
                .id(id)
                .email(email)
                .password(password)
                .company(company)
                .build();
    }

    @Builder
    public MemberDto(Long id, String email, String password,String company) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.company = company;
    }
}
