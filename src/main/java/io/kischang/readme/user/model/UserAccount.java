package io.kischang.readme.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kis_user_account")
public class UserAccount {

    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.blinkfox.fenix.id.SnowflakeIdGenerator")
    private Long userId;

    private String username;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String salt;

    private String access_token;
    private String openid;

    private String nickname;
    private String headimgurl;


    private String reg_time;

    @Transient
    private String token;

}
