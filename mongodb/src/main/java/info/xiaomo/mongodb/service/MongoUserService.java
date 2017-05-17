package info.xiaomo.mongodb.service;

import info.xiaomo.mongodb.model.MongoUser;

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

 * Date: 2016/11/15 15:45
 * Copyright(©) 2015 by xiaomo.
 **/


public interface MongoUserService {

    List<MongoUser> findAll();

    MongoUser findById(Long id);

    MongoUser findByName(String userName);

    MongoUser add(MongoUser mongoUser);

    void delete(Long id);

    MongoUser update(MongoUser mongoUser);

}
