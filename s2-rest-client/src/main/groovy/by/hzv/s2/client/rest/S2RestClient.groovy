package by.hzv.s2.client.rest;

import javax.annotation.PostConstruct

import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.Validate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

import by.hzv.s2.model.ContentStream
import by.hzv.s2.model.FileInfo
import by.hzv.s2.model.SimpleContentStream
import by.hzv.s2.service.S2

/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 01.04.2013
 */
@Component
class S2RestClient implements S2 {
    private static final String SLASH = "/"


    @Autowired
    private RestTemplate restTemplate
    @Value("#{s2Properties['s2.url']}")
    private String s2url

    @PostConstruct
    void init() {
        /* normalize S2 url */
        s2url = s2url.endsWith(SLASH) ? s2url : s2url + SLASH;
    }

    @Override
    public String createFile(String filePath, ContentStream content, Map<String, ?> properties) {
        Validate.notNull(content.getStream(), "Content stream with null stream is not allowed")

        try {
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>()
            parts.setAll([
                "path": filePath,
                "content" : new S2StreamResource(content.getStream(), FilenameUtils.getName(filePath)),
                "properties" : properties
            ])

            return restTemplate.postForObject(s2url, parts, String);
        } catch (IOException e) {
            throw new RuntimeException()
        }
    }

    @Override
    public void deleteFile(String fid) {
        restTemplate.delete(s2url + "{fid}", fid)
    }

    @Override
    public void deleteFileByPath(String filePath) {
        restTemplate.delete(s2url + "{path}?path=true", filePath)
    }

    @Override
    public void deleteFolder(String fid) {
        restTemplate.delete(s2url + "folders/{fid}", fid)
    }

    @Override
    public Collection<FileInfo> listFiles(String fid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ContentStream getContentStream(String fid) {
        Resource response = restTemplate.getForObject(s2url + "{fid}", Resource, fid)

        try {
            return new SimpleContentStream(response.getInputStream())
        } catch (IOException e) {
            throw new RuntimeException()
        }
    }

    @Override
    public ContentStream getContentStreamByPath(String filePath) {
        Resource response = restTemplate.getForObject(s2url + "{path}?path=true", Resource, filePath)

        try {
            return new SimpleContentStream(response.getInputStream())
        } catch (IOException e) {
            throw new RuntimeException()
        }
    }

    @Override
    public String getFid(String path) {
        return restTemplate.getForObject(s2url + "keys/{path}?path", String, path)
    }

    @Override
    public String getPath(String fid) {
        return restTemplate.getForObject(s2url + "keys/{fid}", String, fid)
    }

    @Override
    public Object rpc(String opName, Map<String, ?> parameters) {
        return restTemplate.postForObject(s2url + "rpc/{fid}", parameters, String, opName)
    }
}
