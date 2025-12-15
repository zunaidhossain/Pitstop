package com.pitstop.app.service.impl;
import com.pitstop.app.constants.VehicleType;
import com.pitstop.app.constants.WorkshopServiceType;
import com.pitstop.app.dto.CreatePricingRuleRequest;
import com.pitstop.app.dto.PricingRuleResponse;
import com.pitstop.app.dto.UpdatePricingRuleRequest;
import com.pitstop.app.model.PricingRule;
import com.pitstop.app.repository.PricingRuleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("AdminPricingService End to End Test")
class AdminPricingServiceImplTest {
    @Autowired
    private AdminPricingServiceImpl adminPricingService;
    @Autowired
    private PricingRuleRepository pricingRuleRepository;
    private String pricingRuleId;
    private int initialCount;

    private void authenticateAdmin() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "admin_test_user",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    @BeforeAll
    void cleanBeforeAll() {
        initialCount = 0;
        pricingRuleRepository.deleteByVehicleTypeAndServiceType(VehicleType.TWO_WHEELER, WorkshopServiceType.OIL_CHANGE);
    }
    @BeforeEach
    void authSetup() {
        authenticateAdmin();
    }
    @AfterEach
    void cleanSecurityContext() {
        SecurityContextHolder.clearContext();
    }
    @Order(1)
    @Test
    @DisplayName("Admin should create pricing rule successfully")
    void shouldCreatePricingRuleSuccessfully() {

        CreatePricingRuleRequest request = new CreatePricingRuleRequest();
        request.setVehicleType("TWO_WHEELER");
        request.setWorkshopServiceType("OIL_CHANGE");
        request.setAmount(300.0);
        request.setPremiumAmount(100.0);

        PricingRuleResponse response = adminPricingService.createPricingRule(request);
        assertNotNull(response);
        assertNotNull(response.getId());
        pricingRuleId = response.getId();
    }
    @Order(2)
    @Test
    @DisplayName("Admin should not create duplicate pricing rule")
    void shouldNotAllowDuplicatePricingRule() {

        CreatePricingRuleRequest duplicate = new CreatePricingRuleRequest();
        duplicate.setVehicleType("TWO_WHEELER");
        duplicate.setWorkshopServiceType("OIL_CHANGE");
        duplicate.setAmount(500.0);
        duplicate.setPremiumAmount(200.0);

        assertThrows(RuntimeException.class, () ->
                adminPricingService.createPricingRule(duplicate)
        );
    }
    @Order(3)
    @Test
    @DisplayName("Admin should update pricing rule amount and premium only")
    void shouldUpdatePricingRuleAmountsOnly() {
        UpdatePricingRuleRequest update = new UpdatePricingRuleRequest();
        update.setAmount(350.0);
        update.setPremiumAmount(150.0);

        PricingRuleResponse response = adminPricingService.updatePricingRule(update,pricingRuleId);
        assertEquals(350.0, response.getAmount());
        assertEquals(150.0, response.getPremiumAmount());
        assertEquals("TWO_WHEELER", response.getVehicleType().name());
        assertEquals("OIL_CHANGE", response.getWorkshopServiceType().name());
    }
    @Order(4)
    @Test
    @DisplayName("Admin should fetch all pricing rules")
    void shouldGetAllPricingRules() {

        List<PricingRuleResponse> rules = adminPricingService.getAllPricingRules();
        assertFalse(rules.isEmpty());
        boolean ruleFound = rules.stream().anyMatch(rule -> rule.getId().equals(pricingRuleId));
        assertTrue(ruleFound);
    }
    @Order(5)
    @Test
    @DisplayName("Admin should delete pricing rule")
    void shouldDeletePricingRule() {
        adminPricingService.deletePricingRule(pricingRuleId);
        assertTrue(pricingRuleRepository.findById(pricingRuleId).isEmpty());
    }
    @AfterAll
    void tearDownAll() {
        pricingRuleRepository.deleteByVehicleTypeAndServiceType(VehicleType.TWO_WHEELER, WorkshopServiceType.OIL_CHANGE);
        SecurityContextHolder.clearContext();
    }
}