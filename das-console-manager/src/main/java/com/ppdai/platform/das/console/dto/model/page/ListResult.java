package com.ppdai.platform.das.console.dto.model.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class ListResult<T>  {

    /**
     * 查询结果集
     */
    private List<T> list;

    /**
     * 总数
     */
    private long totalCount;

    /**
     * 当前页数
     */
    private int page;

    /**
     * 每页条数
     */
    private int pageSize;

}