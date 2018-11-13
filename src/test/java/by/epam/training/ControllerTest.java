package by.epam.training;

import by.epam.training.controller.HtmlParsingController;
import by.epam.training.service.ParsingService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ControllerTest {

    @Mock
    private ParsingService concurrentParsingService;

    @InjectMocks
    private HtmlParsingController controller;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void controllerTest() throws Exception {

        Set<String> stringSet2 = new HashSet<>(Arrays.asList("url", "url2"));

        when(concurrentParsingService.parse("some", false)).thenReturn(stringSet2);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/parseConcurrently").accept(
                MediaType.APPLICATION_JSON).param("url", "some").param("skipCacheCheck", "false");

        MockMvc mockMvc = standaloneSetup(controller).build();

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        System.out.println(result.getResponse().getContentAsString());

        String expected = "[\"url2\",\"url\"]";

        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);

        verify(concurrentParsingService, atLeastOnce()).parse("some", false);
    }

}
