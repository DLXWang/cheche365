package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.model.VehicleContact
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.repository.VehicleContactRepository
import com.cheche365.cheche.core.repository.VehicleLicenseRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.Constants._DATE_FORMAT2
import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.misc.Constants._APP_VERSION
import static com.cheche365.cheche.misc.util.BusinessUtils.getDateInSpecificRange
import static java.time.ZonedDateTime.now

@Controller
@Transactional(rollbackFor = Exception)
@Slf4j
class VehicleLicenseDataImporter implements CommandLineRunner {


    private static final _CLI = new CliBuilder().with {
        vlimp   longOpt: 'vehicle-license-import',  '执行行驶证导入命令 <默认：false>', required: true
        idf     longOpt: 'input-data-file',         '输入数据文件（CSV格式）',  args: 1, argName: 'file', required: true
        odf     longOpt: 'output-data-file',        '输出数据文件（CSV格式）',  args: 1, argName: 'file'
        ctsd    longOpt: 'create-time-start-date',  '假的创建时间起始日期 <默认：今天>',  args: 1, argName: 'start-date'
        h       longOpt: 'help',                    '打印此使用信息'
        v       longOpt: 'version',                 '打印版本'

        usage = 'vlimp [options]'
        header = """$_APP_VERSION
选项："""
        footer = """
Report bugs to: zhanghb@cheche365.com"""

        formatter.leftPadding = 4
        formatter.syntaxPrefix = '用法：'
        width = formatter.width = 200

        it
    }


    @Autowired
    private VehicleLicenseRepository vlRepo

    @Autowired
    private VehicleContactRepository vcRepo



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

        def inputDataFile = options.idf as File
        def outputDataFile = options.odf ? options.odf as File : null
        def createTimeStartDate = options.ctsd ? getLocalDate(_DATE_FORMAT3.parse(options.ctsd)) : now().toLocalDate()
        def today = now()


        def startTime = System.currentTimeMillis()

        // 去重
        def lines = inputDataFile.readLines().collect { line ->
            def (pid, owner, mobile, licensePlateNo, vinNo, frameNo, engineNo, enrollDate) = line.tokenize(',')
            [pid, owner, mobile, licensePlateNo, vinNo, frameNo, engineNo, enrollDate]
        }.groupBy { _0, _1, _2, licensePlateNo, _4, _5, _6, _7 ->
            licensePlateNo
        }.collect { group, values ->
            values[0]
        }.findAll { _0, _1, _2, _3, vinNo, frameNo, engineNo, _7 ->
            (!vinNo.contains('*') || !frameNo.contains('*')) && !engineNo.contains('*')
        }
        def lineCount = lines.size()

        if (outputDataFile) {
            outputDataFile.withWriter { writer ->
                lines.each { line ->
                    writer << line.join(',')
                    writer << '\n'
                }
            }
        } else {
            lines.eachWithIndex { line, index ->
                def (pid, owner, mobile, licensePlateNo, vinNo, frameNo, engineNo, enrollDate) = line
                def vehicleLicense = vlRepo.findFirstByLicensePlateNoAndOwner licensePlateNo, owner
                def parsedEnrollDate = null
                try {
                    parsedEnrollDate = _DATE_FORMAT2.parse(enrollDate)
                } catch (ex) {
                    // do nothing
                }
                if (!vehicleLicense) {
                    vehicleLicense = vlRepo.save(new VehicleLicense(
                        licensePlateNo: licensePlateNo,
                        vinNo: getVinNo(vinNo, frameNo),
                        engineNo: engineNo,
                        owner: owner,
                        enrollDate: parsedEnrollDate,
                        identity: pid
                    ))
                } else {
                    if (!vehicleLicense.enrollDate) {
                        vehicleLicense.enrollDate = parsedEnrollDate
                    }
                    if (vehicleLicense.vinNo.contains('*')) {
                        vehicleLicense.vinNo = getVinNo(vinNo, frameNo)
                    }
                    if (vehicleLicense.engineNo.contains('*')) {
                        vehicleLicense.engineNo = engineNo
                    }
                    vehicleLicense.identity = pid
                }
                vcRepo.save(new VehicleContact(
                    mobile: mobile,
                    vehicleLicense: vehicleLicense,
                    createTime: getDateInSpecificRange(createTimeStartDate, today, lineCount, index)
                ))

                if (0 == index % 100) {
                    log.debug '目前已完成{}个记录，当前耗时{}', index + 1, (System.currentTimeMillis() - startTime) / 1000
                }
            }
        }

        log.debug '插入总计{}个vehicle contact记录，总耗时{}', lineCount, (System.currentTimeMillis() - startTime) / 1000
//        throw new Exception()
    }


    private static getVinNo(vinNo, frameNo) {
        !vinNo.contains('*') ? vinNo : frameNo
    }

}
