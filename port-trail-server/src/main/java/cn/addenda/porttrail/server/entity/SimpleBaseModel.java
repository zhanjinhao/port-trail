package cn.addenda.porttrail.server.entity;

import cn.addenda.mybatisbasemodel.core.BaseModel;
import cn.addenda.mybatisbasemodel.core.annotation.BaseModelExpression;
import cn.addenda.mybatisbasemodel.core.annotation.InsertField;
import cn.addenda.mybatisbasemodel.core.annotation.UpdateField;
import cn.addenda.porttrail.common.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Stack;

/**
 * @author addenda
 * @since 2022/8/16 20:40
 */
@ToString
@JsonIgnoreProperties({
        SimpleBaseModel.F_CREATOR, SimpleBaseModel.F_CREATOR_NAME, SimpleBaseModel.F_CREATE_TIME, SimpleBaseModel.F_MODIFIER,
        SimpleBaseModel.F_MODIFIER_NAME, SimpleBaseModel.F_MODIFY_TIME, SimpleBaseModel.F_DELETE_DT, SimpleBaseModel.F_DELETE_FLAG})
public abstract class SimpleBaseModel implements Serializable, BaseModel {

  private static final long serialVersionUID = 1L;

  public static final String F_CREATOR = "creator";

  public static final String F_CREATOR_NAME = "creatorName";

  public static final String F_CREATE_TIME = "createDt";

  public static final String F_MODIFIER = "modifier";

  public static final String F_MODIFIER_NAME = "modifierName";

  public static final String F_MODIFY_TIME = "modifyDt";

  public static final String F_DELETE_DT = "deleteDt";

  public static final String F_DELETE_FLAG = "deleteFlag";

  @Getter
  @Setter
  @InsertField
  @BaseModelExpression(expression = USER_EL, ifObj = true)
  private String creator;

  @Setter
  @Getter
  @InsertField
  @BaseModelExpression(expression = USER_EL, ifObj = true)
  private String creatorName;

  @Getter
  @Setter
  @InsertField
  @JsonSerialize(using = LocalDateTimeStrSerializer.class)
  @JsonDeserialize(using = LocalDateTimeStrDeSerializer.class)
  @BaseModelExpression(expression = "now(3)", ifObj = false)
  private LocalDateTime createDt;

  @Getter
  @Setter
  @InsertField
  @UpdateField
  @BaseModelExpression(expression = USER_EL, ifObj = true)
  private String modifier;

  @Getter
  @Setter
  @InsertField
  @UpdateField
  @BaseModelExpression(expression = USER_EL, ifObj = true)
  private String modifierName;

  @Getter
  @Setter
  @InsertField
  @UpdateField
  @BaseModelExpression(expression = "now(3)", ifObj = false)
  @JsonSerialize(using = LocalDateTimeStrSerializer.class)
  @JsonDeserialize(using = LocalDateTimeStrDeSerializer.class)
  private LocalDateTime modifyDt;

  @Getter
  @Setter
  private String deleteFlag;

  @Getter
  @Setter
  @JsonSerialize(using = LocalDateTimeStrSerializer.class)
  @JsonDeserialize(using = LocalDateTimeStrDeSerializer.class)
  private LocalDateTime deleteDt;

  private static class LocalDateTimeStrDeSerializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
      JsonNode jsonNode = jp.getCodec().readTree(jp);
      final String s = jsonNode.asText();
      return DateUtils.parseLdt(s, DateUtils.yMdHmsS_FORMATTER);
    }
  }

  private static class LocalDateTimeStrSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jgen, SerializerProvider provider) throws IOException {
      jgen.writeString(DateUtils.format(localDateTime, DateUtils.yMdHmsS_FORMATTER));
    }
  }

  public static final String USER_EL = "T(cn.addenda.porttrail.server.entity.SimpleBaseModel).peekUser()";

  private static final ThreadLocal<Stack<String>> USER_TL = ThreadLocal.withInitial(() -> null);

  public static void removeUser() {
    USER_TL.remove();
  }

  public static void pushUser(String user) {
    Stack<String> users = USER_TL.get();
    if (users == null) {
      users = new Stack<>();
      USER_TL.set(users);
    }
    users.push(user);
  }

  public static void popUser() {
    Stack<String> users = USER_TL.get();
    if (users == null) {
      return;
    }
    users.pop();
    if (users.isEmpty()) {
      USER_TL.remove();
    }
  }

  public static String peekUser() {
    Stack<String> users = USER_TL.get();
    if (users == null) {
      return "addenda";
    }
    return users.peek();
  }

  public static void runWithUser(String user, Runnable runnable) {
    try {
      pushUser(user);
      runnable.run();
    } finally {
      popUser();
    }
  }

}
