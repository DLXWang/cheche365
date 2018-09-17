package com.cheche365.cheche.partner.utils

/**
 * Created by tongsong on 2016/8/9 0009.
 */
class EmailTemplate {

    //公共模板
    def static textPublic= '''\
        环境为:<%=l1 %></br>
        机器号为:<%=l2 %></br>
        '''


    //汽车之家第三方优惠券错误邮件模板
    def static textThr = '''\
        电话号码:<%=l1 %></br>
        汽车之家活动类型 activityType:<%=l2 %></br>
        第三方优惠id uid:<%=l3 %></br>
        '''

    //普通错误发送邮件模板
    def static textOrdinary = '''\
        订单号为:<%=l1 %></br>
        订单状态为:<%=l2 %></br>
        订单类型为:<%=l3 %></br>
        渠道为:<%=l4 %></br>
        创建时间为:<%=l5 %></br>
        修改时间为:<%=l6 %></br>
        '''

    //AH同步优惠活动错误邮件模板
    def static textMarketing = '''\
        同步状态：<%=l1 %></br>
        定时同步时间：<%=l2 %></br>
        活动列表：<%=l3 %></br>
        '''

}
