package se.lexicon.subscriptionapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import se.lexicon.subscriptionapi.domain.constant.Role;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;
import se.lexicon.subscriptionapi.domain.entity.Customer;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.repository.CustomerRepository;
import se.lexicon.subscriptionapi.repository.OperatorRepository;
import se.lexicon.subscriptionapi.repository.PlanRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final OperatorRepository operatorRepository;
    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedRegularUser();
        seedOperatorsAndPlans();
    }

    private void seedAdminUser() {
        if (!customerRepository.existsByEmail("admin@example.com")) {
            Customer admin = new Customer();
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("Adminson");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));
            customerRepository.save(admin);
        }
    }

    private void seedRegularUser() {
        if (!customerRepository.existsByEmail("user@example.com")) {
            Customer user = new Customer();
            user.setEmail("user@example.com");
            user.setFirstName("User");
            user.setLastName("Userson");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRoles(Set.of(Role.ROLE_USER));
            customerRepository.save(user);
        }
    }

    private void seedOperatorsAndPlans() {
        if (operatorRepository.count() > 0) return;

        Operator telia = new Operator();
        telia.setName("Telia");

        Operator telenor = new Operator();
        telenor.setName("Telenor");

        operatorRepository.saveAll(List.of(telia, telenor));

        planRepository.saveAll(List.of(
                plan("Fiber 50",         new BigDecimal("299"), ServiceType.INTERNET, 50,   true,  telia),
                plan("Fiber 100",        new BigDecimal("399"), ServiceType.INTERNET, 100,  true,  telia),
                plan("Fiber 300",        new BigDecimal("599"), ServiceType.INTERNET, 300,  false, telia),
                plan("Mobile Basic",     new BigDecimal("149"), ServiceType.MOBILE,   10,   true,  telia),
                plan("Mobile Plus",      new BigDecimal("249"), ServiceType.MOBILE,   30,   true,  telia),
                plan("Mobile Unlimited", new BigDecimal("349"), ServiceType.MOBILE,   null, true,  telia),
                plan("Fiber 50",         new BigDecimal("279"), ServiceType.INTERNET, 50,   true,  telenor),
                plan("Fiber 100",        new BigDecimal("379"), ServiceType.INTERNET, 100,  true,  telenor),
                plan("Fiber 300",        new BigDecimal("579"), ServiceType.INTERNET, 300,  false, telenor),
                plan("Mobile Basic",     new BigDecimal("139"), ServiceType.MOBILE,   10,   true,  telenor),
                plan("Mobile Plus",      new BigDecimal("239"), ServiceType.MOBILE,   30,   true,  telenor),
                plan("Mobile Unlimited", new BigDecimal("339"), ServiceType.MOBILE,   null, false, telenor)
        ));
    }

    private Plan plan(String name, BigDecimal price, ServiceType type, Integer dataLimit, boolean active, Operator operator) {
        Plan plan = new Plan();
        plan.setName(name);
        plan.setPrice(price);
        plan.setServiceType(type);
        plan.setDataLimitGb(dataLimit);
        plan.setActive(active);
        plan.setOperator(operator);
        return plan;
    }
}
