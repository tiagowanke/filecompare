package com.wearewaes.filecompare.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import com.google.gson.JsonObject;
import com.wearewaes.filecompare.model.FileDiff;
import com.wearewaes.filecompare.model.FileDiffTest;
import com.wearewaes.filecompare.service.FileDiffService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = FileDiffController.class)
public class FileDiffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileDiffService fileDiffServiceMock;
    @Mock
    private FileDiff fileDiffMock;

    /**
     * @return To help writting tests for this class this method reads from resources package the given file name and return as byte[].
     */
    private static byte[] fileAsByte(String fileName) throws IOException {
        final ClassLoader classLoader = FileDiffTest.class.getClassLoader();
        final File mainFile = new File(classLoader.getResource(fileName).getFile());
        return Files.readAllBytes(Paths.get(mainFile.getPath()));
    }

    /*******************************
     * Test addFile(Long id, String side, String base64file)
     *******************************/

    /**
     * Call addFile without setting parameter as JSON
     */
    @Test
    public void addFileWithUnsuportedMedia() throws Exception {

        final String path = "/v1/diff/%d/left";

        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(String.format(path, 1L))
                .content(fileAsByte("main-file.txt"));
        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertThat(result.getResolvedException(), instanceOf(HttpMediaTypeNotSupportedException.class));
    }

    @Test
    public void addLeft() throws Exception {

        final String path = "/v1/diff/%d/left";

        final Long id = 1L;
        final byte[] fileAsByte = fileAsByte("main-file.txt");

        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(String.format(path, id))
                .content(fileAsByte)
                .header("Content-Type", "application/json");
        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        verify(this.fileDiffServiceMock, times(1)).addLeft(id, Base64.decodeBase64(fileAsByte));
        verify(this.fileDiffServiceMock, times(0)).addRight(id, Base64.decodeBase64(fileAsByte));
        assertThat(result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
    }

    @Test
    public void addRight() throws Exception {

        final String path = "/v1/diff/%d/right";

        final Long id = 1L;
        final byte[] fileAsByte = fileAsByte("main-file.txt");

        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(String.format(path, id)).content(fileAsByte).header("Content-Type", "application/json");
        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        verify(this.fileDiffServiceMock, times(0)).addLeft(id, Base64.decodeBase64(fileAsByte));
        verify(this.fileDiffServiceMock, times(1)).addRight(id, Base64.decodeBase64(fileAsByte));
        assertThat(result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
    }

    /*******************************
     * Test diff(Long id)
     * @throws Exception
     *******************************/
    @Test
    public void diffMustReturnNotFound() throws Exception {

        final String path = "/v1/diff/%d";

        final Long id = 1L;
        when(this.fileDiffServiceMock.fileDiff(anyLong())).thenReturn(null);

        final RequestBuilder requestBuilder = MockMvcRequestBuilders.get(String.format(path, id));
        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertThat(result.getResponse().getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
        verify(this.fileDiffMock, times(0)).resultAsJson();
    }

    @Test
    public void diffMustReturnJson() throws Exception {

        final String path = "/v1/diff/%d";

        final Long id = 1L;
        when(this.fileDiffServiceMock.fileDiff(anyLong())).thenReturn(this.fileDiffMock);
        when(this.fileDiffMock.resultAsJson()).thenReturn(new JsonObject());

        final RequestBuilder requestBuilder = MockMvcRequestBuilders.get(String.format(path, id));
        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertThat(result.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
        verify(this.fileDiffMock, times(1)).resultAsJson();
    }

}
