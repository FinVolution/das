package com.ppdai.platform.das.console.dto.model.dataSearch;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DataSearchRequest {

    private Integer dbSetId;
    private String dbsetName;
    private String appid;
    private List<Integer> dbSetEntryIds;
    private String sql;
    private List<String> tableShardIds;
    @Deprecated
    private int limit;
}
