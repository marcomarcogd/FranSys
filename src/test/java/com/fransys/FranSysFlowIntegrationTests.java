package com.fransys;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FranSysFlowIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldLoginAndListLeads() throws Exception {
        String token = login("admin", "Admin@123");

        mockMvc.perform(get("/api/leads")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldCreatePublicLeadAndCompleteFeedbackFlow() throws Exception {
        MvcResult publicLeadResult = mockMvc.perform(post("/api/public/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "customerName", "张三",
                                "contactPhone", "13900000000",
                                "sourceChannel", "自然咨询",
                                "serviceObject", "本人",
                                "initialNeedType", "康护",
                                "servicePreference", "上门",
                                "urgency", "高"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andReturn();

        JsonNode leadJson = objectMapper.readTree(publicLeadResult.getResponse().getContentAsString()).path("data");
        long leadId = leadJson.path("id").asLong();
        String token = login("ops", "Ops@123");

        mockMvc.perform(put("/api/deliveries/{leadId}", leadId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "serviceEnterprise", "沪康护理服务中心",
                                "serviceProject", "上门康护",
                                "serviceMode", "上门",
                                "serviceDate", "2026-04-07",
                                "serviceTime", "10:00-11:00",
                                "customerConfirmed", true,
                                "enterpriseConfirmed", true
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.orderNo", notNullValue()));

        MvcResult tokenResult = mockMvc.perform(post("/api/deliveries/{leadId}/feedback-token", leadId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andReturn();

        String feedbackToken = objectMapper.readTree(tokenResult.getResponse().getContentAsString())
                .path("data").path("token").asText();

        mockMvc.perform(get("/api/public/feedback/{token}", feedbackToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.customerName").value("张三"));

        mockMvc.perform(post("/api/public/feedback/{token}", feedbackToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "satisfactionScore", 5,
                                "mostSatisfiedPoint", "服务专业",
                                "willingContinue", true,
                                "willingRecommend", true,
                                "hasNewDemand", false
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).path("data").path("token").asText();
    }
}
