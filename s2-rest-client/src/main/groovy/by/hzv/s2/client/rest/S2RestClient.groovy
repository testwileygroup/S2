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
import by.hzv.s2.model.SimpleFileInfo
import by.hzv.s2.service.S2

import com.google.common.base.Optional

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
        s2url = s2url.endsWith(SLASH) ? s2url[0..-2] : s2url;
    }

    @Override
    String createFile(String filePath, ContentStream content, Map<String, ?> properties) {
        Validate.notNull(content.getStream(), "Content stream with null stream is not allowed")

        try {
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>()
            parts.setAll([
                "filePath": filePath,
                "content" : new S2StreamResource(content.getStream(), FilenameUtils.getName(filePath)),
                "properties" : properties
            ])

            return restTemplate.postForObject(s2url + SLASH, parts, String);
        } catch (IOException e) {
            throw new RuntimeException()
        }
    }

    @Override
    void deleteFile(String fid) {
        restTemplate.delete(s2url + "/{fid}", fid)
    }

    @Override
    void deleteFileByPath(String filePath) {
        restTemplate.delete(s2url + "/urn/{path}", filePath)
    }

    @Override
    void deleteFolder(String fid) {
        restTemplate.delete(s2url + "/folders/{fid}", fid)
    }

    @Override
    Collection<FileInfo> listFiles(String fid) {
        restTemplate.getForObject(s2url + "/metadata/folders/{fid}/files", SimpleFileInfo[], fid)
    }

    @Override
    Optional<ContentStream> getContentStream(String fid) {
        Resource response = restTemplate.getForObject(s2url + "/{fid}", Resource, fid)
        InputStream is = response.getInputStream()

        try {
            return is == null ? Optional.absent() : Optional.of(new SimpleContentStream())
        } catch (IOException e) {
            throw new RuntimeException()
        }
    }

    @Override
    Optional<ContentStream> getContentStreamByPath(String filePath) {
        Resource response = restTemplate.getForObject(s2url + "/urn/{path}", Resource, filePath)

        try {
            return new SimpleContentStream(response.getInputStream())
        } catch (IOException e) {
            throw new RuntimeException()
        }
    }

    @Override
    String getPath(String fid) {
        return restTemplate.getForObject(s2url + "/keys/{fid}", String, fid)
    }

    @Override
    String getFid(String path) {
        return restTemplate.getForObject(s2url + "/keys/urn/{path}", String, path)
    }

    @Override
    Object rpc(String opName, Map<String, ?> parameters) {
        return restTemplate.postForObject(s2url + "/rpc/{fid}", parameters, String, opName)
    }
}
