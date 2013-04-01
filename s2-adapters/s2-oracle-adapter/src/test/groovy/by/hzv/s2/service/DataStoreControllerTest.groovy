package by.hzv.s2.service;

import static org.fest.assertions.Assertions.*
import static org.mockito.Mockito.*

import org.mockito.Mockito
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import by.hzv.s2.dao.DataStoreRepository
import by.hzv.s2.model.ContentStream
import by.hzv.s2.model.entity.ContentStreamEntity



class DataStoreControllerTest {
    private static final String ANY_PATH = '/any/path'
    private static final String ANY_FLID = '1000'

    DataStoreRepository repository
    DataStoreController sut

    @BeforeMethod
    void beforeMethod() {
        repository = Mockito.mock(DataStoreRepository)
        when repository.find(any(String)) thenReturn mock(ContentStreamEntity)
        sut = new DataStoreController()
        sut.repository = repository
    }

    @Test
    public void shouldAskRepositoryToFindContentStream() {
        // When
        sut.getContentStream(ANY_PATH)
        // Then
        verify(repository).find(ANY_PATH)
    }

    @Test
    public void shouldReturnNullForNullArgument() {
        // When
        def result = sut.getContentStream(null)
        // Then
        assertThat result isNull()
    }

    @Test
    public void shouldAskToSaveContentStreamEntity() {
        //Given
        ContentStream cs = Mockito.mock(ContentStream)
        // When
        sut.storeContentStream(ANY_FLID, cs)
        // Then
        verify(repository).save(any(ContentStreamEntity))
    }
}
