package com.cheche365.cheche.web.counter.counterimp;

import com.cheche365.cheche.web.counter.icounter.APICounter;
import com.cheche365.cheche.core.constants.CounterConstants
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yuhao on 2015/9/6.
 */
@Lazy
@Service
class QuoteDefaultCounter extends APICounter {

    QuoteDefaultCounter(HttpServletRequest request, StringRedisTemplate template) {
        super(request, template);
    }

    @Override
    String apiName() {
        return CounterConstants.KEY_QUOTE;
    }


}

