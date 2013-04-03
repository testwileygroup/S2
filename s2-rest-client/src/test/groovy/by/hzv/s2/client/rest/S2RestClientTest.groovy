package by.hzv.s2.client.rest;

import static org.fest.assertions.Assertions.*
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*
import static org.springframework.test.web.client.response.MockRestResponseCreators.*

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import by.hzv.s2.model.ContentStream
import by.hzv.s2.model.SimpleContentStream
import by.hzv.s2.model.SimpleFileInfo

import com.fasterxml.jackson.databind.ObjectMapper


class S2RestClientTest {
    private static final String ANY_PATH = '/any/path'
    private static final String ANY_FILE_PATH = '/any/file/path'
    private static final String ANY_FOLDER_PATH = '/any/folder/path'
    private static final String BASE_URL = 'http://dummy/url'
    private static final String ANY_FID = 'any_fid'
    private static final String ANY_OP_NAME = 'any_operation_name'
    private static final byte[] ANY_CONTENT = 'sdfkjksladkjsdjklra'.bytes

    private RestTemplate restTemplate = new RestTemplate()
    private MockRestServiceServer mockServer
    private InputStream stream;

    private S2RestClient sut = new S2RestClient()


    @BeforeMethod
    void setup() {
        stream = new ByteArrayInputStream([] as byte[])
        mockServer = MockRestServiceServer.createServer(restTemplate)
        sut.restTemplate = restTemplate
        sut.s2url = BASE_URL
        sut.init()
    }

    @Test
    public void shouldNormalizeBaseUrlOnInit() {
        // Given
        def urlWithoutEndingSlash = "/url"
        def urlWithEndingSlash = "/url/"
        sut.s2url = urlWithEndingSlash
        // When
        sut.init()
        // Then
        assertThat sut.s2url isEqualTo urlWithoutEndingSlash
    }

    @Test
    public void shouldCreateFile() {
        // Given
        mockServer.expect(requestTo("${BASE_URL}/"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess(ANY_FID, MediaType.TEXT_PLAIN));
        ContentStream contentStream = new SimpleContentStream(stream)
        // When
        String result = sut.createFile(ANY_FILE_PATH, contentStream, [:])
        // Then
        mockServer.verify()
        assertThat result isNotNull() /*and*/ isEqualTo(ANY_FID)
    }

    @Test
    public void shouldDeleteFileByFid() {
        // Given
        mockServer.expect(requestTo("${BASE_URL}/${ANY_FID}"))
        .andExpect(method(HttpMethod.DELETE))
        .andRespond(withSuccess());
        ContentStream contentStream = new SimpleContentStream(stream)
        // When
        sut.deleteFile(ANY_FID)
        // Then
        mockServer.verify()
    }


    @Test
    public void shouldDeleteFileByPath() {
        // Given
        mockServer.expect(requestTo("${BASE_URL}/urn/${ANY_FILE_PATH}"))
        .andExpect(method(HttpMethod.DELETE))
        .andRespond(withSuccess());
        ContentStream contentStream = new SimpleContentStream(stream)
        // When
        sut.deleteFileByPath(ANY_FILE_PATH)
        // Then
        mockServer.verify()
    }

    @Test
    public void shouldDeleteFolder() {
        // Given
        mockServer.expect(requestTo("${BASE_URL}/folders/${ANY_FID}"))
        .andExpect(method(HttpMethod.DELETE))
        .andRespond(withSuccess());
        ContentStream contentStream = new SimpleContentStream(stream)
        // When
        sut.deleteFolder(ANY_FID)
        // Then
        mockServer.verify()
    }

    @Test
    public void shouldReturnContentByFid() {
        // Given
        mockServer.expect(requestTo("${BASE_URL}/${ANY_FID}"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(ANY_CONTENT, MediaType.APPLICATION_OCTET_STREAM));
        ContentStream contentStream = new SimpleContentStream(stream)
        // When
        sut.getContentStream(ANY_FID)
        // Then
        mockServer.verify()
    }

    @Test
    public void getContentStreamByPath() {
        // Given
        mockServer.expect(requestTo("${BASE_URL}/urn/${ANY_FILE_PATH}"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(ANY_CONTENT, MediaType.APPLICATION_OCTET_STREAM));
        ContentStream contentStream = new SimpleContentStream(stream)
        // When
        sut.getContentStreamByPath(ANY_FILE_PATH)
        // Then
        mockServer.verify()
    }

    @Test
    public void getFid() {
        // Given
        mockServer.expect(requestTo("${BASE_URL}/keys/urn/${ANY_PATH}"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(ANY_FID, MediaType.TEXT_PLAIN));
        // When
        String path = sut.getFid(ANY_PATH)
        // Then
        mockServer.verify()
        assertThat path isEqualTo ANY_FID
    }

    @Test
    public void getPath() {
        // Given
        mockServer.expect(requestTo("${BASE_URL}/keys/${ANY_FID}"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(ANY_PATH, MediaType.TEXT_PLAIN));
        // When
        String fid = sut.getPath(ANY_FID)
        // Then
        mockServer.verify()
        assertThat fid isEqualTo ANY_PATH
    }

    @DataProvider
    Object[][] jsonResponseProvider() {
        [
            ['{ "firstName":"John" , "lastName":"Doe" }'],
            ['String'],
            ['']
        ]
    }

    @Test(dataProvider = 'jsonResponseProvider')
    public void rpc(String jsonResponse) {
        // Given
        mockServer.expect(requestTo("${BASE_URL}/rpc/${ANY_OP_NAME}"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));
        // When
        Object result = sut.rpc(ANY_OP_NAME, [:])
        // Then
        mockServer.verify()
        assertThat result isNotNull() /* and */ isEqualTo  jsonResponse
    }

    @Test
    public void shouldReturnFileInfoList() {
        // Given
        def f1 = new SimpleFileInfo("flid1", "filename1", "extension1", 1)
        def f2 = new SimpleFileInfo("flid2", "filename2", "extension2", 2)
        def f3 = new SimpleFileInfo("flid3", "filename3", "extension3", 3)

        mockServer.expect(requestTo("${BASE_URL}/metadata/folders/${ANY_FID}/files"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(new ObjectMapper().writeValueAsBytes([f1, f2, f3]), MediaType.APPLICATION_JSON))
        // When
        def result = sut.listFiles(ANY_FID)
        // Then
        mockServer.verify()

        //TODO uncomment whe hashcode and equals will be implemented for SimpleFileInfo
        //assertThat result containsOnly f1, f2, f3
    }
}
