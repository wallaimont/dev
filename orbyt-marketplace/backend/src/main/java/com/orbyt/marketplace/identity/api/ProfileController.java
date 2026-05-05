package com.orbyt.marketplace.identity.api;

import com.orbyt.marketplace.identity.domain.UserAddress;
import com.orbyt.marketplace.identity.domain.UserProfile;
import com.orbyt.marketplace.identity.repository.UserAddressRepository;
import com.orbyt.marketplace.identity.repository.UserProfileRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileRepository profileRepository;
    private final UserAddressRepository addressRepository;

    @GetMapping("/{userId}")
    public UserProfile getProfile(@PathVariable UUID userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
    }

    @PutMapping("/{userId}")
    public UserProfile updateProfile(@PathVariable UUID userId, @RequestBody UserProfile update) {
        UserProfile profile = profileRepository.findByUserId(userId).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUserId(userId);
            p.setTenantId(TenantContext.require());
            return p;
        });
        profile.setDisplayName(update.getDisplayName());
        profile.setPhone(update.getPhone());
        profile.setCpf(update.getCpf());
        profile.setBirthDate(update.getBirthDate());
        profile.setAvatarUrl(update.getAvatarUrl());
        profile.setPreferredLanguage(update.getPreferredLanguage());
        profile.setPreferredCurrency(update.getPreferredCurrency());
        return profileRepository.save(profile);
    }

    @GetMapping("/{userId}/addresses")
    public List<UserAddress> getAddresses(@PathVariable UUID userId) {
        return addressRepository.findByUserIdOrderByDefaultAddressDesc(userId);
    }

    @PostMapping("/{userId}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public UserAddress addAddress(@PathVariable UUID userId, @RequestBody UserAddress address) {
        address.setUserId(userId);
        address.setTenantId(TenantContext.require());
        return addressRepository.save(address);
    }

    @PutMapping("/{userId}/addresses/{addressId}")
    public UserAddress updateAddress(@PathVariable UUID userId, @PathVariable UUID addressId,
                                      @RequestBody UserAddress update) {
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        address.setLabel(update.getLabel());
        address.setStreet(update.getStreet());
        address.setNumber(update.getNumber());
        address.setComplement(update.getComplement());
        address.setNeighborhood(update.getNeighborhood());
        address.setCity(update.getCity());
        address.setState(update.getState());
        address.setZipCode(update.getZipCode());
        address.setDefaultAddress(update.isDefaultAddress());
        return addressRepository.save(address);
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable UUID userId, @PathVariable UUID addressId) {
        addressRepository.deleteById(addressId);
    }
}
