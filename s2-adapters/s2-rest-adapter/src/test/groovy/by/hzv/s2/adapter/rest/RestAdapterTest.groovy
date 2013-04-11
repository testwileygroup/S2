package by.hzv.s2.adapter.rest

import static org.fest.assertions.Assertions.assertThat
import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.*
import static org.mockito.Mockito.verify
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

import org.mockito.ArgumentCaptor
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.context.WebApplicationContext
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import by.hzv.s2.model.ContentStream
import by.hzv.s2.model.FileInfo
import by.hzv.s2.model.SimpleContentStream
import by.hzv.s2.model.SimpleFileInfo
import by.hzv.s2.service.S2

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper
/**
 * @author <a href="mailto:mityan@wiley.com">Mikhail Tyan</a>
 * @since 01.04.13
 */

@ActiveProfiles("integration")
@WebAppConfiguration
@ContextConfiguration("file:../**/rest-plugin-context.xml")
public class RestAdapterTest extends AbstractTestNGSpringContextTests {
    private final String fid = "999"
    private final String path = "/any/path/to/file.ext"
    private final String url = "http://any.url.to.file"

    @Autowired
    WebApplicationContext waq;
    private MockMvc mockMvc;

    @Autowired
    private S2 s2;


    @BeforeClass
    void setupWebAppContext() {
        MockitoAnnotations.initMocks(this);
        mockMvc = webAppContextSetup(waq).build();
    }

    @Test
    public void shouldRedirectWhenContentStreamProxyReturnedByFid() throws Exception {
        //given
        ContentStream proxy = new SimpleContentStream(url)
        given s2.getContentStream(fid) willReturn proxy

        //when
        def result = mockMvc.perform(get("/{fid}", fid))

        //then
        result.andExpect status().isMovedTemporarily() // should redirect to URL with content

        def response = result.andReturn().response
        assertThat response.contentLength isEqualTo 0
        assertThat response.getHeader("Location") isEqualTo url
    }

    @Test
    public void shouldReturnContentByFid() throws Exception {
        //given
        byte[] expectedContent = [1, 2, 3]
        given s2.getContentStream(fid) willReturn new SimpleContentStream(new ByteArrayInputStream(expectedContent))

        //when
        def result = mockMvc.perform(get("/{fid}", fid))

        //then
        result.andExpect status().isOk()
        def responseAsByteArray = result.andReturn().response.contentAsByteArray
        assertThat responseAsByteArray isEqualTo expectedContent
    }

    @Test
    public void shouldReturnContentByPath() throws Exception {
        //given
        byte[] expectedContent = [1, 2, 3]
        given s2.getContentStreamByPath(path) willReturn new SimpleContentStream(new ByteArrayInputStream(expectedContent))

        //when
        def result = mockMvc.perform(get("/urn/{path}", path))

        //then
        result.andExpect(status().isOk())

        def response = result.andReturn().response
        assertThat response.contentAsByteArray isEqualTo expectedContent
    }

    @Test
    public void shouldRedirectWhenContentStreamProxyReturnedByPath() throws Exception {
        //given
        ContentStream proxy = new SimpleContentStream(url)
        given s2.getContentStreamByPath(path) willReturn proxy

        //when
        def result = mockMvc.perform(get("/urn/{path}", path))

        //then
        result.andExpect status().isMovedTemporarily() // should redirect to URL with content

        def response = result.andReturn().response
        assertThat response.contentLength isEqualTo 0
        assertThat response.getHeader("Location") isEqualTo url
    }


    @Test //TODO enhance test to use DataProvider with collection of 0, 1, several FileInfo
    public void shouldReturnListOfFilesMetadata() throws Exception {
        //given
        FileInfo fileInfo = new SimpleFileInfo("anyid", "anyfilename", "any.ext", new BigInteger(1))
        given s2.listFiles(fid) willReturn([fileInfo])

        //when
        def result = mockMvc.perform(get("/metadata/folders/{fid}/files/", fid))

        //then
        result.andExpect status().isOk()

        def json = result.andReturn().getResponse().contentAsString
        Collection<FileInfo> fileInfos = new ObjectMapper().readValue(json, SimpleFileInfo[])
        assertThat fileInfos[0] isEqualTo fileInfo
    }

    @Test
    public void shouldDeleteFileByFId() throws Exception {
        //when
        def result = mockMvc.perform(delete("/{fid}", fid))

        //then
        result.andExpect status().isOk()
        verify(s2).deleteFile(fid)
    }

    @Test
    public void shouldDeleteFileByPath() throws Exception {
        //when
        def result = mockMvc.perform(delete("/urn/{path}", path))

        //then
        result.andExpect status().isOk()
        verify(s2).deleteFileByPath(path)

    }

    @Test
    public void shouldDeleteFolderByFId() throws Exception {
        //when
        def result = mockMvc.perform(delete("/folders/{fid}", fid))
        //then
        result.andExpect status().isOk()
        verify(s2).deleteFolder(fid)

    }

    @Test
    public void shouldReturnFidForPath() throws Exception {
        //given
        given s2.getFid(path) willReturn fid

        //when
        def result = mockMvc.perform(get("/keys/urn/{path}", path));

        //then
        result.andExpect status().isOk()

        def retrievedFid = result.andReturn().response.contentAsString
        assertThat retrievedFid isEqualTo fid
    }

    @Test
    public void shouldReturnPathForFid() throws Exception {
        //given
        given s2.getPath(fid) willReturn path

        //when
        def result = mockMvc.perform(get("/keys/${fid}"))

        //then
        result.andExpect status().isOk()

        def retrievedPath = result.andReturn().response.contentAsString
        assertThat retrievedPath isEqualTo path
    }

    @Test
    public void shouldCreateFile() throws Exception {
        //given
        def fileUpload = fileUpload("/")
            .file('content', 'test_content'.bytes)
            .file(new MockMultipartFile('properties', '', MediaType.APPLICATION_JSON_VALUE, '{}'.bytes))
            .param('filePath', '/any/path')

        //when
        def result = mockMvc.perform(fileUpload)
        //then
        result.andExpect(status().isOk());
        verify(s2).createFile(eq('/any/path'), any(ContentStream), any(Map))
    }

    @Test
    public void shouldCallRemoteProcedure() throws Exception {
        //given
        String key = "key"
        String value = "value"
        String operationName = "operation"

        String rpcObj = new Object() //TODO provide more complex object for test. Probably some custom Class
        given s2.rpc(anyString(),anyMap()) willReturn rpcObj;

        //when
        def result = mockMvc.perform(post("/rpc/${operationName}")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"${key}\":\"${value}\"}".bytes))

        //then
        result.andExpect status().isOk()
        verifyRpcArguments(operationName, key, value)

        def json = result.andReturn().getResponse().contentAsString
        Object answer = new ObjectMapper().readValue(json, Object)
        assertThat answer isEqualTo rpcObj
    }

    private void verifyRpcArguments(String operationName, String key, String value) {
        ArgumentCaptor mapCaptor = ArgumentCaptor.forClass(Map.class)
        verify(s2).rpc(eq(operationName), mapCaptor.capture())
        assertThat mapCaptor.getValue().get(key) isEqualTo value
    }
}