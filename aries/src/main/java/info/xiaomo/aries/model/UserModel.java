package info.xiaomo.aries.model;

import info.xiaomo.core.base.BaseModel;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * author 小莫 (https://xiaomo.info) (https://github.com/xiaomoinfo)
 * @version : 2017/1/11 16:40
 */
@Entity
@Table(name = "user")
@ApiModel(value = "用户实体类")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel extends BaseModel {

    private String school;

    private String password;

    private String salt;

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
