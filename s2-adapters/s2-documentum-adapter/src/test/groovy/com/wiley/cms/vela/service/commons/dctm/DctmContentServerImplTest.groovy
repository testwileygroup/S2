package com.wiley.cms.vela.service.commons.dctm;

import static org.fest.assertions.Assertions.*
import static org.mockito.Mockito.*

import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import by.hzv.s2.model.SimpleContentStream

import com.documentum.fc.client.DfIdNotFoundException
import com.documentum.fc.client.IDfDocument
import com.documentum.fc.client.IDfFolder
import com.documentum.fc.common.DfId



@ActiveProfiles('s2-documentum-adapter')
@ContextConfiguration("/config/spring/dctm-plugin-context.xml")
class DctmContentServerImplTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private DctmContentServer sut
    @Autowired
    private DfcSessionFactory sessionFactory
    private ClassPathResource testDoc = new ClassPathResource("dctmContentServerTest.txt")


    @BeforeMethod
    void init() {
        sessionFactory.openSession(false)
    }

    @AfterMethod
    void cleanup() {
        sessionFactory.close(sessionFactory.currentSession)
    }


    @Test(enabled = false)
    public void deleteFolder() {
        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void shouldRetrieveFolderById() {
        // Given
        def folder = sessionFactory.currentSession.getObjectByPath('/Temp')
        def folderId = folder.objectId.id
        // When
        def result = sut.getFolder(folderId)
        // Then
        assertThat result isNotNull()
        assertThat result isInstanceOf IDfFolder
    }

    @Test
    public void shouldImportDocument() {
        def docid = sut.importDoc("/Temp/dctmContentServerTest.txt", "dm_document",
            new SimpleContentStream(testDoc.inputStream))
        assertThat sessionFactory.currentSession.getObject(new DfId(docid)) isNotNull()
    }

    @Test(dependsOnMethods = ['shouldImportDocument'])
    public void shouldReturnDocByid() {
        // Given
        def doc = sessionFactory.currentSession.getObjectByPath("/Temp/${testDoc.filename}")
        def docid = doc.objectId.id
        // When
        def result = sut.getDocById(docid)
        // Then
        assertThat result isNotNull()
        assertThat result isEqualTo doc
    }

    @Test(dependsOnMethods = ['shouldImportDocument'])
    public void shouldReturnDocByPath() {
        // When
        IDfDocument result = sut.getDocByPath("/Temp/${testDoc.filename}")
        // Then
        assertThat result isNotNull()
        def content = IOUtils.toByteArray(result.content)
        assertThat content isEqualTo testDoc.file.getBytes()
    }

    @Test(dependsOnMethods = ['shouldImportDocument'])
    public void shouldReturDocContent() {
        // Given
        def docid = getTestDocId()
        // When
        def result = sut.getDocContent(docid)
        // Then
        def content = IOUtils.toByteArray(result)
        assertThat content isNotNull()
        assertThat content isEqualTo testDoc.file.getBytes()
    }

    @Test(dependsOnMethods = ['shouldImportDocument'])
    public void shouldReturnDocContentByPath() {
        // When
        def result = sut.getDocContentByPath("/Temp/${testDoc.filename}")
        // Then
        def content = IOUtils.toByteArray(result)
        assertThat content isNotNull()
        assertThat content isEqualTo testDoc.file.getBytes()
    }

    @Test(dependsOnMethods = ['shouldImportDocument'])
    public void getDocsUnderPath() {
        // When
        def result = sut.getDocsUnderPath("/Temp")
        // Then
        assertThat result.size() isGreaterThan 1
        println "Nuber of retrieve documents: ${result.size()}"
    }


    @Test(dependsOnMethods = ['shouldImportDocument'], priority = Integer.MAX_VALUE,
        expectedExceptions = [DfIdNotFoundException])
    public void shouldDeleteDoc() {
        // Given
        def docid = getTestDocId()
        // When
        sut.deleteDoc(docid)
        // Then
        def result = sessionFactory.currentSession.getObject(new DfId(docid))
    }

    String getTestDocId() {
        def doc = sessionFactory.currentSession.getObjectByPath("/Temp/${testDoc.filename}")
        doc.objectId.id
    }
}
