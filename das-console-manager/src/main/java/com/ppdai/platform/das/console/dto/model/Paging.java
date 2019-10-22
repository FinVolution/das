package com.ppdai.platform.das.console.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paging<T> {
    private Integer pageSize = 10;
    private Integer page = 1;
    private Long totalCount;
    private Integer offset;
    private String sort;
    private boolean ascending;
    private T data;

    public int getOffset() {
        return (page - 1) * pageSize;
    }

}
