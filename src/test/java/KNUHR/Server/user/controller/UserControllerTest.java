package KNUHR.Server.user.controller;

import KNUHR.Server.user.dto.LoginRequest;
import KNUHR.Server.user.dto.RegisterRequest;
import KNUHR.Server.user.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("hi@knu.ac.kr");
        loginRequest.setPassword("1111");

        ObjectMapper objectMapper = new ObjectMapper();

        // when
        mockMvc.perform(RestDocumentationRequestBuilders
                .post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
                // then
                .andExpect(status().isOk())
                .andDo(
                        document("login",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email").description("로그인 유저 이메일 주소"),
                                        fieldWithPath("password").description("로그인 유저 패스워드")
                                ),
                                responseFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("해당 유저 이메일 주소"),
                                        fieldWithPath("token").type(JsonFieldType.STRING).description("해당 유저 로그인 토큰")
                                )
                        )
        );
    }

    @Test
    void register() throws Exception {
        // given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .userName("김테스트")
                .nickName("테스트")
                .email("test@knu.ac.kr")
                .password("123456")
                .verified(true)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        // when
        mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest))
                )
                // then
                .andExpect(status().isCreated())
                .andDo(
                        document("register",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("userName").description("회원가입 유저 이름"),
                                        fieldWithPath("nickName").description("회원가입 유저 닉네임"),
                                        fieldWithPath("email").description("회원가입 유저 이메일 주소"),
                                        fieldWithPath("password").description("회원가입 유저 패스워드"),
                                        fieldWithPath("verified").description("이메일 인증 여부")
                                )
                        )
                );
    }
}