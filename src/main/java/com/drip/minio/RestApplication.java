package com.drip.minio;

import com.drip.minio.service.FileService;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Application;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lenovo on 2018/6/5.
 */
public class RestApplication extends Application {

    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> empty = new HashSet<Class<?>>();

    public RestApplication()
    {
        List<AsyncResponse> listeners = new ArrayList<AsyncResponse>();
        singletons.add(new FileService(listeners));
    }

    @Override
    public Set<Class<?>> getClasses()
    {
        return empty;
    }

    @Override
    public Set<Object> getSingletons()
    {
        return singletons;
    }
}
