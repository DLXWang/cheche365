//package com.cheche365.cheche.unionpay
//
//import com.cheche365.cheche.common.http.RESTClient
//import com.cheche365.cheche.core.app.config.CoreConfig
//import com.cheche365.cheche.core.model.Payment
//import com.cheche365.cheche.core.model.PaymentChannel
//import com.cheche365.cheche.core.model.PurchaseOrder
//import com.cheche365.cheche.core.repository.PaymentRepository
//import com.cheche365.cheche.core.repository.PurchaseOrderRepository
//import com.cheche365.cheche.core.service.PaymentSupplementService
//import com.cheche365.cheche.unionpay.app.config.UnionPayConfig
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.test.context.ContextConfiguration
//import org.springframework.test.context.web.WebAppConfiguration
//import spock.lang.Specification
//
//import java.awt.Desktop
//
//import static groovyx.net.http.ContentType.*
//
///**
// * Created by zhengwei on 9/20/16.
// */
//
//@WebAppConfiguration
//@ContextConfiguration( classes = [ CoreConfig, UnionPayConfig ] )
//class UnionPayMultiPayFT extends Specification {
//
//    @Autowired
//    private PaymentSupplementService paymentSupplementService;
//    @Autowired
//    private PurchaseOrderRepository purchaseOrderRepository;
//
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    def '银联增补测试'(){
//
//        given:
//        def orderNo = System.getProperty("test.purchase.no");//订单号
//        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(orderNo);
//        Payment payment = paymentSupplementService.getAddPayPayment(purchaseOrder,0.1d,PaymentChannel.Enum.UNIONPAY_3);
//        paymentRepository.save(payment);
//        Long paymentId = payment.getId();
//        when:
//        RESTClient builder = new RESTClient("http://localhost:7310/");
//        builder.get(path: 'token', query: [id: System.getProperty("test.user.id")])
//
//        def response = builder.post(
//            path: 'v1.3/orders/'+orderNo+'/payment/additional',
//            body: [id: 3, paymentId: paymentId],
//            requestContentType: JSON
//        )
//        println(response.toString())
//        //def File file= new File("d:/test.html");//目标文件用于把html页面打印到该文件后面会打开该文件
//        def File file= new File(System.getProperty("test.dir.path")+"test.html");
//        if(!file.exists()){//是否存在该文件没有则创建(提示如果本地有该文件请确认之前的文件是否用用)
//            file.mkdirs();
//        }
//        PrintStream ps = new PrintStream(new FileOutputStream(file));//输出流
//        ps.println("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>银联支付</title></head>");// 往文件里写入字符串
//        ps.append("<body></body>");
//        ps.append(response.data.data);
//        ps.append("<script>document.forms['unionPayForm'].submit();</script>");
//        Desktop.getDesktop().browse(file.toURI());//打开文件
//        then:
//        response.status == 200
//    }
//}
