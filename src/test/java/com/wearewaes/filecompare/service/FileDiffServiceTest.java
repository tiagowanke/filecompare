package com.wearewaes.filecompare.service;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.test.context.junit4.SpringRunner;

import com.wearewaes.filecompare.model.FileDiff;

@RunWith(SpringRunner.class)
public class FileDiffServiceTest {

    private FileDiffService fileCompareService;

    @Mock
    private Map<Long, FileDiff> filesDiffMock;

    @Mock
    private FileDiff fileDiffMock;


    @Before
    public void setUp() {
        this.fileCompareService = new FileDiffService();
    }

    /********************
     * test fileCompare(Long)
     *******************/
    private static Method fileCompareAccessible() throws NoSuchMethodException, SecurityException {
        final Method method = FileDiffService.class.getDeclaredMethod("createAndReturnFileDiff", Long.class);
        method.setAccessible(true);
        return method;
    }

    @Test
    public void fileCompareMustCreateNewObject() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {

        final Method createAndReturnFileDiffMethod = FileDiffServiceTest.fileCompareAccessible();
        final Field filesCompareField = this.fileCompareService.getClass().getDeclaredField("filesDiff");
        filesCompareField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final Map<Long, FileDiff> filesCompare = (Map<Long, FileDiff>) filesCompareField.get(this.fileCompareService);

        // ask for a FileCompare while the base64files are empty
        assertTrue(filesCompare.isEmpty());
        assertThat(createAndReturnFileDiffMethod.invoke(this.fileCompareService, 1l), instanceOf(FileDiff.class));
        assertTrue(filesCompare.isEmpty());

        // ask for a FileCompare that is not on the list yet
        final FileDiff myFileCompare = new FileDiff();
        filesCompare.put(1L, myFileCompare);
        final FileDiff actualFileCompare = (FileDiff) createAndReturnFileDiffMethod.invoke(fileCompareService, 2L);
        assertThat(actualFileCompare, instanceOf(FileDiff.class));
        assertThat(actualFileCompare, is(not(sameInstance(myFileCompare))));
    }

    @Test
    public void fileCompareMustReturnExistingObject() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {

        final Method method = FileDiffServiceTest.fileCompareAccessible();
        final Field filesCompareField = this.fileCompareService.getClass().getDeclaredField("filesDiff");
        filesCompareField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final Map<Long, FileDiff> filesCompare = (Map<Long, FileDiff>) filesCompareField.get(this.fileCompareService);

        final FileDiff myFileDiff = new FileDiff();
        filesCompare.put(1L, myFileDiff);
        final FileDiff actualFileDiff = (FileDiff) method.invoke(this.fileCompareService, 1L);
        assertThat(actualFileDiff, sameInstance(myFileDiff));
    }

    /********************
     * test addLeft(Long id, byte[])
     *******************/
    @Test
    public void addLeftMustDoAsExpected() throws Exception {
        final Field filesCompareField = this.fileCompareService.getClass().getDeclaredField("filesDiff");
        filesCompareField.setAccessible(true);
        filesCompareField.set(this.fileCompareService, this.filesDiffMock);

        final FileDiffService fileCompareServiceSpy = PowerMockito.spy(this.fileCompareService);
        when(fileCompareServiceSpy, method(FileDiffService.class, "createAndReturnFileDiff", Long.class))
        .withArguments(anyLong())
        .thenReturn(this.fileDiffMock);

        this.fileCompareService.addLeft(1L, null);
        verify(this.fileDiffMock, times(1)).setLeft(null);
        verify(this.filesDiffMock, times(1)).put(1L, this.fileDiffMock);
    }

    /********************
     * test addRight(Long id, byte[])
     *******************/
    @Test
    public void addRightMustDoAsExpected() throws Exception {
        final Field filesCompareField = this.fileCompareService.getClass().getDeclaredField("filesDiff");
        filesCompareField.setAccessible(true);
        filesCompareField.set(this.fileCompareService, this.filesDiffMock);

        final FileDiffService fileCompareServiceSpy = PowerMockito.spy(this.fileCompareService);
        when(fileCompareServiceSpy, method(FileDiffService.class, "createAndReturnFileDiff", Long.class)).withArguments(anyLong()).thenReturn(this.fileDiffMock);

        this.fileCompareService.addRight(1L, null);
        verify(this.fileDiffMock, times(1)).setRight(null);
        verify(this.filesDiffMock, times(1)).put(1L, this.fileDiffMock);
    }

}