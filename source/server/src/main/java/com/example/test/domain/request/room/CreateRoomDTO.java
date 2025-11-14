package com.example.test.domain.request.room;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class CreateRoomDTO {
    private String name;
    private List<Long> participantIds = new ArrayList<>();
}
