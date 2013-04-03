package by.hzv.s2.client.rest;

import static org.fest.assertions.Assertions.*
import static org.mockito.Mockito.mock

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

public class S2StreamResourceTest {
	private static final String ANY_FILENAME = "filename.any"
	private static final String DUMMY_CONTENT = 'any_bytes'
	private static final InputStream ANY_INPUTSTREAM = new ByteArrayInputStream(DUMMY_CONTENT.bytes)
	private final ClassPathResource dummyResource = new ClassPathResource('dummy_content.txt')

	private S2StreamResource sut
	

	@BeforeMethod
	public void setup() throws Exception {
		
	}
	
	@Test(expectedExceptions = [NullPointerException], 
		  expectedExceptionsMessageRegExp = 'Filename should be provided')
	void filenameShouldBeProvided() {
		// When 
		new S2StreamResource(ANY_INPUTSTREAM, null)	
	}
	
	@Test(expectedExceptions = [NullPointerException], 
		  expectedExceptionsMessageRegExp = 'Input stream should be provided')
	void inputStreamShouldBeProvided() {
		// When
		new S2StreamResource(null, ANY_FILENAME)
	}
	
	@Test
	void shouldReturnFilename() {
		// When
		sut = new S2StreamResource(ANY_INPUTSTREAM, ANY_FILENAME)	
		// Then
		assertThat sut.getFilename() isNotNull() /* and */ isEqualTo ANY_FILENAME
	}
	
	@Test
	void shouldReturnContentLength() {
		// When
		sut = new S2StreamResource(ANY_INPUTSTREAM, ANY_FILENAME)
		// Then
		assertThat sut.contentLength() isNotNull() /* and */ isEqualTo DUMMY_CONTENT.bytes.size()
	}
}
