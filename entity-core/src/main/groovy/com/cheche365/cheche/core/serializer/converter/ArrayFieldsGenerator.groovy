package com.cheche365.cheche.core.serializer.converter

import com.cheche365.cheche.common.util.ConvertmoneyUtils
import com.cheche365.cheche.core.exception.BusinessException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Service

import java.beans.PropertyDescriptor
import java.lang.reflect.InvocationTargetException
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Created by zhengwei on 4/25/16.
 */

@Service
abstract class ArrayFieldsGenerator {

    private static Logger logger = LoggerFactory.getLogger(ArrayFieldsGenerator.class);

    static final DecimalFormat FORMATER = new DecimalFormat("0.00")
    static {
        FORMATER.setRoundingMode(RoundingMode.HALF_UP)
    }

    public static List<String> FIELD_ORDER = ["compulsoryPremium", "autoTax", "thirdParty", "damage", "glass", "scratch", "driver", "passenger", "theft", "spontaneousLoss", "engine", "unableFindThirdParty", "designatedRepairShop", "iopTotal"]

    private final static Map<String, String[]> NAME_TO_ATTR = [
        "compulsoryPremium" : ["机动车交通事故责任强制险", "交强", Field.FieldGroup.compulsory.toString()],
        "thirdParty" : ["机动车第三者责任保险", "三者", Field.FieldGroup.base.toString()],
        "damage" : ["机动车损失险", "车损", Field.FieldGroup.base.toString()],
        "glass" : ["玻璃单独破碎险", "玻璃", Field.FieldGroup.base.toString()],
        "scratch" : ["车身划痕损失险", "划痕", Field.FieldGroup.base.toString()],
        "driver" : ["车上人员责任险(司机)", "司机", Field.FieldGroup.base.toString()],
        "passenger": ["车上人员责任险(乘客)", "乘客", Field.FieldGroup.base.toString()],
        "theft" : ["机动车盗抢险", "盗抢", Field.FieldGroup.base.toString()],
        "spontaneousLoss" : ["自燃损失险", "自燃", Field.FieldGroup.base.toString()],
        "engine": ["发动机特别损失险(涉水险)", "涉水", Field.FieldGroup.base.toString()],
        "unableFindThirdParty": ["无法找到第三方特约险", "无法找到第三方", Field.FieldGroup.base.toString()],
        "designatedRepairShop": ["指定专修厂险", "指定专修厂险", Field.FieldGroup.base.toString()],
        "iopTotal": ["不计免赔险", "不计免赔", Field.FieldGroup.base.toString()]
    ]

    public final static Map<String, String[]> TWO_GROUPS = [  //将所有险种分成商业，交强两组的情况，其中交强包括交强险＋车船税
        "autoTax" : ["车船使用税", "车船税", Field.FieldGroup.compulsory.toString()]
    ] + NAME_TO_ATTR;

    public final static Map<String, String[]> THREE_GROUPS = [ //将所有险种分成商业，交强，车船税三组的情况
        "autoTax" : ["车船使用税", "车船税", Field.FieldGroup.autoTax.toString()]
    ] + NAME_TO_ATTR;

    enum GroupPolicy {
        Two, Three
    }

    static enum FieldType {
        Premium, Amount, Iop
    }



    static FIELDS_WITH_CRAZY_NAME = ["compulsoryPremium", "autoTax", "iopTotal"];
    static FIELDS_WITHOUT_AMOUNT = ["compulsoryPremium", "autoTax", "engine", "glass", "iopTotal", "unableFindThirdParty", "designatedRepairShop"];
    static FIELDS_WITHOUT_IOP = ["compulsoryPremium", "autoTax", "glass", "iopTotal", "unableFindThirdParty", "designatedRepairShop"];

    Map<String, Object> total = new HashMap();  //之所以用object不用double是因为double的0会显示为0.0，需要要求是0
    List<Field> fields = []
    List<Map<String,Object>> discounts= []
    Long quoteSource

    void toArray(Object oneBill, Object another) {
        this.toArray(oneBill, another, GroupPolicy.Two)

    }

    void toArray(Object oneBill, Object another, GroupPolicy groupPolicy) {
        this.toArray(oneBill, groupPolicy)
        this.toArray(another, groupPolicy)

    }

    void toArray(Object bill){
        this.toArray(bill, GroupPolicy.Two)
    }

    void toArray(Object bill, GroupPolicy groupPolicy){
        if(null == bill){
            logger.debug("待序列化对象为空，跳过格式转换步骤");
            return;
        }

        initTotal(groupPolicy==GroupPolicy.Two ? [Field.FieldGroup.base, Field.FieldGroup.compulsory] : [Field.FieldGroup.base, Field.FieldGroup.compulsory, Field.FieldGroup.autoTax]);
        this.doBeforeMerge()

        def targetFields = groupPolicy==GroupPolicy.Two ? TWO_GROUPS : THREE_GROUPS
        targetFields.each {name, attrs -> fields.add(getMergedField(name, attrs, bill))}

        fields = fields.findAll{it != null}

        fields.sort{o1, o2 -> FIELD_ORDER.indexOf(o1.name) - FIELD_ORDER.indexOf(o2.name)}
        doAfterMerge(this.fields);

    }

    abstract protected void doBeforeMerge();

    abstract protected void doAfterMerge(List<Field> fields);

    Field getMergedField(String fieldName, List attrs, Object bill){

        PropertyDescriptor premiumDescriptor = toPropertyDescriptor(bill, fieldName, FieldType.Premium);
        if(!premiumDescriptor) {
            return null;
        }

        try {

            Double premium = (Double)premiumDescriptor.getReadMethod().invoke(bill);


            if(emptyValue(bill, fieldName, premium)){  //只处理报出价的险种，车船税免缴(autoTaxFree)的情况也要处理
                return null;
            }

            calculateTotal(attrs[2], premium);

            Field field = new Field(attrs[2]);
            field.setName(fieldName)
                 .setDisplayName(attrs[0])
                 .setShortName(attrs[1])
                 .setPremium(premium);

            if(hasAmount(fieldName)){
                PropertyDescriptor amountDescriptor = toPropertyDescriptor(bill, fieldName, FieldType.Amount);
                Double amount = (Double)amountDescriptor.getReadMethod().invoke(bill);
                field.getAmount().put("value", amount);
                field.getAmount().put("text", ConvertmoneyUtils.convertMoneyWan(amount));
            }

            if(isGlass(fieldName)){  //玻璃险保额格式特殊，单独处理

                field.getAmount().put("value", bill.insurancePackage?.glassType?.name ? bill.insurancePackage.glassType.id : 1);
                field.getAmount().put("text", bill.insurancePackage?.glassType?.name);
            }

            if(hasIop(fieldName)){
                PropertyDescriptor amountDescriptor = toPropertyDescriptor(bill, fieldName, FieldType.Iop);
                Double iop = (Double)amountDescriptor.getReadMethod().invoke(bill);
                field.setIop(iop);
            }

            return field;

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();  //should never happen
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "序列化报价时反射调用发生异常: "+e.getMessage());
        }

    }

    def initTotal(groups) {
        groups.each{
            if(!this.total.containsKey(it.toString())) {
                this.total.put(it.toString(), 0.0)
            }
        }
    }

    private void calculateTotal(String group, Double premium) {
        this.total.put(group, ((this.total.get(group) as Double) + premium));
    }


    static boolean hasAmount(String fieldName) {
        return !FIELDS_WITHOUT_AMOUNT.contains(fieldName);
    }

    static boolean hasIop(String fieldName) {
        return !FIELDS_WITHOUT_IOP.contains(fieldName);
    }

    private boolean isGlass(String fieldName){
        return "glass" == fieldName;
    }

    protected boolean emptyValue(Object bill, String fieldName, Double premium){
        return premium == null || premium <= 0.0;
    }

    private PropertyDescriptor toPropertyDescriptor(Object bill, String fieldName, FieldType type){
        return BeanUtils.getPropertyDescriptor(bill.getClass(), toFullName(fieldName, type));
    }


    static String toFullName(String fieldName, FieldType type){

        switch (type){
            case FieldType.Premium :
                return FIELDS_WITH_CRAZY_NAME.contains(fieldName) ? fieldName : (fieldName+type.toString());
            case FieldType.Amount :
                return fieldName + type.toString();
            case FieldType.Iop :
                return fieldName + type.toString();
            default :
                throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "非预期险种类型: "+type);
        }

    }

    //小数点后不足两位的，用0补齐
    static String prettyPrintDouble(Double value) {

        value ? FORMATER.format(value) : FORMATER.format(0)
    }
}
