package com.example.test.domain.response.room;

import java.util.List;

import com.example.test.domain.response.user.ResponseUserDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseRoomDTO {
    private Long id;
    private String name;

    private List<ResponseUserDTO> participants;

}
