package com.cheche365.cheche.ordercenter.test.excel.test;

import com.cheche365.cheche.common.excel.ExcelExportUtil;
import com.cheche365.cheche.common.excel.entity.enums.ExcelType;
import com.cheche365.cheche.common.excel.entity.params.ExcelExportEntity;
import com.cheche365.cheche.common.excel.entity.params.ExportParams;
import com.cheche365.cheche.core.app.config.CoreConfig;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.ordercenter.app.config.OrderCenterConfig;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = { OrderCenterConfig.class, CoreConfig.class }
)
@EnableAutoConfiguration
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
@TransactionConfiguration
public class ExcelExportUtilIdTest {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Test
    public void exportPurchaseOrders() throws Exception{


        Date currentTime=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.MINUTE, -30);
        Calendar calendar2=Calendar.getInstance();
        calendar2.setTime(currentTime);
        calendar2.add(Calendar.DAY_OF_MONTH,-31);
        List<PurchaseOrder> orders=purchaseOrderRepository.findUnPayOrderByCreateTimeBetween(calendar2.getTime(), calendar.getTime(), 0);
        ExportParams params = new ExportParams();
        params.setSecondTitle("订单统计");
        params.setTitleHeight((short) 20);
        params.setIsCreateHeadRows(true);
        params.setType(ExcelType.HSSF);
        params.setAddIndex(true);
        params.setTarget("target");
     //   params.setExclusions(new String[]{"应付金额", "实付金额"});
        OrderHandler orderHandler=new OrderHandler();
        orderHandler.setNeedHandlerFields(new String[]{"应付金额", "实付金额"});
        params.setDataHanlder(orderHandler);
        Workbook workbook = ExcelExportUtil.exportExcel(params, PurchaseOrder.class, orders);
        FileOutputStream fos = new FileOutputStream("d:/orders.xls");
        workbook.write(fos);
        fos.close();
    }

    /**
     * 测试单sheet里展示多个pojo对象数据
     * */
    @Test
    public void  createMorePojoinSheet()throws Exception{
        Date currentTime=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.MINUTE, -30);
        Calendar calendar2=Calendar.getInstance();
        calendar2.setTime(currentTime);
        calendar2.add(Calendar.DAY_OF_MONTH,-31);
        List<PurchaseOrder> orders=purchaseOrderRepository.findUnPayOrderByCreateTimeBetween(calendar2.getTime(), calendar.getTime(), 0);
        ExportParams params = new ExportParams();
      //  params.setSecondTitle("订单统计");
      //  params.setTitleHeight((short) 20);
        params.setSheetName("订单统计");
        params.setFreezeCol(1);
        params.setIsCreateHeadRows(true);
        params.setType(ExcelType.HSSF);
     //   params.setAddIndex(true);
       // params.setExclusions(new String[]{"应付金额", "实付金额"});
        Workbook workbook = ExcelExportUtil.exportExcel(params,new Class[]{PurchaseOrder.class,PurchaseOrder.class},new List[]{orders,orders});
        FileOutputStream fos = new FileOutputStream("d:/orderss.xls");
        workbook.write(fos);
        fos.close();
    }

    public static <T> T[] newArrayByArrayClass(Class<T[]> clazz, int length) {
        return (T[]) Array.newInstance(clazz.getComponentType(), length);
    }


    /**
     * 测试显示多个sheet
     * */
    @Test
    public void createMoreSheetByPojo() throws Exception{
        Date currentTime=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.MINUTE, -30);
        Calendar calendar2=Calendar.getInstance();
        calendar2.setTime(currentTime);
        calendar2.add(Calendar.DAY_OF_MONTH, -31);
        List list=new ArrayList<>();
        for(int i=0;i<2;i++){
            List<PurchaseOrder> orders=purchaseOrderRepository.findUnPayOrderByCreateTimeBetween(calendar2.getTime(), calendar.getTime(), 0);
            ExportParams params = new ExportParams("订单统计", "订单统计", "订单统计");
            params.setIsCreateHeadRows(true);
            params.setExclusions(new String[]{"应付金额","实付金额"});
            Map map=new HashMap();
            map.put("title",params);
            map.put("entity",PurchaseOrder.class);
            map.put("data",orders);
            list.add(map);
        }
        Workbook workbook = ExcelExportUtil.exportExcel(list,ExcelType.HSSF);
        FileOutputStream fos = new FileOutputStream("d:/sheet_orders.xls");
        workbook.write(fos);
        fos.close();
    }

    /**
     * map形式数据生成excel
     * */
    @Test
    public void createByArray() {
        try {
            List<ExcelExportEntity> entities = new ArrayList<ExcelExportEntity>();
            ExcelExportEntity excelentity = new ExcelExportEntity("部门", "depart");
            excelentity.setMergeVertical(true);
            excelentity.setMergeRely(new int[0]);
            entities.add(excelentity);
            entities.add(new ExcelExportEntity("姓名", "name"));
            entities.add(new ExcelExportEntity("电话", "phone"));
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            Map<String, Object> map;
            for (int i = 0; i < 10; i++) {
                map = new HashMap<String, Object>();
                map.put("depart", "产品部");
                map.put("name", "小明" + i);
                map.put("phone", "1311234567" + i);
                list.add(map);
            }
            for (int i = 0; i < 10; i++) {
                map = new HashMap<String, Object>();
                map.put("depart", "开发部");
                map.put("name", "小蓝" + i);
                map.put("phone", "1871234567" + i);
                list.add(map);
            }
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("员工通讯录", "通讯录"),
                entities, list);
            FileOutputStream fos = new FileOutputStream("d:/testMerge.xls");
            workbook.write(fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createMoreSheetByArray(){
       // try {
            List<ExcelExportEntity> entities = new ArrayList<ExcelExportEntity>();
            ExcelExportEntity excelentity = new ExcelExportEntity("部门", "depart");
            excelentity.setMergeVertical(true);
            excelentity.setMergeRely(new int[0]);
            entities.add(excelentity);
            entities.add(new ExcelExportEntity("姓名", "name"));
            entities.add(new ExcelExportEntity("电话", "phone"));
            Map dataSet=new HashMap<>();
            //sheet1
            List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
            Map<String, Object> map;
            for (int i = 0; i < 10; i++) {
                map = new HashMap<String, Object>();
                 map.put("depart", "产品部");
                map.put("name", "小明" + i);
                map.put("phone", "1311234567" + i);
                list1.add(map);
            }
            for (int i = 0; i < 10; i++) {
                map = new HashMap<String, Object>();
                   map.put("depart", "开发部");
                map.put("name", "小蓝" + i);
                map.put("phone", "1871234567" + i);
                list1.add(map);
            }
            dataSet.put("命名1",list1);

            //sheet2
            List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
            Map<String, Object> map2;
            for (int i = 0; i < 10; i++) {
                map2 = new HashMap<String, Object>();
                map2.put("depart", "产品部");
                map2.put("name", "小明" + i);
                map2.put("phone", "1311234567" + i);
                list2.add(map2);
            }
            for (int i = 0; i < 10; i++) {
                map2 = new HashMap<String, Object>();
                map2.put("depart", "开发部");
                map2.put("name", "小蓝" + i);
                map2.put("phone", "1871234567" + i);
                list2.add(map2);
            }
            dataSet.put("命名2", list2);


//            Workbook workbook = ExcelExportUtil.exportExcel(entities, dataSet, ExcelType.HSSF);
//            FileOutputStream fos = new FileOutputStream("d:/sheetsForMap.xls");
//            workbook.write(fos);
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }



}
