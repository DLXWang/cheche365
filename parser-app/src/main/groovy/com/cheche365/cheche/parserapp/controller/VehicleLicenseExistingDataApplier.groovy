package com.cheche365.cheche.parserapp.controller

import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.IdentityType
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.service.spi.IThirdPartyVehicleLicenseService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Controller

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.core.util.AutoUtils.getAreaOfAuto
import static com.cheche365.cheche.parserapp.Constants._APP_VERSION

/**
 * 行驶证既有数据填充器
 * Created by Huabin on 2016/11/07.
 */
@Controller
@Slf4j
class VehicleLicenseExistingDataApplier implements CommandLineRunner {

    private static final _CLI = new CliBuilder().with {
        vleda   longOpt: 'vl-data-applier',     '行驶证既有数据填充器 <默认：false>', required: true
        idf     longOpt: 'input-data-file',     '输入数据文件（CSV格式）',    args: 1, argName: 'data-file',  required: true
        odf     longOpt: 'output-data-file',    '输出数据文件（CSV格式）',    args: 1, argName: 'data-file',  required: true
        fdf     longOpt: 'fail-data-file',      '失败数据文件（CSV格式）',    args: 1, argName: 'data-file',  required: true
        h       longOpt: 'help',                '打印此使用信息'
        v       longOpt: 'version',             '打印版本'

        usage = 'vleda [options]'
        header = """$_APP_VERSION
Options:"""
        footer = """
Report bugs to: zhanghb@cheche365.com"""

        formatter.leftPadding = 4
        formatter.syntaxPrefix = 'Usage: '
        width = formatter.width = 200

        it
    }

    @Autowired(required = false)
    @Qualifier("concurrentVehicleLicenseService")
    private IThirdPartyVehicleLicenseService vlService;


    @Override
    void run(String... args) throws Exception {

        def options = _CLI.parse args
        if (!options) {
            return
        }
        if (options.h) {
            _CLI.usage()
            return
        }
        if (options.v) {
            println _APP_VERSION
            return
        }

        def startTime = System.currentTimeMillis()

        def inputDataFile = options.idf as File
        def outputDataFile = options.odf as File
        def failDataFile = options.fdf as File

        def lines = inputDataFile.readLines().collect { line ->
            def (pid, licensePlateNo, engineNo, owner, vinNo, enrollDate, identity, brandCode) =
            line.tokenize('\t').collect { text ->
                text.replaceAll('NULL', '')
            }
            [
                pid           : pid,
                licensePlateNo: licensePlateNo,
                engineNo      : engineNo,
                owner         : owner,
                vinNo         : vinNo,
                enrollDate    : enrollDate,
                identity      : identity,
                brandCode     : brandCode
            ]
        }

        def outputWriter = outputDataFile.newPrintWriter()
        def failWriter = failDataFile.newPrintWriter()

        lines.withIndex().each { line, index ->
            def auto = new Auto(
                licensePlateNo: line.licensePlateNo,
                owner: line.owner,
                identity: line.identity,
                identityType: IdentityType.Enum.IDENTITYCARD
            )

            def area = getAreaOfAuto line.licensePlateNo
            VehicleLicense vehicleLicense = vlService.getVehicleLicense area, auto, [:]
            if (vehicleLicense) {
                line = mergeMaps true, line, [
                    pid           : null,
                    licensePlateNo: vehicleLicense.licensePlateNo,
                    engineNo      : vehicleLicense.engineNo,
                    owner         : vehicleLicense.owner,
                    vinNo         : vehicleLicense.vinNo,
                    enrollDate    : _DATE_FORMAT3.format(vehicleLicense.enrollDate),
                    identity      : vehicleLicense.identity,
                    brandCode     : vehicleLicense.brandCode
                ]
                outputWriter << line.values().collect { text ->
                    text ?: 'NULL'
                }.join('\t')
                outputWriter << '\n'
            } else {
                def failMsg = !line.identity || line.identity.contains('*') ? '身份证号不正确'
                    : line.vinNo.contains('*') || line.engineNo.contains('*') ? '车架号、发动机中间有*'
                        : '未查到'
                failWriter << [line.pid, line.owner, failMsg].join('\t')
                failWriter << '\n'
            }
            if ((index + 1) == lines.size() || (index + 1) % 100 == 0) {
                outputWriter.flush()
                failWriter.flush()
            }
        }

        log.debug '总耗时{}', (System.currentTimeMillis() - startTime) / 1000
    }

}
