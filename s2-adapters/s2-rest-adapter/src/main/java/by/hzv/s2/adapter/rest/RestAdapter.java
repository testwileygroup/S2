package by.hzv.s2.adapter.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import by.hzv.s2.model.ContentStream;
import by.hzv.s2.model.FileInfo;
import by.hzv.s2.model.SimpleContentStream;
import by.hzv.s2.service.S2;

import com.google.common.base.Optional;


/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 01.04.2013
 */
@Controller
public class RestAdapter {
    private static final String SLASH = "/";
    @Autowired
    private S2 s2;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public String createFile(@RequestParam("filePath") String filePath,
                             @RequestPart("content") MultipartFile content,
                             @RequestPart("properties") Map<String, ?> properties) throws IOException {
        return s2.createFile(filePath, new SimpleContentStream(content.getInputStream()), properties);
    }

    @RequestMapping(value = "/{fid}", method = RequestMethod.GET)
    @ResponseBody
    public byte[] getContentStream(
            @PathVariable("fid") String fid,
            HttpServletResponse response) throws IOException {

        Optional<ContentStream> ocs = s2.getContentStream(fid);
        return prepareContentStream(response, ocs);
    }

    @RequestMapping(value = "/urn/**", method = RequestMethod.GET)
    @ResponseBody
    public byte[] getContentStreamByPath(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        Optional<ContentStream> ocs = s2.getContentStreamByPath(getUrn(request));
        return prepareContentStream(response, ocs);
    }

    private byte[] prepareContentStream(HttpServletResponse response, Optional<ContentStream> ocs) throws IOException {
        byte[] res = null;

        if (ocs.isPresent()) {
            ContentStream cs = ocs.get();
            if (cs.isProxy()) {
                response.sendRedirect(cs.getUrl());
            } else {
                res = IOUtils.toByteArray(cs.getStream());
            }
        }

        return res;
    }

    @RequestMapping(value = "/metadata/folders/{fid}/files", method = RequestMethod.GET)
    @ResponseBody
    public Collection<FileInfo> listFiles(@PathVariable("fid") String fid) {
        return s2.listFiles(fid);
    }

    @RequestMapping(value = "/{fid}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteFile(@PathVariable("fid") String fid) {
        s2.deleteFile(fid);
    }

    @RequestMapping(value = "/urn/**", method = RequestMethod.DELETE)
    public void deleteFileByPath(HttpServletRequest request) {
        s2.deleteFileByPath(getUrn(request));
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

    @RequestMapping(value = "/keys/urn/**", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getFid(HttpServletRequest request) {
        return s2.getFid(getUrn(request));
    }

    @RequestMapping(value = "/rpc/{opName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object rpc(@PathVariable("opName") String opName, @RequestBody Map<String, ?> params) {
        return s2.rpc(opName, params);
    }

    private String getUrn(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        String answer = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
        return answer.startsWith(SLASH) ? answer : SLASH + answer; //normalize path
    }
}