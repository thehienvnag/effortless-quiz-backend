package com.example.springdemo.util;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class PageableWrapper {
    private Integer page = 0;
    private Integer size = 5;
    public Pageable getPageable() {
        return PageRequest.of(page, size, Sort.Direction.ASC, "id");
    }
}
