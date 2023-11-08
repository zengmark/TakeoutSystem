package com.takeout.takeoutmenuservice.mapper;

import com.takeout.takeoutmodel.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 13123
* @description 针对表【menu(菜品表)】的数据库操作Mapper
* @createDate 2023-11-06 20:43:13
* @Entity com.takeout.takeoutmodel.entity.Menu
*/
public interface MenuMapper extends BaseMapper<Menu> {
    @Select("select * from menu where id = 9")
    Menu test();

    List<Menu> getHistoryMenus(List<Long> historyIds);
}




