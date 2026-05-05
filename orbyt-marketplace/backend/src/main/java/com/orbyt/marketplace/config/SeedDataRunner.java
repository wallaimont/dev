package com.orbyt.marketplace.config;

import com.orbyt.marketplace.catalog.domain.Brand;
import com.orbyt.marketplace.catalog.domain.Category;
import com.orbyt.marketplace.catalog.domain.Product;
import com.orbyt.marketplace.catalog.domain.Seller;
import com.orbyt.marketplace.catalog.domain.Store;
import com.orbyt.marketplace.catalog.repository.BrandRepository;
import com.orbyt.marketplace.catalog.repository.CategoryRepository;
import com.orbyt.marketplace.catalog.repository.ProductRepository;
import com.orbyt.marketplace.catalog.repository.SellerRepository;
import com.orbyt.marketplace.catalog.repository.StoreRepository;
import com.orbyt.marketplace.identity.application.RbacProvisioningService;
import com.orbyt.marketplace.identity.domain.User;
import com.orbyt.marketplace.identity.repository.UserRepository;
import com.orbyt.marketplace.platform.domain.Tenant;
import com.orbyt.marketplace.platform.repository.TenantRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SeedDataRunner implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final RbacProvisioningService rbacProvisioningService;

    @Override
    @Transactional
    public void run(String... args) {
        if (tenantRepository.findBySlugIgnoreCase("orbyt-demo").isPresent()) {
            return;
        }

        Tenant tenant = new Tenant();
        tenant.setSlug("orbyt-demo");
        tenant.setName("Orbyt Demo");
        tenant.setDefaultLocale("pt-BR");
        tenant.setCurrencyCode("BRL");
        tenant.setStatus("ACTIVE");
        tenant = tenantRepository.saveAndFlush(tenant);

        User user = new User();
        user.setTenantId(tenant.getId());
        user.setEmail("admin@orbyt.local");
        user.setFullName("Orbyt Admin");
        user.setPasswordHash(passwordEncoder.encode("Admin@123"));
        user.setStatus("ACTIVE");
        user = userRepository.saveAndFlush(user);
        rbacProvisioningService.provisionTenantDefaults(tenant.getId(), user.getId());

        Seller seller = new Seller();
        seller.setTenantId(tenant.getId());
        seller.setUserId(user.getId());
        seller.setLegalName("Orbyt Store LTDA");
        seller.setDocumentNumber("00000000000100");
        seller.setApprovalStatus("APPROVED");
        seller.setStatus("ACTIVE");
        seller = sellerRepository.save(seller);

        Store store = new Store();
        store.setTenantId(tenant.getId());
        store.setSellerId(seller.getId());
        store.setName("Orbyt Official");
        store.setSlug("orbyt-official");
        store.setDescription("Loja oficial com itens premium.");
        store.setStatus("ACTIVE");
        store = storeRepository.save(store);

        Category category = new Category();
        category.setTenantId(tenant.getId());
        category.setName("Tecnologia");
        category.setSlug("tecnologia");
        category.setStatus("ACTIVE");
        category = categoryRepository.save(category);

        Brand brand = new Brand();
        brand.setTenantId(tenant.getId());
        brand.setName("Orbyt");
        brand.setSlug("orbyt");
        brand.setStatus("ACTIVE");
        brandRepository.save(brand);

        Product product = new Product();
        product.setTenantId(tenant.getId());
        product.setStoreId(store.getId());
        product.setCategoryId(category.getId());
        product.setSku("ORB-DOCK-01");
        product.setName("Orbyt Pro Dock");
        product.setDescription("Dock premium com carregamento rapido e conectividade corporativa.");
        product.setPrice(BigDecimal.valueOf(399.90));
        product.setPromotionalPrice(BigDecimal.valueOf(349.90));
        product.setCurrencyCode("BRL");
        product.setApprovalStatus("APPROVED");
        product.setStatus("ACTIVE");
        productRepository.save(product);
    }
}
