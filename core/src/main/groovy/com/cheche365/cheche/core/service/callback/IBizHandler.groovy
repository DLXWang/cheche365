package com.cheche365.cheche.core.service.callback

import com.cheche365.cheche.core.model.Payment

interface IBizHandler {
    boolean handler(Payment payment, boolean isSuccess)
}
