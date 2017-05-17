package info.xiaomo.aries.service.impl;

import info.xiaomo.aries.dao.UserDao;
import info.xiaomo.aries.model.UserModel;
import info.xiaomo.aries.service.UserService;
import info.xiaomo.core.untils.MD5Util;
import info.xiaomo.core.untils.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;

/**
 * author 小莫 (https://xiaomo.info) (https://github.com/xiaomoinfo)
 * @version : 2017/1/11 16:39
 */

public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserModel findById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public UserModel findByName(String name) {
        return userDao.findByName(name);
    }

    @Override
    public List<UserModel> findAll() {
        return userDao.findAll();
    }

    @Override
    public Page<UserModel> findAll(int start, int pageSize) {
        return userDao.findAll(new PageRequest(start, pageSize));
    }

    @Override
    public boolean delById(Long id) {
        try {
            userDao.delete(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delByName(String name) {
        return userDao.deleteByName(name);
    }

    @Override
    public boolean add(UserModel model) {
        UserModel userModel = userDao.findByName(model.getName());
        if (userModel != null) {
            LOGGER.debug("用户{}己经存在", userModel.getName());
            return false;
        }
        model.setCreateTime(new Date());
        model.setUpdateTime(new Date());
        String salt = RandomUtil.createSalt();
        String password = MD5Util.encode(model.getPassword(), salt);
        model.setPassword(password);
        model.setSalt(salt);
        userDao.save(model);
        return true;
    }

    @Override
    public boolean update(UserModel model) {
        UserModel userModel = userDao.findById(model.getId());
        if (userModel == null) {
            LOGGER.debug("用户{}不存在", model.getName());
            return false;
        }
        String salt = RandomUtil.createSalt();
        String password = MD5Util.encode(model.getPassword(), salt);
        model.setPassword(password);
        model.setSalt(salt);
        model.setUpdateTime(new Date());
        userDao.save(model);
        return true;
    }

    @Override
    public boolean delByIds(List<Long> ids) {
        for (Long id : ids) {
            userDao.delete(id);
        }
        return true;
    }
}
