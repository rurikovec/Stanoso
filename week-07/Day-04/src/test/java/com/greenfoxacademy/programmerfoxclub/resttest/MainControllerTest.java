package com.greenfoxacademy.programmerfoxclub.resttest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.greenfoxacademy.programmerfoxclub.models.FoxStock;

import org.assertj.core.internal.bytebuddy.jar.asm.Type;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FoxStock foxStock;

    @Test
    public void mockMainPageTest () throws Exception {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    public void mockLoginTest () throws Exception {
        this.mockMvc.perform(get("/login")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void mocLoginTest_isFoxInStock_setNewFox() throws Exception {
        when (foxStock.isFoxInStock("Joseph")).thenReturn(false);
        this.mockMvc.perform(get("/login")).andDo(print())
                .andExpect(content().string(containsString("Please provide a name")))
                .andExpect(status().isOk());
    }

    @Test
    public void mocLoginTest_isFoxInStock_loggedFox() throws Exception {
        when (foxStock.isFoxInStock("Joey")).thenReturn(true);
        this.mockMvc.perform(get("/login")).andDo(print())
//                .andExpect(content().string(containsString("This is Joseph")))
                .andExpect(status().isOk());
    }


}
