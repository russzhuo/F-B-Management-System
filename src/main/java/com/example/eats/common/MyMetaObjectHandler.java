package com.example.eats.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject){
        log.info("Automatic filling of public fields [insert]......");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrentid());
        metaObject.setValue("updateUser",BaseContext.getCurrentid());

    }

    @Override
    public void updateFill(MetaObject metaObject){
        log.info("Automatic filling of public fields [update]......");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentid());

//        long id = Thread.currentThread().getId();
//        log.info("tid: {}",id);

    }
}
