package com.ppdai.platform.das.codegen.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.platform.das.codegen.common.validates.group.comm.Login;
import com.ppdai.platform.das.codegen.common.validates.group.user.AddUser;
import com.ppdai.platform.das.codegen.common.validates.group.user.DeleteUser;
import com.ppdai.platform.das.codegen.common.validates.group.user.UpdateUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.JDBCType;
import java.util.Date;

/**
 * create by das-console
 * 请勿修改此文件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class LoginUser {

    public static final LoginUsersDefinition LOGINUSER = new LoginUsersDefinition();

    public static class LoginUsersDefinition extends TableDefinition {
        public final ColumnDefinition id;
        public final ColumnDefinition userNo;
        public final ColumnDefinition userName;
        public final ColumnDefinition userRealName;
        public final ColumnDefinition userEmail;
        public final ColumnDefinition password;
        public final ColumnDefinition insertTime;
        public final ColumnDefinition updateTime;
        public final ColumnDefinition updateUserNo;

        public LoginUsersDefinition as(String alias) {
            return _as(alias);
        }

        public LoginUsersDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public LoginUsersDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public LoginUsersDefinition() {
            super("login_users");
            id = column("id", JDBCType.BIGINT);
            userNo = column("user_no", JDBCType.VARCHAR);
            userName = column("user_name", JDBCType.VARCHAR);
            userRealName = column("user_real_name", JDBCType.VARCHAR);
            userEmail = column("user_email", JDBCType.VARCHAR);
            password = column("password", JDBCType.VARCHAR);
            insertTime = column("insert_time", JDBCType.TIMESTAMP);
            updateTime = column("update_time", JDBCType.TIMESTAMP);
            updateUserNo = column("update_user_no", JDBCType.VARCHAR);
            setColumnDefinitions(
                    id, userNo, userName, userRealName, userEmail, password, insertTime,
                    updateTime, updateUserNo
            );
        }
    }


    @NotNull(message = "{loginUser.id.notNull}", groups = {UpdateUser.class, DeleteUser.class})
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "{loginUser.userNo.notNull}", groups = {AddUser.class, UpdateUser.class})
    @Column(name = "user_no")
    private String userNo;

    @NotBlank(message = "{loginUser.userName.notNull}", groups = {Login.class, AddUser.class, UpdateUser.class})
    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_real_name")
    private String userRealName;

    @NotBlank(message = "{loginUser.userEmail.notNull}", groups = {AddUser.class, UpdateUser.class})
    @Column(name = "user_email")
    private String userEmail;

    @NotBlank(message = "{loginUser.password.notNull}", groups = {Login.class, AddUser.class})
    @Size(min = 6, max = 20, message = "{loginUser.password.size}", groups = {Login.class, AddUser.class})
    @Column(name = "password")
    private String password;

    @Column(name = "insert_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date create_time;

    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;

    /**
     * 最后操作人
     **/
    @Column(name = "update_user_no")
    private String update_user_no;

    private Integer role;

    private String adduser;

    private Integer intAdduser;

    private boolean isDalTeam = false;// true:是DAL Team，false:是正常用户

    private boolean active;

    private boolean isVisitor; //游客
}