package by.hzv.storage;

import static org.fest.assertions.Assertions.*
import static org.mockito.Mockito.*

import java.util.concurrent.locks.Lock;

import org.apache.commons.io.FilenameUtils
import org.mockito.InOrder
import org.mockito.Mockito
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import by.hzv.storage.util.KeyBasedLocker

class SimpleStorageTest {
    private static final String ANY_FOLDER_ID = 'any_id'
    private static final String ANY_FOLDER_PATH = '/any/fodler/path'
    private static final String UNNORMALIZED_FOLDER_PATH = '\\any/fodler\\path'
    private static final String ANY_FILE_PATH = '/any/file/path'
    private static final String UNNORMALIZED_FILE_PATH = '/any\\file/path'
    private static final String FILE_PARENT_PATH = FilenameUtils.getFullPath('/any/file/path')

    private PathInfoController pathInfoController
    private DataStoreController dataStoreController
    private ContentStream contentStream
    private FolderInfo finfo
    private FileInfo fileinfo
    private KeyBasedLocker locker
    private SimpleStorage sut


    @BeforeMethod
    void setup() {
        /* pathInfoController fixture  */
        pathInfoController = Mockito.mock(PathInfoController)
        finfo = Mockito.mock(FolderInfo)
        when pathInfoController.getOrCreateFolderInfo(any()) thenReturn finfo
        fileinfo = Mockito.mock(FileInfo)
        when pathInfoController.createFileInfo(any(), any(), any()) thenReturn fileinfo

        /* dataStoreController fixture  */
        dataStoreController = Mockito.mock(DataStoreController)

        /* KeyBasedLocker fixture  */
        locker = Mockito.mock(KeyBasedLocker)
        when locker.getLock(any()) thenReturn Mockito.mock(Lock)

        contentStream = Mockito.mock(ContentStream)

        /* SUT fixture  */
        sut = new SimpleStorage()
        sut.pathInfoController = pathInfoController
        sut.dataStoreController = dataStoreController
        sut.locker = locker
    }


    @Test
    void shouldDeleteFoderInfoWhenDeleteFolder() {
        // When
        sut.deleteFolder(ANY_FOLDER_ID)
        // Then
        Mockito.verify(pathInfoController).deleteFolderInfo(ANY_FOLDER_ID)
    }

    @Test
    void shouldDeleteFileInfoWhenDeleteFile() {
        // When
        sut.deleteFile(ANY_FILE_PATH)
        // Then
        Mockito.verify(pathInfoController).deleteFileInfo(ANY_FILE_PATH)
    }

    @Test
    void shouldProperlyManageFileCreation() {
        // When
        sut.createFile(ANY_FILE_PATH, contentStream, [:])

        // Then
        //create inOrder object passing any mocks that need to be verified in order
        InOrder inOrder = inOrder(pathInfoController, dataStoreController);
        inOrder.verify(pathInfoController).getOrCreateFolderInfo(FILE_PARENT_PATH)
        inOrder.verify(pathInfoController).deleteFileInfo(ANY_FILE_PATH)
        inOrder.verify(pathInfoController).createFileInfo(ANY_FILE_PATH, finfo.getFid(), [:])
        inOrder.verify(dataStoreController).storeContentStream(fileinfo.getFlid(), contentStream)
    }

    @Test
    void shouldReturnContentStreamByFilePath() {
        // When
        sut.getContentStream(ANY_FILE_PATH)
        // Then
        Mockito.verify(dataStoreController).getContentStream(ANY_FILE_PATH)
    }

    @Test
    void shouldInvokeListFileInfoWhenListFiles() {
        // When
        sut.listFiles(ANY_FOLDER_ID)
        // Then
        Mockito.verify(pathInfoController).listFileInfo(ANY_FOLDER_ID)
    }

    @Test
    void shouldInvokeAskForFidWhenGetFid() {
        // When
        sut.getFid(ANY_FOLDER_PATH)
        // Then
        Mockito.verify(pathInfoController).getFid(ANY_FOLDER_PATH)
    }

    @Test
    void shouldInvokeAskForFolderPathWhenGetFolderPath() {
        // When
        sut.getFolderPath(ANY_FOLDER_ID)

        // Then
        Mockito.verify(pathInfoController).getFolderPath(ANY_FOLDER_ID)
    }

    @Test
    void shouldNormalizePathBeforeFurtherProcessing() {
        /* scenario 1 */
        // Given
        SimpleStorage sutSpy = Mockito.spy(sut)
        // When
        sutSpy.createFile(UNNORMALIZED_FILE_PATH, contentStream, [:])
        // Then
        verify(sutSpy).normalizePath(UNNORMALIZED_FILE_PATH)
        Mockito.reset(sutSpy)

        /* scenario 2 */
        // When
        sutSpy.deleteFile(UNNORMALIZED_FILE_PATH)
        // Then
        verify(sutSpy).normalizePath(UNNORMALIZED_FILE_PATH)
        Mockito.reset(sutSpy)

        /* scenario 3 */
        // When
        sutSpy.getFid(UNNORMALIZED_FOLDER_PATH)
        // Then
        verify(sutSpy).normalizePath(UNNORMALIZED_FOLDER_PATH)
        Mockito.reset(sutSpy)

        /* scenario 4 */
        // When
        sutSpy.getContentStream(UNNORMALIZED_FILE_PATH)
        // Then
        verify(sutSpy).normalizePath(UNNORMALIZED_FILE_PATH)
        Mockito.reset(sutSpy)
    }

    @DataProvider
    Object[][] unnormilizedPathProveder() {
      [
        ['\\any/fodler\\path', '/any/fodler/path'],
        ['/any\\file/path/',  '/any/file/path/'],
        ['any/path\\', 'any/path/']
      ]
    }

    @Test(dataProvider = 'unnormilizedPathProveder')
    void normalizePathShouldWorkProperly(String unnormalizedPath, String expectedPath) {
        def normalizedPath = sut.normalizePath(unnormalizedPath)
        assertThat(normalizedPath) isEqualTo expectedPath
    }
}
