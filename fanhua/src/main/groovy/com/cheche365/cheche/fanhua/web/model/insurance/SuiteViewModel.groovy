package  com.cheche365.cheche.fanhua.web.model.insurance

import com.cheche365.cheche.fanhua.annotation.Essential
import org.hibernate.validator.internal.engine.messageinterpolation.parser.EscapedState

/**
 * Created by zhangtc on 2017/11/30.
 */
class SuiteViewModel {
    private String amount
    private String carkindriskcode
    private String charge
    private String eriskkindname
    @Essential
    private String ecode
}
