package com.ppdai.das.console.dto.model;

import com.ppdai.das.console.dto.view.search.CheckTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppGroupModel {

    private Long id;

    private String name;

    private Integer serverEnabled;

    private Integer serverGroupId;

    private String comment;

    private String update_user_no;

    private Date create_time;

    private Date update_time;

    private List<Item> items;

    private String serverGroupName;

    private CheckTypes serverEnableds;

    private String projectNames;

}
