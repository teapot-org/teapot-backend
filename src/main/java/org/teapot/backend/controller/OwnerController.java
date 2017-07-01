package org.teapot.backend.controller;

import com.google.common.primitives.Longs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.teapot.backend.controller.exception.ResourceNotFoundException;
import org.teapot.backend.model.Owner;
import org.teapot.backend.repository.OwnerRepository;

import java.util.List;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/owners")
public class OwnerController {

    @Autowired
    private OwnerRepository ownerRepository;

    @GetMapping
    public List<Owner> getOwners(Pageable pageable) {
        return ownerRepository.findAll(pageable).getContent();
    }

    @GetMapping("/{idOrName}")
    public Owner getOwner(@PathVariable String idOrName) {
        Long id = Longs.tryParse(idOrName);
        return ofNullable((id != null)
                ? ownerRepository.findOne(id)
                : ownerRepository.findByName(idOrName))
                .orElseThrow(ResourceNotFoundException::new);
    }
}
