package com.cheche365.cheche.web.service;

import com.cheche365.cheche.core.service.spi.ISchemaService;
import com.cheche365.cheche.web.util.UrlUtil;
import org.springframework.stereotype.Service;

/**
 * Created by shanxf on 2017/8/15.
 */
@Service
public class SchemaService implements ISchemaService {


    @Override
    public String getSchema() {
        return UrlUtil.getSchema();
    }
}
