package by.hzv.s2.adapter.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import by.hzv.s2.model.ContentStream;
import by.hzv.s2.model.FileInfo;
import by.hzv.s2.model.SimpleContentStream;
import by.hzv.s2.service.S2;


/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 01.04.2013
 */
@Controller
public class RestAdapter {
    @Autowired
    private S2 s2;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public String createFile(@RequestParam("filePath") String filePath,
                             @RequestPart("content") MultipartFile content,
                             @RequestPart("properties") Map<String, ?> properties) throws IOException {
        return s2.createFile(filePath, new SimpleContentStream(content.getInputStream()), properties);
    }

    @RequestMapping(value = "/{fid}", method = RequestMethod.GET, produces = "application/octet-stream")
    public @ResponseBody byte[] getContentStream(
            @PathVariable("fid") String fid,
            HttpServletResponse response) throws IOException {

        ContentStream cs = s2.getContentStream(fid);
        if (cs.isProxy()) {
            response.sendRedirect(cs.getUrl());
            return null;
        } else {
            return IOUtils.toByteArray(cs.getStream());
        }
    }

    @RequestMapping(value = "/urn/{path:.*}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getContentStreamByPath(@PathVariable String path, HttpServletResponse response)
        throws IOException {

        ContentStream cs = s2.getContentStreamByPath(decodePath(path));
        if (cs.isProxy()) {
            response.sendRedirect(cs.getUrl());
            return null;
        } else {
            return IOUtils.toByteArray(cs.getStream());
        }

    }

    @RequestMapping(value = "/metadata/folders/{fid}/files", method = RequestMethod.GET)
    @ResponseBody
    public Collection<FileInfo> listFiles(@PathVariable("fid") String fid) {
        return s2.listFiles(fid);
    }

    @RequestMapping(value = "/{fid}", method = RequestMethod.DELETE)
    public @ResponseBody void deleteFile(@PathVariable("fid") String fid) {
        s2.deleteFile(fid);
    }

    @RequestMapping(value = "/urn/{path:.+}", method = RequestMethod.DELETE)
    public void deleteFileByPath(@PathVariable("path") String path) {
        s2.deleteFileByPath(decodePath(path));
    }

    @RequestMapping(value = "/folders/{fid}", method = RequestMethod.DELETE)
    public void deleteFolder(@PathVariable("fid") String fid) {
        s2.deleteFolder(fid);
    }

    @RequestMapping(value = "/keys/{fid}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getPath(@PathVariable("fid") String fid) {
        return s2.getPath(fid);
    }

    @RequestMapping(value = "/keys/urn/{path:.+}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getFid(@PathVariable("path") String path) {
        return s2.getFid(decodePath(path));
    }

    @RequestMapping(value = "/rpc/{opName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object rpc(@PathVariable("opName") String opName, @RequestBody Map<String, ?> params) {
        return s2.rpc(opName, params);
    }

    private String decodePath(String path) {
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Cannot decode path parameter: " + path);
        }
    }
}