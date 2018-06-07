package com.drip.minio.util;


import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ExceptionProvider implements ExceptionMapper<Exception> {

        @Override
        public Response toResponse(Exception exception) {
            return Response.ok("{\"status\":\"500\",\"message\":\""+exception.getMessage()+"\"}",MediaType.APPLICATION_JSON).build();
        }

}
