package info.xiaomo.website.controller;

import freemarker.template.Configuration;
import info.xiaomo.core.base.BaseController;
import info.xiaomo.core.base.Result;
import info.xiaomo.core.constant.Code;
import info.xiaomo.core.constant.GenderType;
import info.xiaomo.core.exception.UserNotFoundException;
import info.xiaomo.core.untils.MD5Util;
import info.xiaomo.core.untils.RandomUtil;
import info.xiaomo.core.untils.TimeUtil;
import info.xiaomo.website.model.UserModel;
import info.xiaomo.website.service.UserService;
import info.xiaomo.website.util.MailUtil;
import info.xiaomo.website.view.UserView;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.List;

/**
 * 把今天最好的表现当作明天最新的起点．．～
 * いま 最高の表現 として 明日最新の始発．．～
 * Today the best performance  as tomorrow newest starter!
 * Created by IntelliJ IDEA.
 *
 * author: xiaomo
 * github: https://github.com/xiaomoinfo
 * email: xiaomo@xiaomo.info

 * Date: 2016/4/1 17:51
 * Description: 用户控制器
 * Copyright(©) 2015 by xiaomo.
 **/
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    private final UserService service;

    private final Configuration configuration;

    @Autowired
    public UserController(UserService service, Configuration configuration) {
        this.service = service;
        this.configuration = configuration;
    }

    @RequestMapping(value = "toLogin", method = RequestMethod.GET)
    public String toLogin() {
        return UserView.LOGIN.getName();
    }

    @RequestMapping(value = "toRegister", method = RequestMethod.GET)
    public String toRegister() {
        return UserView.REGISTER.getName();
    }

    /**
     * 登录
     *
     * @return Result<>
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        ModelMap map) {
        UserModel userModel = service.findUserByEmail(email);
        //找不到用户
        if (userModel == null) {
            map.put("CodeMsg", "找不到用户");
            return UserView.LOGIN.getName();
        }
        //密码不正确
        if (!MD5Util.encode(password, userModel.getSalt()).equals(userModel.getPassword())) {
            map.put("CodeMsg", "密码不正确");
            return UserView.LOGIN.getName();
        }
        session.setAttribute("currentUser", userModel);
        return UserView.INDEX.getName();
    }


    /**
     * 注册
     *
     * @return Result<>
     */
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public String register(@RequestParam("email") String email,
                           @RequestParam("password") String password,
                           ModelMap map) throws Exception {
        UserModel userModel = service.findUserByEmail(email);
        //邮箱被占用
        if (userModel != null) {
            map.put("CodeMsg", "邮箱被占用！");
            return UserView.REGISTER.getName();
        }
        String content = MailUtil.getContent(email, password, configuration);
        boolean send = MailUtil.send(email, "帐号激活邮件", content);
        if (!send) {
            map.put("CodeMsg", "邮件发送失败，请重试！");
            return UserView.REGISTER.getName();
        }
        return UserView.REGISTER_INFO.getName();
    }


    /**
     * 修改密码
     *
     * @return model
     * @throws UserNotFoundException UserNotFoundException
     */
    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    public Result<UserModel> changePassword(@RequestBody UserModel user) throws UserNotFoundException {
        UserModel userByEmail = service.findUserByEmail(user.getEmail());
        if (userByEmail == null) {
            return new Result<>(Code.USER_NOT_FOUND.getResultCode(), Code.USER_NOT_FOUND.getMessage());
        }
        String salt = RandomUtil.createSalt();
        userByEmail.setPassword(MD5Util.encode(user.getPassword(), salt));
        userByEmail.setNickName(user.getNickName());
        userByEmail.setSalt(salt);
        UserModel updateUser = service.updateUser(userByEmail);
        return new Result<>(updateUser);
    }

    /**
     * 更新用户信息
     *
     * @return model
     * @throws UserNotFoundException UserNotFoundException
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public Result<UserModel> update(@RequestBody UserModel user) throws UserNotFoundException {
        UserModel userModel = service.findUserByEmail(user.getEmail());
        if (userModel == null) {
            return new Result<>(Code.USER_NOT_FOUND.getResultCode(), Code.USER_NOT_FOUND.getMessage());
        }
        userModel = new UserModel();
        userModel.setEmail(user.getEmail());
        userModel.setNickName(user.getNickName());
        userModel.setPhone(user.getPhone());
        userModel.setAddress(user.getAddress());
        userModel.setGender(user.getGender());
        UserModel updateUser = service.updateUser(userModel);
        return new Result<>(updateUser);
    }



    /**
     * 处理激活
     */
    @RequestMapping(value = "validate", method = RequestMethod.GET)
    public String validateEmail(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam Long time,
            ModelMap map,
            HttpSession session
    ) throws ServiceException, ParseException, UserNotFoundException {
        //数据访问层，通过email获取用户信息
        UserModel userModel = service.findUserByEmail(email);
        if (userModel != null) {
            map.put("CodeMsg", "邮箱己被占用");
            return UserView.REGISTER.getName();
        }
        //验证码是否过期
        if (time + TimeUtil.ONE_DAY_IN_MILLISECONDS * 2 < TimeUtil.getNowOfMills()) {
            LOGGER.info("用户{}使用己过期时间{}激活邮箱失败！", email, time);
            map.put("CodeMsg", "时间己过期，请重新注册");
            return UserView.REGISTER.getName();
        }
        //激活
        String salt = RandomUtil.createSalt();
        userModel = new UserModel();
        userModel.setNickName(email);
        userModel.setEmail(email);
        userModel.setGender(GenderType.secret);
        userModel.setPhone(0L);
        userModel.setSalt(salt);
        userModel.setAddress("");
        userModel.setPassword(MD5Util.encode(password, salt));
        userModel = service.addUser(userModel);
        LOGGER.info("用户{}激活邮箱成功！", userModel.getEmail());
        session.setAttribute("currentUser", userModel);
        return UserView.INDEX.getName();
    }

    /**
     * 登出
     * @param session session
     * @return index
     */
    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logout(HttpSession session){
        UserModel userModel = (UserModel)session.getAttribute("currentUser");
        if (userModel!=null){
            session.setAttribute("currentUser",null);
        }
        return UserView.INDEX.getName();
    }


//*************************************************后台接口**********************************************************/

    /**
     * 根据id 查找用户
     *
     * @param id id
     * @return Result<>
     */
    @RequestMapping(value = "findById/{id}", method = RequestMethod.GET)
    public Result<UserModel> findUserById(@PathVariable("id") Long id) {
        UserModel userModel = service.findUserById(id);
        if (userModel == null) {
            return new Result<>(Code.USER_NOT_FOUND.getResultCode(), Code.USER_NOT_FOUND.getMessage());
        }
        return new Result<>(userModel);
    }

    /**
     * 添加用户
     */
    @RequestMapping(value = "addUser", method = RequestMethod.POST)
    public Result<UserModel> addUser(@RequestBody UserModel user) {
        UserModel userModel = service.findUserByEmail(user.getEmail());
        if (userModel != null) {
            return new Result<>(Code.USER_REPEAT.getResultCode(), Code.USER_REPEAT.getMessage());
        }
        String salt = RandomUtil.createSalt();
        user.setPassword(MD5Util.encode(user.getPassword(), salt));
        user.setSalt(salt);
        service.addUser(user);
        return new Result<>(user);
    }


    /**
     * 返回所有用户数据
     *
     * @return Result<>
     */
    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    public Result<List<UserModel>> getAll() {
        List<UserModel> pages = service.findAll();
        if (pages == null || pages.size() <= 0) {
            return new Result<>(Code.NULL_DATA.getResultCode(), Code.NULL_DATA.getMessage());
        }
        return new Result<>(pages);
    }


    /**
     * 根据id删除用户
     *
     * @param id id
     * @return Result<>
     */
    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public Result<UserModel> deleteUserById(@PathVariable("id") Long id) throws UserNotFoundException {
        UserModel userModel = service.deleteUserById(id);
        if (userModel == null) {
            return new Result<>(Code.USER_NOT_FOUND.getResultCode(), Code.USER_NOT_FOUND.getMessage());
        }
        return new Result<>(userModel);
    }



    /**
     * 查找所有(不带分页)
     *
     * @return result
     */
    @Override
    public Result<List> findAll() {
        return null;
    }

    /**
     * 带分页
     *
     * @param start    起始页
     * @param pageSize 页码数
     * @return result
     */
    @Override
    public Result<Page> findAll(@PathVariable int start, @PathVariable int pageSize) {
        return null;
    }

    @Override
    public Result<Boolean> findById(@PathVariable Long id) {
        return null;
    }

    @Override
    public Result<Boolean> findByName(@PathVariable String name) {
        return null;
    }

    /**
     * 根据名字删除模型
     *
     * @param name name
     * @return result
     */
    @Override
    public Result<Boolean> delByName(@PathVariable String name) {
        return null;
    }

    /**
     * 根据id删除模型
     *
     * @param id id
     * @return result
     */
    @Override
    public Result<Boolean> delById(@PathVariable Long id) {
        return null;
    }

    /**
     * 添加模型
     *
     * @param model model
     * @return result
     */
    @Override
    public Result<Boolean> add(@RequestBody Object model) {
        return null;
    }

    /**
     * 更新
     *
     * @param model model
     * @return result
     */
    @Override
    public Result<Boolean> update(@RequestBody Object model) {
        return null;
    }

    /**
     * 批量删除
     *
     * @param ids ids
     * @return result
     */
    @Override
    public Result<Boolean> delByIds(@PathVariable List ids) {
        return null;
    }


}
