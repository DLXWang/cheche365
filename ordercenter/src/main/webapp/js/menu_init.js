//实例化菜单
var menu = {
    init: function () {
        var permissionCodeArray = common.permission.getPermissionCodeArray();
        if (permissionCodeArray && permissionCodeArray.length > 0) {
            //车辆订单管理
            if (permissionCodeArray.indexOf("or0101") >= 0
                || permissionCodeArray.indexOf("or0103") >= 0
                || permissionCodeArray.indexOf("or0104") >= 0
                || permissionCodeArray.indexOf("or0105") >= 0
                || permissionCodeArray.indexOf("or0107") >= 0
                || permissionCodeArray.indexOf("or0110") >= 0) {

                $("#vehicle_order_menu").show();
                if (permissionCodeArray.indexOf("or0101") >= 0) {//订单列表
                    $("#bj_order_list").show();
                }
                if (permissionCodeArray.indexOf("or0103") >= 0) {//订单查询
                    $("#order_search").show();
                }
                if (permissionCodeArray.indexOf("or0103") >= 0) {//停复驶查询
                    $("#stop_restart_search").show();
                }
                if (permissionCodeArray.indexOf("or0104") >= 0) {//分配订单
                    $("#order_redistribution_li").show();
                }
                if (permissionCodeArray.indexOf("or0105") >= 0) {//保单录入
                    $("#browser_insurance_input_li").show();
                }
                if (permissionCodeArray.indexOf("or0107") >= 0) {//临时订单详情
                    $("#bj_order_list_temp").show();
                }
                if (permissionCodeArray.indexOf("or0110") >= 0) {//安心保单回录
                    $("#answern_insurance_newly_input_li").show();
                }
            } else {
                $("#order_menu").remove();
            }
            //非车辆订单管理
            if (permissionCodeArray.indexOf("or10011") >= 0 || permissionCodeArray.indexOf("or10012") >= 0) {

                $("#health_order_menu").show();
                if (permissionCodeArray.indexOf("or10011") >= 0) {//订单列表
                    $("#health_order_list").show();
                }
                if (permissionCodeArray.indexOf("or10012") >= 0) {//订单操作
                    $("#health_order_excel").show();
                }
            } else {
                $("#health_order_menu").remove();
            }
            //客户管理
            if (permissionCodeArray.indexOf("or0201") >= 0) {
                $("#customer_menu").show();
                $("#customer_appointment").show();
            } else {
                $("#customer_menu").remove();
            }
            //代理人管理
            if (permissionCodeArray.indexOf("or0301") >= 0 || permissionCodeArray.indexOf("or0302") >= 0 || permissionCodeArray.indexOf("or0303") >= 0) {
                $("#agent_menu").show();

                if (permissionCodeArray.indexOf("or0302") >= 0) {//代理人列表
                    $("#list_agent_li").show();
                }

                if (permissionCodeArray.indexOf("or0303") >= 0) {//代理人审计列表
                    $("#list_agent_tmp_li").show();
                }
            } else {
                $("#agent_menu").remove();
            }
            //用户管理
            if (permissionCodeArray.indexOf("or0401") >= 0
                || permissionCodeArray.indexOf("or0402") >= 0
                || permissionCodeArray.indexOf("or0403") >= 0
                || permissionCodeArray.indexOf("or0404") >= 0) {
                $("#internal_user_menu").show();
                if (permissionCodeArray.indexOf("or0401") >= 0) {//新增用户
                    $("#add_internalUser_li").show();
                }
                if (permissionCodeArray.indexOf("or0402") >= 0) {//用户列表
                    $("#list_internalUser_li").show();
                }
                if (permissionCodeArray.indexOf("or0403") >= 0) {//外部用户列表
                    $("#outer_list_user_li").show();
                }
            } else {
                $("#internal_user_menu").remove();
            }
            //车辆报价
            if (permissionCodeArray.indexOf("or0501") >= 0 || permissionCodeArray.indexOf("or0502") >= 0) {
                $("#quote_menu").show();
                if (permissionCodeArray.indexOf("or0501") >= 0) {//拍照信息
                    $("#photo_quote_li").show();
                }
                if (permissionCodeArray.indexOf("or0502") >= 0) {//电话信息
                    $("#phone_quote_li").show();
                }
            } else {
                $("#quote_menu").remove();
            }
            //电话营销
            if (permissionCodeArray.indexOf("or0601") >= 0
                || permissionCodeArray.indexOf("or0602") >= 0
                || permissionCodeArray.indexOf("or0603") >= 0
                || permissionCodeArray.indexOf("or0604") >= 0
                || permissionCodeArray.indexOf("or0606") >= 0
            ) {
                $("#purpose_menu").show();
                if (permissionCodeArray.indexOf("or0601") >= 0) {//意向客户
                    $("#purpose_customer_li").show();
                }
                if (permissionCodeArray.indexOf("or0602") >= 0) {//查看工作
                    $("#work_details_li").show();
                }
                if (permissionCodeArray.indexOf("or0603") >= 0) {//修改指定跟进人
                    $("#customer_redistribution_li").show();
                }
                if (permissionCodeArray.indexOf("or0604") >= 0) {//新建客户
                    $("#new_customer_li").show();
                }
                if (permissionCodeArray.indexOf("or0606") >= 0) {//电话用户管理
                    $("#tel_marketer_li").show();
                }
            } else {
                $("#purpose_menu").remove();
            }

            //钱包管理
            var show_wallet_manager_menu_flag = false;
            //钱包列表
            if (permissionCodeArray.indexOf("or1101") >= 0) {
                $("#wallet_li").show();
                show_wallet_manager_menu_flag = true;
            }
            if (show_wallet_manager_menu_flag)
                $("#wallet_menu").show();
            else
                $("#wallet_menu").remove();

            /**
             * 全国出单中心
             */

            /**
             * 订单管理
             */
            var show_order_manager_menu_flag = false;
            //订单总控台
            if (permissionCodeArray.indexOf("or070101") >= 0) {
                $("#order_console_li").show();
                show_order_manager_menu_flag = true;
            }
            //全部订单
            if (permissionCodeArray.indexOf("or070102") >= 0) {
                $("#order_total_li").show();
                show_order_manager_menu_flag = true;
            }
            //订单新建
            if (permissionCodeArray.indexOf("or070103") >= 0) {
                $("#order_add_li").show();
                show_order_manager_menu_flag = true;
            }
            //订单异常
            if (permissionCodeArray.indexOf("or070104") >= 0) {
                $("#order_abnormity_li").show();
                show_order_manager_menu_flag = true;
            }
            //订单退款
            if (permissionCodeArray.indexOf("or070105") >= 0) {
                $("#order_refund_li").show();
                show_order_manager_menu_flag = true;
            }
            //已报价待审核
            if (permissionCodeArray.indexOf("or070106") >= 0) {
                $("#order_check_pending_li").show();
                show_order_manager_menu_flag = true;
            }
            //通过审核待结款
            if (permissionCodeArray.indexOf("or070107") >= 0) {
                $("#order_pay_pending_li").show();
                show_order_manager_menu_flag = true;
            }
            //结款完成待出单
            if (permissionCodeArray.indexOf("or070108") >= 0) {
                $("#order_pending_li").show();
                show_order_manager_menu_flag = true;
            }
            //已出单
            if (permissionCodeArray.indexOf("or070109") >= 0) {
                $("#order_done_li").show();
                show_order_manager_menu_flag = true;
            }
            //订单完成
            if (permissionCodeArray.indexOf("or070110") >= 0) {
                $("#order_finished_li").show();
                show_order_manager_menu_flag = true;
            }
            //订单查询
            if (permissionCodeArray.indexOf("or070111") >= 0) {
                $("#order_cooperation_select_li").show();
                show_order_manager_menu_flag = true;
            }

            if (show_order_manager_menu_flag) {
                $("#order_manager_menu").show();
            } else {
                $("#order_manager_menu").remove();
            }

            /**
             * 分站信息管理
             */
            var show_area_contact_info_manager_menu_flag = false;
            //分站信息管理
            if (permissionCodeArray.indexOf("or070201") >= 0) {
                $("#area_contact_info_li").show();
                show_area_contact_info_manager_menu_flag = true;
            }
            if (show_area_contact_info_manager_menu_flag) {
                $("#area_contact_info_manager_menu").show();
            } else {
                $("#area_contact_info_manager_menu").remove();
            }

            /**
             * 出单机构管理
             */
            var show_institution_manager_menu_flag = false;
            //出单机构管理
            if (permissionCodeArray.indexOf("or070301") >= 0) {
                $("#institution_li").show();
                show_institution_manager_menu_flag = true;
            }
            if (permissionCodeArray.indexOf("or070302") >= 0) {
                $("#institution_li_temp").show();
                show_institution_manager_menu_flag = true;
            }

            if (show_institution_manager_menu_flag) {
                $("#institution_manager_menu").show();
            } else {
                $("#institution_manager_menu").remove();
            }

            //全国出单中心
            if (show_order_manager_menu_flag || show_area_contact_info_manager_menu_flag || show_institution_manager_menu_flag) {
                $("#nationwide_order_menu").show();
            } else {
                $("#nationwide_order_menu").remove();
            }

            /**
             * 礼物订单管理
             */
            var show_new_year_pack_menu_flag = false;
            //礼物订单管理
            //兑换码导入
            if (permissionCodeArray.indexOf("or0802") >= 0) {
                $("#new_year_import_code_li").show();
                show_new_year_pack_menu_flag = true;
            }
            //按天买车险分享活动
            if (permissionCodeArray.indexOf("or0803") >= 0) {
                $("#daily_insurance_share_activity_li").show();
                show_new_year_pack_menu_flag = true;
            }
            //保单导入
            if (permissionCodeArray.indexOf("or0804") >= 0) {
                $("#insurance_import_li").show();
                show_new_year_pack_menu_flag = true;
            }
            if (show_new_year_pack_menu_flag) {
                $("#new_year_pack_menu").show();
            } else {
                $("#new_year_pack_menu").remove();
            }

            if (permissionCodeArray.indexOf("or1301") >= 0) {
                $('#importdata_menu').show();
                $("#import_data_li").show();
            }
            //财务数据查询
            if (permissionCodeArray.indexOf("or1302") >= 0) {
                $('#importdata_menu').show();
                $("#offlineinsurance_list_li").show();
            }

            //退运险查询
            var show_freight_insurance_flag = false;
            //退运险订单查询,退运险理赔查询
            if (permissionCodeArray.indexOf("or1201") >= 0) {
                $("#freight_insurance_purchase_order_li").show();
                $("#freight_insurance_claim_li").show();
                show_freight_insurance_flag = true;
            }
            if (show_freight_insurance_flag)
                $("#freight_insurance_menu").show();
            else
                $("#freight_insurance_menu").remove();

        }
    }
}
