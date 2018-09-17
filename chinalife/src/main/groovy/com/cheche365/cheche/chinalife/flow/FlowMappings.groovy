package com.cheche365.cheche.chinalife.flow

import static com.cheche365.cheche.chinalife.flow.Flows._INSURANCE_BASIC_INFO_FLOW
import static com.cheche365.cheche.chinalife.flow.Flows._INSURANCE_INFO_FLOW
import static com.cheche365.cheche.chinalife.flow.Flows._INSURING_FLOW_TYPE1
import static com.cheche365.cheche.chinalife.flow.Flows._INSURING_FLOW_TYPE2
import static com.cheche365.cheche.chinalife.flow.Flows._INSURING_FLOW_TYPE3
import static com.cheche365.cheche.chinalife.flow.Flows._INSURING_FLOW_TYPE4
import static com.cheche365.cheche.chinalife.flow.Flows._INSURING_FLOW_TYPE5
import static com.cheche365.cheche.chinalife.flow.Flows._QUOTING_FLOW_TYPE1
import static com.cheche365.cheche.chinalife.flow.Flows._QUOTING_FLOW_TYPE2
import static com.cheche365.cheche.chinalife.flow.Flows._QUOTING_FLOW_TYPE3
import static com.cheche365.cheche.chinalife.flow.Flows._QUOTING_FLOW_TYPE4
import static com.cheche365.cheche.chinalife.flow.Flows._QUOTING_FLOW_TYPE5



/**
 * 流程步骤所需的常量
 */
class FlowMappings {

    /**
     * 为了支持全国报价，需要处理不同城市相同step使用不同请求参数以及响应处理器的情况。
     * 现定义了“城市 -> 请求参数生成器（Request Parameters Generator，简称RPG）”以及“城市 -> 响应处理器（Response Handler，简称RH）”的两个mapping，其中必须包含“default -> generator”
     * 和“default -> handler”。
     */

    static final _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS = [
        310000L : _QUOTING_FLOW_TYPE2, //上海
        440300L : _QUOTING_FLOW_TYPE2,//深圳
        330100L : _QUOTING_FLOW_TYPE2, //杭州
        510100L : _QUOTING_FLOW_TYPE2, //成都
        120000L : _QUOTING_FLOW_TYPE3, //天津
        110000L : _QUOTING_FLOW_TYPE1, //北京

        130100L : _QUOTING_FLOW_TYPE3, // 石家庄
        150100L : _QUOTING_FLOW_TYPE2, // 呼和浩特
        370100L : _QUOTING_FLOW_TYPE3, // 济南
        460100L : _QUOTING_FLOW_TYPE3, // 海口
        530100L : _QUOTING_FLOW_TYPE3, // 昆明
        230100L : _QUOTING_FLOW_TYPE3, // 哈尔滨
        320100L : _QUOTING_FLOW_TYPE5, // 南京
        140100L : _QUOTING_FLOW_TYPE3, // 太原
        210100L : _QUOTING_FLOW_TYPE3, // 沈阳
        220100L : _QUOTING_FLOW_TYPE3, // 长春
        340100L : _QUOTING_FLOW_TYPE3, // 合肥
        360100L : _QUOTING_FLOW_TYPE3, // 南昌
        430100L : _QUOTING_FLOW_TYPE3, // 长沙
        450100L : _QUOTING_FLOW_TYPE3, // 南宁
        520100L : _QUOTING_FLOW_TYPE3, // 贵阳
        650100L : _QUOTING_FLOW_TYPE2, // 乌鲁木齐

        440900L : _QUOTING_FLOW_TYPE3, //茂名

        330200L : _QUOTING_FLOW_TYPE3, //宁波,

        210200L : _QUOTING_FLOW_TYPE4, // 大连

        default : _QUOTING_FLOW_TYPE4
    ]

    static final _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS = [
        310000L : _INSURING_FLOW_TYPE2, //上海
        330100L : _INSURING_FLOW_TYPE2, //杭州
        440300L : _INSURING_FLOW_TYPE2, //深圳
        510100L : _INSURING_FLOW_TYPE2, //成都
        120000L : _INSURING_FLOW_TYPE3, //天津
        110000L : _INSURING_FLOW_TYPE1,  //北京

        130100L : _INSURING_FLOW_TYPE3, // 石家庄
        150100L : _INSURING_FLOW_TYPE2, // 呼和浩特
        370100L : _INSURING_FLOW_TYPE3, // 济南
        460100L : _INSURING_FLOW_TYPE3, // 海口
        530100L : _INSURING_FLOW_TYPE3, // 昆明
        230100L : _INSURING_FLOW_TYPE3, // 哈尔滨
        320100L : _INSURING_FLOW_TYPE5, // 南京

        140100L : _INSURING_FLOW_TYPE3, // 太原
        210100L : _INSURING_FLOW_TYPE3, // 沈阳
        220100L : _INSURING_FLOW_TYPE3, // 长春
        340100L : _INSURING_FLOW_TYPE3, // 合肥
        360100L : _INSURING_FLOW_TYPE3, // 南昌
        430100L : _INSURING_FLOW_TYPE3, // 长沙
        450100L : _INSURING_FLOW_TYPE3, // 南宁
        520100L : _INSURING_FLOW_TYPE3, // 贵阳
        650100L : _INSURING_FLOW_TYPE2, // 乌鲁木齐

        440900L : _INSURING_FLOW_TYPE3, //茂名

        330200L : _INSURING_FLOW_TYPE3, //宁波

        210200L : _INSURING_FLOW_TYPE4, // 大连

        default : _INSURING_FLOW_TYPE4

    ]

    static final _FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS = [
        default : _INSURANCE_BASIC_INFO_FLOW
    ]

    static final _FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS = [
        default : _INSURANCE_INFO_FLOW
    ]

}
