package com.ppdai.platform.das.console.dto.model.display;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserManageButton {

    //添加按钮
    public boolean showAddButton;

    //编辑按钮
    public boolean showEditorButton;

    //模拟登陆按钮
    public boolean showSimLoginButton;
}
