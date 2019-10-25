package com.ppdai.das.console.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:msg/message.properties")
public class Message {

    @Value("${db.message.replace.operation.failed}")
    public String db_message_add_operation_failed;

    @Value("${db.message.delete.operation.failed}")
    public String db_message_delete_operation_failed;

    @Value("${db.message.update.operation.failed}")
    public String db_message_update_operation_failed;

    @Value("${group.message.user.ingroup}")
    public String group_message_user_ingroup;

    @Value("${group.message.no.pemission}")
    public String group_message_no_pemission;

    @Value("${user.replace.fail.is.exist}")
    public String user_add_fail_is_exist;

    @Value("${user.replace.fail.no.is.exist}")
    public String user_add_fail_no_is_exist;

    @Value("${user.replace.fail.not.exist}")
    public String user_add_fail_not_exist;

    @Value("${permisson.user.crud}")
    public String permisson_user_crud;

    @Value("${message.operation.pemission}")
    public String message_operation_pemission;

    @Value("${user.add.password.not.null")
    public String user_add_password_not_null;

}
