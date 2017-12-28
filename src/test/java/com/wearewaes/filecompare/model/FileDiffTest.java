package com.wearewaes.filecompare.model;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Test;

import com.google.gson.JsonObject;

public class FileDiffTest {

    /**
     * @return To help writting tests for this class this method reads from resources package the given file name and return as byte[].
     */
    private static byte[] fileAsByte(String fileName) throws IOException {
        final ClassLoader classLoader = FileDiffTest.class.getClassLoader();
        final File mainFile = new File(classLoader.getResource(fileName).getFile());
        return Files.readAllBytes(Paths.get(mainFile.getPath()));
    }

    /************************************************
     * Test FileDiff.diff()
     ************************************************/
    @Test
    public void diffMustBeEquals() throws IOException {

        final byte[] bytesMainFile = FileDiffTest.fileAsByte("main-file.txt");
        final FileDiff fileDiff = new FileDiff();
        fileDiff.setLeft(bytesMainFile);
        fileDiff.setRight(bytesMainFile);
        assertThat(fileDiff.diff(), is(DiffResult.EQUALS));
    }

    @Test
    public void diffMustBeDifferentSize() throws IOException {

        final byte[] bytesMainFile = FileDiffTest.fileAsByte("main-file.txt");
        final byte[] bytesDifferentSizeFile = FileDiffTest.fileAsByte("different-size-file.txt");
        ;

        final FileDiff fileDiff = new FileDiff();
        fileDiff.setLeft(bytesMainFile);
        fileDiff.setRight(bytesDifferentSizeFile);
        assertThat(fileDiff.diff(), is(DiffResult.DIFERENT_SIZE));

    }

    @Test
    public void diffMustBeSameSizeDifferentContent() throws IOException {

        final byte[] bytesMainFile = FileDiffTest.fileAsByte("main-file.txt");
        final byte[] bytesSameSizeFile = FileDiffTest.fileAsByte("same-size-file.txt");

        final FileDiff fileDiff = new FileDiff();
        fileDiff.setLeft(bytesMainFile);
        fileDiff.setRight(bytesSameSizeFile);
        assertThat(fileDiff.diff(), is(DiffResult.SAME_SIZE_NOT_EQUALS));
    }

    @Test
    public void diffMustThrowIllegalArgumentException() throws IOException {

        final byte[] bytesMainFile = FileDiffTest.fileAsByte("main-file.txt");

        // not set left neither right
        FileDiff fileDiff = new FileDiff();
        try {
            fileDiff.diff();
            fail();
        } catch (final IllegalArgumentException e) {
        }

        // not set left
        fileDiff = new FileDiff();
        fileDiff.setLeft(bytesMainFile);
        try {
            fileDiff.diff();
            fail();
        } catch (final IllegalArgumentException e) {}

        // not set right
        fileDiff = new FileDiff();
        fileDiff.setRight(bytesMainFile);
        try {
            fileDiff.diff();
            fail();
        } catch (final IllegalArgumentException e) {
        }

    }

    /************************************************
     * Test FileDiff.result()
     ************************************************/
    @Test
    public void resultMustThrowIllegalArgumentException() throws Exception {

        final FileDiff fileDiff = spy(new FileDiff());
        // if files are equals
        try {
            doReturn(DiffResult.EQUALS).when(fileDiff).diff();
            fileDiff.result();
            fail();
        } catch (final IllegalArgumentException e) {
            final JsonObject resultAsJson = fileDiff.resultAsJson();
            assertNotNull(resultAsJson.get("message"));
            assertNull(resultAsJson.get("offset"));
        }

        // if files have different sizes
        try {
            doReturn(DiffResult.DIFERENT_SIZE).when(fileDiff).diff();
            fileDiff.result();
            fail();
        } catch (final IllegalArgumentException e) {
            final JsonObject resultAsJson = fileDiff.resultAsJson();
            assertNotNull(resultAsJson.get("message"));
            assertNull(resultAsJson.get("offset"));
        }
    }

    @Test
    public void resultWithOnlyPartOfTheFileDiffer() throws IOException {

        final FileDiff fileDiff = spy(new FileDiff());
        doReturn(DiffResult.SAME_SIZE_NOT_EQUALS).when(fileDiff).diff();

        final byte[] mainFile = FileDiffTest.fileAsByte("main-file.txt");
        final byte[] sameSizeFile = FileDiffTest.fileAsByte("same-size-file.txt");

        fileDiff.setLeft(mainFile);
        fileDiff.setRight(sameSizeFile);
        final Map<Integer, Integer> result = fileDiff.result();

        assertThat(result.size(), is(1));

        // assert that only position 11 is an offset
        for (int i = 0; i < mainFile.length; i++) {
            if (i != 11) {
                assertThat(result.get(i), nullValue());
            } else {
                assertThat(result.get(i), notNullValue());
            }
        }
        assertThat(result.get(11), is(4));

        // lets check the json return
        final JsonObject resultAsJson = fileDiff.resultAsJson();
        assertNull(resultAsJson.get("message"));
        assertThat(resultAsJson.get("11").getAsInt(), is(4));
    }

    @Test
    public void resultWithWholeFileDiffer() throws IOException {

        final FileDiff fileDiff = spy(new FileDiff());
        doReturn(DiffResult.SAME_SIZE_NOT_EQUALS).when(fileDiff).diff();

        final byte[] mainFile = FileDiffTest.fileAsByte("main-file.txt");
        final byte[] sameSizeFile = FileDiffTest.fileAsByte("same-size-file-totally-diff.txt");

        fileDiff.setLeft(mainFile);
        fileDiff.setRight(sameSizeFile);
        final Map<Integer, Integer> result = fileDiff.result();

        assertThat(result.size(), is(1));

        // assert that only position 0 is an offset
        for (int i = 0; i < mainFile.length; i++) {
            if (i != 0) {
                assertThat(result.get(i), nullValue());
            } else {
                assertThat(result.get(i), notNullValue());
            }
        }
        assertThat(result.get(0), is(mainFile.length));

        // lets check the json return
        final JsonObject resultAsJson = fileDiff.resultAsJson();
        assertNull(resultAsJson.get("message"));
        assertThat(resultAsJson.get("0").getAsInt(), is(mainFile.length));

    }

    @Test
    public void resultWithTwoOffsets() throws IOException {

        final FileDiff fileDiff = spy(new FileDiff());
        doReturn(DiffResult.SAME_SIZE_NOT_EQUALS).when(fileDiff).diff();

        final byte[] mainFile = FileDiffTest.fileAsByte("main-file.txt");
        final byte[] sameSizeFile = FileDiffTest.fileAsByte("same-size-file-two-offsets.txt");

        fileDiff.setLeft(mainFile);
        fileDiff.setRight(sameSizeFile);
        final Map<Integer, Integer> result = fileDiff.result();

        assertThat(result.size(), is(2));

        // assert that only position 5 and 11 is an offset
        for (int i = 0; i < mainFile.length; i++) {
            if (i != 5 && i != 11) {
                assertThat(result.get(i), nullValue());
            } else
                assertThat(result.get(i), notNullValue());
        }
        assertThat(result.get(5), is(1));
        assertThat(result.get(11), is(4));

        // lets check the json return
        final JsonObject resultAsJson = fileDiff.resultAsJson();
        assertNull(resultAsJson.get("message"));
        assertThat(resultAsJson.get("5").getAsInt(), is(1));
        assertThat(resultAsJson.get("11").getAsInt(), is(4));
    }

}
