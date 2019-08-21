package com.xl.miaosha.dao;

import com.xl.miaosha.domain.MiaoshaOrder;
import com.xl.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id = #{userId} and goods_id = #{goodsId}")
    MiaoshaOrder getByUserIdAndGoodsId(@Param("userId")Long userId,@Param("goodsId")Long goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date) values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn = "id",keyProperty = "id",before = false,resultType = long.class,statement = "select last_insert_id()")
    long insertOrder(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id=#{id}")
    OrderInfo getById(@Param("id") long id);

    @Select("select * from miaosha_order where goods_id = #{goodsId}")
    List<MiaoshaOrder> getAllOrderByGoodsId(long goodsId);

}
