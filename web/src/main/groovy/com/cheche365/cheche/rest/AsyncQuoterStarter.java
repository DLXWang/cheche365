package com.cheche365.cheche.rest;

import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.rest.processor.quote.QuoteProcessor;
import groovy.util.logging.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by zhengwei on 8/17/15.
 */
@Slf4j
public class AsyncQuoterStarter implements Runnable {

    private ExecutorService execSvc;
    private List<AsyncQuoter> asyncQuoterList;
    private QuoteProcessor quoteProcessor;

    @Override
    public void run() {

        QuoteRecord targetQuoteRecord = null;
        AsyncQuoter asyncQuoter = asyncQuoterList.get(0);
        Quoter quoter = asyncQuoter.getQuoter();

        try {

            List<Future<QuoteRecord>> results = execSvc.invokeAll(asyncQuoterList);
            asyncQuoter.getSpBroadCaster().quoteAllFinish();
            for (Future<QuoteRecord> quoteRecordFuture : results) {
                try {
                    QuoteRecord result = quoteRecordFuture.get();
                    if (null != result) {
                        targetQuoteRecord = result;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            quoteProcessor.saveAuto(quoter.getQuoteRecord().getAuto(), targetQuoteRecord, asyncQuoter.getQuoter().getAdditionalParameters());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AsyncQuoterStarter setExecSvc(ExecutorService execSvc) {
        this.execSvc = execSvc;
        return this;
    }

    public AsyncQuoterStarter setAsyncQuoterList(List<AsyncQuoter> asyncQuoterList) {
        this.asyncQuoterList = asyncQuoterList;
        return this;
    }

    public AsyncQuoterStarter setQuoteProcessor(QuoteProcessor quoteProcessor) {
        this.quoteProcessor = quoteProcessor;
        return this;
    }

}
