package com.cheche365.cheche.core.service.spi

/**
 * 报价/核保/承保 接口
 * Created by suyq on 2016/12/21.
 */
interface IHandlerService<QO, QR, IO, IR, OO, OR> {

    /**
     * 报价
     * @param quoteObject
     * @return
     */
    QR quote(QO quoteObject)

    /**
     * 核保
     * @param insureObject
     * @return
     */
    IR insure(IO insureObject)

    /**
     * 承保
     * @param orderObject
     * @return
     */
    OR order(OO orderObject)

}
