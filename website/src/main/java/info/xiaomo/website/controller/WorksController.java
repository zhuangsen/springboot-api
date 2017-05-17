package info.xiaomo.website.controller;

import info.xiaomo.core.base.BaseController;
import info.xiaomo.core.base.Result;
import info.xiaomo.core.constant.Code;
import info.xiaomo.website.model.WorksModel;
import info.xiaomo.website.service.WorksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

 * Date: 2016/11/3 14:36
 * Description: 用户实体类
 * Copyright(©) 2015 by xiaomo.
 **/


@Controller
@RequestMapping("/works")
public class WorksController extends BaseController {

    private final WorksService service;

    @Autowired
    public WorksController(WorksService service) {
        this.service = service;
    }


    @RequestMapping(value = "/findById/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable Long id) {
        WorksModel model = service.findById(id);
        if (model == null) {
            return new Result(Code.NULL_DATA.getResultCode(), Code.NULL_DATA.getMessage());
        }
        return new Result<>(model);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public Result findAll() {
        List<WorksModel> all = service.findAll();
        if (all == null || all.isEmpty()) {
            return new Result(Code.NULL_DATA.getResultCode(), Code.NULL_DATA.getMessage());
        }
        return new Result<>(all);
    }

    @Override
    public Result<Page> findAll(@PathVariable int start, @PathVariable int pageSize) {
        return null;
    }


    @RequestMapping(value = "/findByName/{name}", method = RequestMethod.GET)
    public Result findByName(@PathVariable String name) {
        WorksModel model = service.findByName(name);
        if (model == null) {
            return new Result(Code.NULL_DATA.getResultCode(), Code.NULL_DATA.getMessage());
        }
        return new Result<>(model);
    }

    @Override
    public Result<Boolean> delByName(@PathVariable String name) {
        return null;
    }

    @Override
    public Result<Boolean> delById(@PathVariable Long id) {
        return null;
    }

    @Override
    public Result<Boolean> add(@RequestBody Object model) {
        return null;
    }

    @Override
    public Result<Boolean> update(@RequestBody Object model) {
        return null;
    }

    @Override
    public Result<Boolean> delByIds(@PathVariable List ids) {
        return null;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody WorksModel model) {
        WorksModel addModel = service.findByName(model.getName());
        if (addModel != null) {
            return new Result(Code.REPEAT.getResultCode(), Code.REPEAT.getMessage());
        }
        addModel = service.add(model);
        return new Result<>(addModel);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result update(@RequestBody WorksModel model) {
        WorksModel worksModel = service.findById(model.getId());
        if (worksModel == null) {
            return new Result(Code.CodeOR.getResultCode(), Code.CodeOR.getMessage());
        }
        worksModel = service.update(worksModel);
        return new Result<>(worksModel);
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public Result delete(@PathVariable Long id) {
        WorksModel model = service.findById(id);
        if (model == null) {
            return new Result(Code.NULL_DATA.getResultCode(), Code.NULL_DATA.getMessage());
        }
        service.del(id);
        return new Result<>(model);
    }

}
