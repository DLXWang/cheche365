package com.cheche365.cheche.gshell.flow

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.gshell.flow.step.CheckSave
import com.cheche365.cheche.gshell.flow.step.DoLogin
import com.cheche365.cheche.gshell.flow.step.DoSaveNew
import com.cheche365.cheche.gshell.flow.step.Jcaptcha
import com.cheche365.cheche.gshell.flow.step.SfShiBieNew
import com.cheche365.cheche.gshell.flow.step.UploadSFBack
import com.cheche365.cheche.gshell.flow.step.UploadSFFace
import com.cheche365.cheche.parser.flow.steps.Decaptcha



/**
 * 所有流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        登录     : DoLogin,
        上传身份证正面: UploadSFFace,
        上传身份证反面: UploadSFBack,
        身份证识别   : SfShiBieNew,
        获取登录验证码: Jcaptcha,
        识别登录验证码: Decaptcha,
        检查信息   : CheckSave,
        保存信息   : DoSaveNew,
    ]


    private static get_FLOW_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    //<editor-fold defaultstate="collapsed" desc="VehicleLicense Flows">
    static final _ACQUISITION_FLOW = _FLOW_BUILDER {
//        loop({ 获取登录验证码 >> 识别登录验证码 >> 登录 }, 30) >> 上传身份证正面 >> 上传身份证反面 >> 身份证识别 >> 检查信息 >> 保存信息
        loop({ 获取登录验证码 >> 识别登录验证码 >> 登录 }, 15) >> 检查信息 >> 保存信息
    }

    //</editor-fold>

}
