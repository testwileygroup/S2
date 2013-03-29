package by.hzv.s2.service;

import static org.mockito.Mockito.*

import java.util.concurrent.locks.Lock

import org.mockito.InOrder
import org.mockito.Mockito
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import by.hzv.s2.dao.PathInfoRepository
import by.hzv.s2.model.FilePropertyNames
import by.hzv.s2.model.entity.FileInfoEntity
import by.hzv.s2.model.entity.FolderInfoEntity
import by.hzv.s2.service.util.KeyBasedLocker


class PathInfoControllerTest {
    private static final String ANY_FID = 'any_fid'
    private static final String ANY_PATH = '/any/path'

    private PathInfoRepository repository
    private KeyBasedLocker locker
    private PathInfoController sut

    @BeforeMethod
    void beforeMethod() {
        repository = Mockito.mock(PathInfoRepository)

        locker = Mockito.mock(KeyBasedLocker)
        when locker.getLock(any()) thenReturn Mockito.mock(Lock)

        sut = new PathInfoController()
        sut.repository = repository
        sut.loker = locker
    }


    @Test
    void shouldSaveFileInfo() {
        // Given
        def fid = 'fid'
        def path = '/path'
        def properties = [:]
        properties[FilePropertyNames.CONTENT_SIZE.name()] = BigInteger.valueOf(1000)
        // When
        sut.createFileInfo(path, fid, properties)
        // Then
        verify(repository).save(any(FileInfoEntity))
    }

    @Test
    void shouldAskRepositoryToDeleteFolderInfo() {
        // When
        sut.deleteFolderInfo(ANY_FID)
        // Then
        verify(repository).deleteFolderInfo(ANY_FID)
    }

    @Test
    void shouldAskRepositoryToDeleteFileInfo() {
        // When
        sut.deleteFileInfoByPath(ANY_PATH)
        // Then
        verify(repository).deleteFileInfoByPath(ANY_PATH)
    }

    @Test
    void shouldAskRepositoryToFindFid() {
        // When
        sut.getFid(ANY_PATH)
        // Then
        verify(repository).findFid(ANY_PATH)
    }

    @Test
    void shouldAskRepositoryToFindPath() {
        // When
        sut.getPath(ANY_FID)
        // Then
        verify(repository).findPath(ANY_FID)
    }

    @Test
    void shouldNotSaveFolderInfoIfExists() {
        // Given
        def folderPath = '/path'
        when repository.findFolderInfo(folderPath) thenReturn mock(FolderInfoEntity)
        // When
        sut.getOrCreateFolderInfo(folderPath);
        // Then
        verify(repository, never()).save(any(FileInfoEntity))
    }

    @Test
    void shouldSaveFolderInfoIfThereIsNoOne() {
        // Given
        def folderPath = '/path'
        // When
        sut.getOrCreateFolderInfo(folderPath);
        // Then
        InOrder inOrder = inOrder(repository)
        inOrder.verify(repository, times(2)).findFolderInfo(folderPath)
        inOrder.verify(repository).save(any(FileInfoEntity))
    }

    @Test
    void shouldAskRepositoryToFindFileInfoByFid() {
        // When
        sut.listFileInfo(ANY_FID)
        // Then
        verify(repository).findFileInfo(ANY_FID)
    }
}
