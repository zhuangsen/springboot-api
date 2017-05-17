package info.xiaomo.api.dao;

import info.xiaomo.api.model.ChangeLogModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 把今天最好的表现当作明天最新的起点．．～
 * いま 最高の表現 として 明日最新の始発．．～
 * Today the best performance  as tomorrow newest starter!
 * Created by IntelliJ IDEA.
 *
 * author: xiaomo
 * github: https://github.com/xiaomoinfo
 * email: xiaomo@xiaomo.info

 * Date: 2016/4/1119:52
 * Copyright(©) 2015 by xiaomo.
 **/
@Repository
public interface ChangeLogDao extends JpaRepository<ChangeLogModel, Long> {

    ChangeLogModel findByName(String name);

}
