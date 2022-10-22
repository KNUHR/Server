package KNUHR.Server.member.controller;

import KNUHR.Server.member.dto.LoginRequest;
import KNUHR.Server.member.dto.RegisterRequest;
import KNUHR.Server.member.dto.SendEmailRequest;
import KNUHR.Server.member.dto.VerifyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("hi@knu.ac.kr")
                .password("1111")
                .build();

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
    @Transactional
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

    @RegisterExtension
    static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("knuhr", "helloworld"))
            .withPerMethodLifecycle(false);

    @Test
    @Order(1)
    void sendEmail() throws Exception {
        // given
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .email("knuhr@spring.io")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        // when
        mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/api/user/register/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendEmailRequest))
                )
                // then
                .andExpect(status().isOk())
                .andDo(
                        document("sendEmail",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email").description("회원가입 유저 이메일 주소")
                                )
                        )
                );
    }

    // sendEmail 테스트가 수행된 다음 실행되어야 함
    @Test
    @Order(2)
    void verify() throws Exception {
        // given
        // 도착한 메일 내용(인증 코드) 저장
        MimeMessage[] receivedMessages = greenMailExtension.getReceivedMessages();
        MimeMessage receivedMessage = receivedMessages[0];

        VerifyRequest verifyRequest = VerifyRequest.builder()
                .email("knuhr@spring.io")
                .verifyCode(GreenMailUtil.getBody(receivedMessage))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        // when
        mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/api/user/register/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest))
                )
                // then
                .andExpect(status().isOk())
                .andDo(
                        document("verify",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email").description("회원가입 유저 이메일 주소"),
                                        fieldWithPath("verifyCode").description("인증 코드")
                                )
                        )
                );
    }
}