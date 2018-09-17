package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.VehicleTypeRepository
import org.springframework.stereotype.Component

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

import static javax.persistence.GenerationType.IDENTITY

/**
 * 行驶证-车辆类型
 *
 * GA/T 16.4—2012 机动车车辆类型代码
 *
 * 机动车大类代码
 *
 * 代码 名称
 * K 客车
 * H 货车
 * Q 牵引车
 * Z 专项作业车
 * D 电车
 * M 摩托车
 * N 三轮汽车
 * T 拖拉机
 * J 轮式机械
 * G 全挂车
 * B 半挂车
 * X 其他
 *
 * 机动车车辆类型代码
 *
 * 代码 名称
 * K10 大型客车
 * K11 大型普通客车
 * K12 大型双层客车
 * K13 大型卧铺客车
 * K14 大型铰接客车
 * K15 大型越野客车
 * K16 大型轿车
 * K17 大型专用客车
 * K20 中型客车
 * K21 中型普通客车
 * K22 中型双层客车
 * K23 中型卧铺客车
 * K24 中型铰接客车
 * K25 中型越野客车
 * K27 中型专用客车
 * K30 小型客车
 * K31 小型普通客车
 * K32 小型越野客车
 * K33 小型轿车
 * K34 小型专用客车
 * K40 微型客车
 * K41 微型普通客车
 * K42 微型越野客车
 * K43 微型轿车
 * H10 重型货车
 * H11 重型普通货车
 * H12 重型厢式货车
 * H13 重型封闭货车
 * H14 重型罐式货车
 * H15 重型平板货车
 * H16 重型集装厢车
 * H17 重型自卸货车
 * H18 重型特殊结构货车
 * H19 重型仓栅式货车
 * H20 中型货车
 * H21 中型普通货车
 * H22 中型厢式货车
 * H23 中型封闭货车
 * H24 中型罐式货车
 * H25 中型平板货车
 * H26 中型集装厢车
 * H27 中型自卸货车
 * H28 中型特殊结构货车
 * H29 中型仓栅式货车
 * H30 轻型货车
 * H31 轻型普通货车
 * H32 轻型厢式货车
 * H33 轻型封闭货车
 * H34 轻型罐式货车
 * H35 轻型平板货车
 * H37 轻型自卸货车
 * H38 轻型特殊结构货车
 * H39 轻型仓栅式货车
 * H40 微型货车
 * H41 微型普通货车
 * H42 微型厢式货车
 * H43 微型封闭货车
 * H44 微型罐式货车
 * H45 微型自卸货车
 * H46 微型特殊结构货车
 * H47 微型仓栅式货车
 * H50 低速货车
 * H51 普通低速货车
 * H52 厢式低速货车
 * H53 罐式低速货车
 * H54 自卸低速货车
 * H55 仓栅式低速货车
 * Q10 重型牵引车
 * Q11 重型半挂牵引车
 * Q12 重型全挂牵引车
 * Q20 中型牵引车
 * Q21 中型半挂牵引车
 * Q22 中型全挂牵引车
 * Q30 轻型牵引车
 * Q31 轻型半挂牵引车
 * Q32 轻型全挂牵引车
 * Z11 大型专项作业车
 * Z21 中型专项作业车
 * Z31 小型专项作业车
 * Z41 微型专项作业车
 * Z51 重型专项作业车
 * Z71 轻型专项作业车
 * D11 无轨电车
 * D12 有轨电车
 * M10 三轮摩托车
 * M11 普通正三轮摩托车
 * M12 轻便正三轮摩托车
 * M13 正三轮载客摩托车
 * M14 正三轮载货摩托车
 *
 * Created by liheng on 2017/3/24 024.
 */
@Entity
class VehicleType {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    long id
    String code
    String description

    @Component
    static class Enum {

        static List<VehicleType> ALL

        private VehicleTypeRepository vehicleTypeRepository

        Enum(VehicleTypeRepository vehicleTypeRepository){
            this.vehicleTypeRepository = vehicleTypeRepository
            ALL = this.vehicleTypeRepository.findAll()
        }

        static findVehicleType(description) {
            ALL.find {
                it.description == description
            }
        }
    }
}
