package com.ppdai.platform.das.console.dto.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSearchLogView {

    @Column(name = "id")
    private Integer id;

    @Column(name = "ip")
    private String ip;

    @Column(name = "request_type")
    private Integer request_type;

    @Column(name = "request")
    private String request;

    @Column(name = "success")
    private Boolean success;

    @Column(name = "result")
    private String result;

    @Column(name = "user_no")
    private String user_no;

    @Column(name = "inserttime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date insert_time;

    @Column(name = "updatetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;

    @Column(name = "isactive")
    private Integer isactive;

    @Column(name = "user_real_name")
    private String user_real_name;
}
