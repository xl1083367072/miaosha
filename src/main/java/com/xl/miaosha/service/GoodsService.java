package com.xl.miaosha.service;

import com.xl.miaosha.dao.GoodsDao;
import com.xl.miaosha.domain.MiaoshaGoods;
import com.xl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> list(){
        return goodsDao.list();
    }

    public GoodsVo getByGoodsId(Long id){
        return goodsDao.getByGoodsId(id);
    }

    public boolean reduceStock(GoodsVo goods){
        MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
        miaoshaGoods.setGoodsId(goods.getId());
        int res = goodsDao.reduceStock(miaoshaGoods);
        return res>0;
    }

}
