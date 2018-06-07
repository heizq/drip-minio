/*
 * Minio Java Example, (C) 2016 Minio, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drip.minio.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.drip.minio.dto.RequestDto;
import com.drip.minio.util.FileFormatUtil;
import com.drip.minio.vo.FileVo;
import com.drip.minio.vo.ResultVo;
import com.google.common.base.Strings;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;


@Path("/file")
public class FileService {


    List<AsyncResponse> listeners;

    public FileService(List<AsyncResponse> listeners)
    {
        this.listeners = listeners;
    }


    @GET
    @Path("/list")
    @Produces({MediaType.APPLICATION_JSON})
    public List<FileVo> listAlbums() {
        List<FileVo> list = new ArrayList<FileVo>();
        try{
            MinioTemplate template = MinioTemplate.getInstance();
            MinioClient minioClient = template.getMinioClient();
            final String minioBucket = template.getBucket();

            Iterable<Result<Item>> myObjects = minioClient.listObjects(minioBucket);

            for (Result<Item> result : myObjects) {
                Item item = result.get();
                String url = minioClient.presignedGetObject(minioBucket, item.objectName(), 60 * 60 * 24);
                FileVo fileVo = new FileVo();
                fileVo.setUrl(url);
                list.add(fileVo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    @POST
    @Path("/bucket/{bucketName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResultVo createBucket(@PathParam("bucketName") String bucketName) {
        ResultVo result = new ResultVo();
        try{
            MinioTemplate template = MinioTemplate.getInstance();
            template.createBucket(bucketName);
            result.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
        }

        return result;
    }


    @POST
    @Path("/upload")
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_JSON)
    public ResultVo addTeachingResource(MultipartFormDataInput input) throws Exception {
        ResultVo result = new ResultVo();
        FileVo fileVo = new FileVo();
        InputStream resInputStream = null;
        try{
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
            /*String voJson = uploadForm.get("keyVo").get(0).getBodyAsString();
            fileVo = JSON.parseObject(voJson, new TypeReference<FileVo>() {});*/
            List<InputPart> inputParts = uploadForm.get("file");
            if (inputParts == null || inputParts.size() <= 0) {
                throw new Exception("参数file:必须值没有传入.");
            }
            resInputStream = inputParts.get(0).getBody(InputStream.class, null);
            MultivaluedMap<String, String> header = inputParts.get(0).getHeaders();
            String fileName = getFileName(header);

            fileName = FileFormatUtil.getUniqueFileName(fileName);

            MinioTemplate template = MinioTemplate.getInstance();
            String fileRelativePath = template.uploadFile(resInputStream,template.getBucket(),fileName);

            result.setResult("/" + template.getBucket() + "/" + fileRelativePath);
            result.setSuccess(true);
        }catch (Exception e){
            result.setSuccess(false);
            throw new Exception("upload file to minio failed.");
        }finally {
            if (resInputStream != null) {
                resInputStream.close();
            }
        }

        return result;


    }


    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                int length = finalFileName.length();
                if(length > 12){
                    finalFileName = finalFileName.substring(length - 12);
                }
                return finalFileName;
            }
        }
        return "unknown";
    }

    @DELETE
    @Path("/object/{objectName:.*}")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ResultVo removeObject(@PathParam("objectName") String objectName)  {
        ResultVo resultVo = new ResultVo();
        try {
            if (Strings.isNullOrEmpty(objectName)) {
                throw new Exception("参数必须值没有传入.");
            }
            MinioTemplate template = MinioTemplate.getInstance();
            template.deleteFile(template.getBucket(),objectName);
            resultVo.setSuccess(true);

        } catch (Exception e) {
            resultVo.setSuccess(false);
            e.printStackTrace();
        }
        return resultVo;

    }


    @GET
    @Path("/download/{objectName:.*}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public void getResouceData(@PathParam("objectName") String objectName,@Context HttpServletResponse response) throws Exception {
        InputStream in = null;
        ServletOutputStream out = null;
        try{
            if (Strings.isNullOrEmpty(objectName)) {
                throw new Exception("参数必须值没有传入.");
            }
            response.reset();
            response.setContentType("APPLICATION/OCTET-STREAM");
            out = response.getOutputStream();
            MinioTemplate template = MinioTemplate.getInstance();
            in = template.getFile(template.getBucket(),objectName);
            byte[] tempbytes = new byte[1024];
            int byteread = 0;
            while ((byteread = in.read(tempbytes)) != -1) {
                out.write(tempbytes, 0, byteread);
            }
            String fileName=response.encodeURL(new String(FileFormatUtil.getFileNameFromPath(objectName).getBytes(),"UTF-8"));//转码
            response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
            out.close();
        }catch (Exception e){
            throw  e;
        }finally {
            if (in != null) {
                in.close();
            }
            if(out != null) {
                out.close();
            }
        }
    }


}
