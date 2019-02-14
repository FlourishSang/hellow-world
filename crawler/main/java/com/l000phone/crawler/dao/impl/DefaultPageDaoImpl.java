package com.l000phone.crawler.dao.impl;

import com.l000phone.crawler.dao.IPageDao;
import com.l000phone.crawler.domain.Page;
import com.l000phone.crawler.utils.DBCPUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.SQLException;

/**
 * Description：将解析后的页面数据保存到db中数据访问层接口实现类<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月04日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class DefaultPageDaoImpl implements IPageDao{
    private QueryRunner qr = new QueryRunner(DBCPUtils.getDataSource());

    @Override
    public void save(Page page) {
        //System.out.println("dsadad253---------------------------------------------------------------------");
            //先查询表中数据相同的数据是否存在
        try {
            Page bean = qr.query("select * from tb_product_info where goodsId=? and source=?",new BeanHandler<>(Page.class),page.getGoodsId(),page.getSource());
            //1)存在就更新
            if (bean != null){
                updateToDB(page,bean);
            }else {
                saveToDB(page);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveToDB(Page page) {
        String sql = "insert into tb_product_info values(?,?,?,?,?,?,?,?,?,?)";
        try {
            qr.update(sql, page.getId(),
                    page.getGoodsId(),
                    page.getSource(),
                    page.getUrl(),
                    page.getTitle(),
                    page.getImageUrl(),
                    page.getPrice(),
                    page.getCommentCnt(),
                    page.getGoodRate(),
                    page.getParams());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void updateToDB(Page page,Page beanFromDB) {
        String sql = "update  tb_product_info  set title=?," +
                "imageUrl=?," +
                "price=?," +
                "commentCnt=?," +
                "goodRate=?," +
                "params=? where id=?";
        try {
            qr.update(sql,
                    page.getTitle(),
                    page.getImageUrl(),
                    page.getPrice(),
                    page.getCommentCnt(),
                    page.getGoodRate(),
                    page.getParams(),
                    beanFromDB.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
