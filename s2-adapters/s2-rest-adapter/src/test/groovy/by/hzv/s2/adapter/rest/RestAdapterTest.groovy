package by.hzv.s2.adapter.rest

import by.hzv.s2.model.ContentStream
import by.hzv.s2.model.FileInfo
import by.hzv.s2.model.SimpleContentStream
import by.hzv.s2.model.SimpleFileInfo
import by.hzv.s2.service.S2
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Optional
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
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import static org.fest.assertions.Assertions.assertThat
import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.*
import static org.mockito.Mockito.reset
import static org.mockito.Mockito.verify
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

/**
 * @author <a href="mailto:mityan@wiley.com">Mikhail Tyan</a>
 * @since 01.04.13
 */
@ActiveProfiles("integration")
@WebAppConfiguration
@ContextConfiguration(["/integration-tests-profile.xml", "/config/mvc/rest-plugin-context.xml"])
class RestAdapterTest extends AbstractTestNGSpringContextTests {
    private final String fid = "999"
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

    @BeforeMethod
    void resetS2() {
        reset(s2)
    }

    @DataProvider
    Object[][] pathDataProvider() {
        return [
                ['/anyfolder/anyfile.xml'],
                ['/anyfolder/anotherfolder/anyfile.gif'],
                ['/anyfile.pdf'],
                ['/anyfile.png'],
                ['/anyfile.pdf']
        ]
    }

    @DataProvider
    Object[][] getFileInfos() {
        FileInfo fileInfo = new SimpleFileInfo("anyid", "anyfilename", "any.ext", new BigInteger(1))
        FileInfo anotherFileInfo = new SimpleFileInfo("anotherjid", "anotherfilename", "another.ext", new BigInteger(1))
        return [
                [[]],
                [[fileInfo]],
                [[fileInfo, anotherFileInfo]]
        ]
    }

    @Test
    void shouldRedirectWhenContentStreamProxyReturnedByFid() throws Exception {
        //given
        ContentStream proxy = new SimpleContentStream(url)
        given s2.getContentStream(fid) willReturn Optional.of(proxy)

        //when
        def result = mockMvc.perform(get("/{fid}", fid))

        //then
        result.andExpect status().isMovedTemporarily() // should redirect to URL with content

        def response = result.andReturn().response
        assertThat response.contentLength isEqualTo 0
        assertThat response.getHeader("Location") isEqualTo url
    }

    @Test
    void shouldReturnContentByFid() throws Exception {
        //given
        byte[] expectedContent = [1, 2, 3]
        given s2.getContentStream(fid) willReturn(
                Optional.of(new SimpleContentStream(new ByteArrayInputStream(expectedContent))))

        //when
        def result = mockMvc.perform(get("/{fid}", fid))

        //then
        result.andExpect status().isOk()
        def responseAsByteArray = result.andReturn().response.contentAsByteArray
        assertThat responseAsByteArray isEqualTo expectedContent
    }

    @Test(dataProvider = 'pathDataProvider')
    void shouldReturnContentByPath(String anyPath) throws Exception {
        //given
        byte[] expectedContent = [1, 2, 3]
        given s2.getContentStreamByPath(anyPath) willReturn(
                Optional.of(new SimpleContentStream(new ByteArrayInputStream(expectedContent))))

        //when
        def result = mockMvc.perform(get("/urn/{path}", anyPath))

        //then
        result.andExpect(status().isOk())

        def response = result.andReturn().response
        assertThat response.contentAsByteArray isEqualTo expectedContent
    }

    @Test(dataProvider = 'pathDataProvider')
    public void shouldRedirectWhenContentStreamProxyReturnedByPath(String anyPath) throws Exception {
        //given
        ContentStream proxy = new SimpleContentStream(url)
        given s2.getContentStreamByPath(anyPath) willReturn Optional.of(proxy)

        //when
        def result = mockMvc.perform(get("/urn/{path}", anyPath))

        //then
        result.andExpect status().isMovedTemporarily() // should redirect to URL with content

        def response = result.andReturn().response
        assertThat response.contentLength isEqualTo 0
        assertThat response.getHeader("Location") isEqualTo url
    }


    @Test(dataProvider = "getFileInfos")
    public void shouldReturnListOfFilesMetadata(List<FileInfo> fileInfos) throws Exception {
        //given
        given s2.listFiles(fid) willReturn(fileInfos)

        //when
        def result = mockMvc.perform(get("/metadata/folders/{fid}/files/", fid))

        //then
        result.andExpect status().isOk()

        def json = result.andReturn().getResponse().contentAsString
        Collection<FileInfo> resultFileInfos = new ObjectMapper().readValue(json, SimpleFileInfo[])
        assertThat resultFileInfos isEqualTo fileInfos
    }

    @Test
    public void shouldDeleteFileByFId() throws Exception {
        //when
        def result = mockMvc.perform(delete("/{fid}", fid))

        //then
        result.andExpect status().isOk()
        verify(s2).deleteFile(fid)
    }

    @Test(dataProvider = 'pathDataProvider')
    public void shouldDeleteFileByPath(String anyPath) throws Exception {
        //when
        def result = mockMvc.perform(delete("/urn/{path}", anyPath))

        //then
        result.andExpect status().isOk()
        verify(s2).deleteFileByPath(anyPath)

    }

    @Test
    public void shouldDeleteFolderByFId() throws Exception {
        //when
        def result = mockMvc.perform(delete("/folders/{fid}", fid))
        //then
        result.andExpect status().isOk()
        verify(s2).deleteFolder(fid)

    }

    @Test(dataProvider = 'pathDataProvider')
    public void shouldReturnFidForPath(String anyPath) throws Exception {
        //given
        given s2.getFid(anyPath) willReturn fid

        //when
        def result = mockMvc.perform(get("/keys/urn/{path}", anyPath));

        //then
        result.andExpect status().isOk()

        def retrievedFid = result.andReturn().response.contentAsString
        assertThat retrievedFid isEqualTo fid
    }

    @Test(dataProvider = 'pathDataProvider')
    public void shouldReturnPathForFid(String anyPath) throws Exception {
        //given
        given s2.getPath(fid) willReturn anyPath

        //when
        def result = mockMvc.perform(get("/keys/${fid}"))

        //then
        result.andExpect status().isOk()

        def retrievedPath = result.andReturn().response.contentAsString
        assertThat retrievedPath isEqualTo anyPath
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
        long id = 1L
        String name = "rpcObject"
        boolean condition = true

        Object rpcObj = new TestObject(id: 1, name: name, condition: condition)

        given s2.rpc(anyString(), anyMap()) willReturn rpcObj;

        //when
        def result = mockMvc.perform(post("/rpc/${operationName}")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"${key}\":\"${value}\"}".bytes))

        //then
        result.andExpect status().isOk()
        verifyRpcArguments(operationName, key, value)

        def json = result.andReturn().getResponse().contentAsString
        Object answer = new ObjectMapper().readValue(json, TestObject.class)
        assertThat answer isEqualTo rpcObj
    }

    private void verifyRpcArguments(String operationName, String key, String value) {
        ArgumentCaptor mapCaptor = ArgumentCaptor.forClass(Map.class)
        verify(s2).rpc(eq(operationName), mapCaptor.capture())
        assertThat mapCaptor.getValue().get(key) isEqualTo value
    }
}