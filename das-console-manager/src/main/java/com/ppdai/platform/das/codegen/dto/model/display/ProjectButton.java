package com.ppdai.platform.das.codegen.dto.model.display;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectButton {

    //添加按钮
    public boolean showAddButton;

    //编辑按钮
    public boolean showEditorButton;

    //删除按钮
    public boolean showDeleteButton;

    //下载按钮
    public boolean showDownloadButton;

    //同步数据按钮
    public boolean showSyncButton;

    //校验按钮
    public boolean showCkeckButton;

    //获取TOKEN
    public boolean showSaecButton;

    //详细信息按钮
    public boolean showDetailButton;
}
