package com.l000phone.crawler.store.biz;


import com.l000phone.crawler.domain.Page;
import com.l000phone.crawler.store.IStoreBiz;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/**
 * Description：数据存储模块接口实现类（存储到HBASE中）<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月07日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class HBaseStoreBizImpl implements IStoreBiz{
    private Table table;
    public HBaseStoreBizImpl(){
        try {
            Connection connection = ConnectionFactory.createConnection();
            this.table = connection.getTable(TableName.valueOf("tb_product_info"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void store(Page page) {
        //拦截商品列表页面
        List<String> urls = page.getUrls();
        //若是列表页面，就不需要保存
        if(urls != null && urls.size() >0){
            return;
        }
        //步骤
        //查询
        try {
            Scan scan = new Scan();

            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
            filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("info"),Bytes.toBytes("goodsId"),CompareFilter.CompareOp.EQUAL,page.getGoodsId().getBytes()));
            filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("info"), Bytes.toBytes("source"), CompareFilter.CompareOp.EQUAL, page.getSource().getBytes()));
            
            scan.setFilter(filterList);
            ResultScanner scanner = table.getScanner(scan);
            Result nextResult= scanner.next();
            if (nextResult != null){
                //更新操作
                String rowId = Bytes.toString(nextResult.getRow());
                updateToHBase(page,rowId);
            }else {
                //新增操作
                saveToHbase(page);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToHbase(Page page) {
        byte[] rowKey = Bytes.toBytes(page.getId());
        commonDealWith(page,rowKey);
    }

    /**
     * 保存到HBASE中
     * @param page
     * @param rowId
     */
    private void updateToHBase(Page page, String rowId) {
        commonDealWith(page,Bytes.toBytes(page.getId()));
    }

    private void commonDealWith(Page page, byte[] rowKey) {
        try {
            Put put = new Put(rowKey);
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("goodsId"),Bytes.toBytes(page.getGoodsId()));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("source"),Bytes.toBytes(page.getSource()));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("url"),Bytes.toBytes(page.getUrl()));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("title"),Bytes.toBytes(page.getTitle()));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("imageUrl"),Bytes.toBytes(page.getImageUrl()));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("price"),Double.toString(page.getPrice()).getBytes());
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("commentCnt"),Integer.toString(page.getCommentCnt()).getBytes());
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("goodRate"),Double.toString(page.getGoodRate()).getBytes());
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("params"),Bytes.toBytes(page.getParams()));

            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
