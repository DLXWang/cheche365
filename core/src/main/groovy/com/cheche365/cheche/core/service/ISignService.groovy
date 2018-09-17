package com.cheche365.cheche.core.service

interface ISignService<R, T1, T2> {

    R sign(T1 obj)

    Boolean isSigned(T2 obj)

}
