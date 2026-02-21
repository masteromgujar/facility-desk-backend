package com.facilitydesk.facility_desk.service;

import com.facilitydesk.facility_desk.dto.VendorDto;
import com.facilitydesk.facility_desk.exception.BadRequestException;
import com.facilitydesk.facility_desk.exception.ResourceNotFoundException;
import com.facilitydesk.facility_desk.model.Vendor;
import com.facilitydesk.facility_desk.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


//this is the Monolothic Programmmed Archietecture
@Service
@RequiredArgsConstructor
@Slf4j
public class VendorService {

    private final VendorRepository vendorRepository;

    public Page<VendorDto.Response> getAllVendors(Pageable pageable) {
        return vendorRepository.findAllByActiveTrue(pageable).map(this::toResponse);
    }

    public Page<VendorDto.Response> searchVendors(String keyword, Pageable pageable) {
        return vendorRepository.searchVendors(keyword, pageable).map(this::toResponse);
    }

    public VendorDto.Response getVendorById(Long id) {
        return toResponse(findVendorById(id));
    }

    @Transactional
    public VendorDto.Response createVendor(VendorDto.Request request) {
        if (vendorRepository.existsByContactEmail(request.getContactEmail())) {
            throw new BadRequestException("Vendor with this email already exists!");
        }
        Vendor vendor = Vendor.builder()
                .name(request.getName())
                .services(request.getServices())
                .contactPerson(request.getContactPerson())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .address(request.getAddress())
                .active(request.isActive())
                .build();

        Vendor saved = vendorRepository.save(vendor);
        log.info("Vendor created: {}", saved.getName());
        return toResponse(saved);
    }

    @Transactional
    public VendorDto.Response updateVendor(Long id, VendorDto.Request request) {
        Vendor vendor = findVendorById(id);

        if (StringUtils.hasText(request.getName())) vendor.setName(request.getName());
        if (StringUtils.hasText(request.getServices())) vendor.setServices(request.getServices());
        if (StringUtils.hasText(request.getContactPerson())) vendor.setContactPerson(request.getContactPerson());
        if (StringUtils.hasText(request.getContactEmail())) {
            if (!request.getContactEmail().equals(vendor.getContactEmail())
                    && vendorRepository.existsByContactEmail(request.getContactEmail())) {
                throw new BadRequestException("Email already in use by another vendor.");
            }
            vendor.setContactEmail(request.getContactEmail());
        }
        if (StringUtils.hasText(request.getContactPhone())) vendor.setContactPhone(request.getContactPhone());
        if (StringUtils.hasText(request.getAddress())) vendor.setAddress(request.getAddress());

        Vendor saved = vendorRepository.save(vendor);
        log.info("Vendor updated: {}", saved.getName());
        return toResponse(saved);
    }

    @Transactional
    public void deleteVendor(Long id) {
        Vendor vendor = findVendorById(id);
        vendor.setActive(false);
        vendorRepository.save(vendor);
        log.info("Vendor deactivated: {}", vendor.getName());
    }

    public Vendor findVendorById(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
    }

    public VendorDto.Response toResponse(Vendor vendor) {
        VendorDto.Response r = new VendorDto.Response();
        r.setId(vendor.getId());
        r.setName(vendor.getName());
        r.setServices(vendor.getServices());
        r.setContactPerson(vendor.getContactPerson());
        r.setContactEmail(vendor.getContactEmail());
        r.setContactPhone(vendor.getContactPhone());
        r.setAddress(vendor.getAddress());
        r.setActive(vendor.isActive());
        r.setTotalOrders(vendor.getOrders().size());
        r.setCreatedAt(vendor.getCreatedAt());
        r.setUpdatedAt(vendor.getUpdatedAt());
        return r;
    }
}
