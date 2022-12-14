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
                                        fieldWithPath("email").description("????????? ?????? ????????? ??????"),
                                        fieldWithPath("password").description("????????? ?????? ????????????")
                                ),
                                responseFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("?????? ?????? ????????? ??????"),
                                        fieldWithPath("token").type(JsonFieldType.STRING).description("?????? ?????? ????????? ??????")
                                )
                        )
        );
    }

    @Test
    @Transactional
    void register() throws Exception {
        // given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .userName("????????????")
                .nickName("?????????")
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
                                        fieldWithPath("userName").description("???????????? ?????? ??????"),
                                        fieldWithPath("nickName").description("???????????? ?????? ?????????"),
                                        fieldWithPath("email").description("???????????? ?????? ????????? ??????"),
                                        fieldWithPath("password").description("???????????? ?????? ????????????"),
                                        fieldWithPath("verified").description("????????? ?????? ??????")
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
                                        fieldWithPath("email").description("???????????? ?????? ????????? ??????")
                                )
                        )
                );
    }

    // sendEmail ???????????? ????????? ?????? ??????????????? ???
    @Test
    @Order(2)
    void verify() throws Exception {
        // given
        // ????????? ?????? ??????(?????? ??????) ??????
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
                                        fieldWithPath("email").description("???????????? ?????? ????????? ??????"),
                                        fieldWithPath("verifyCode").description("?????? ??????")
                                )
                        )
                );
    }
}