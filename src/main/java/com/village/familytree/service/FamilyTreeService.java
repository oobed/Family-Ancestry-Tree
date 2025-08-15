package com.village.familytree.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.village.familytree.model.FamilyMember;
import com.village.familytree.model.FamilyMemberDTO;
import com.village.familytree.model.FamilyTreeResponse;
import com.village.familytree.repository.FamilyMemberRepository;
import com.village.familytree.exception.ResourceNotFoundException;

@Service
public class FamilyTreeService {

    @Autowired
    private FamilyMemberRepository memberRepository;
    
    /**
     * Get all family members with calculated generation information
     */
    public FamilyTreeResponse getAllFamilyMembers() {
        List<FamilyMember> allMembers = memberRepository.findAll();
        List<FamilyMember> rootMembers = memberRepository.findRootMembers();
        
        // Calculate generations
        calculateGenerations(allMembers, rootMembers);
        
        return new FamilyTreeResponse(allMembers, rootMembers);
    }
    
    /**
     * Get a family member by ID
     */
    public FamilyMember getFamilyMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Family member not found with id: " + id));
    }
    
    /**
     * Search for family members based on criteria
     */
    public List<FamilyMember> searchFamilyMembers(String name, String gender, String role) {
        return memberRepository.searchMembers(name, gender, role);
    }
    
    /**
     * Create a new family member
     */
    @Transactional
    public FamilyMember createFamilyMember(FamilyMemberDTO memberDTO) {
        FamilyMember member = new FamilyMember();
        updateMemberFromDTO(member, memberDTO);
        return memberRepository.save(member);
    }
    
    /**
     * Update an existing family member
     */
    @Transactional
    public FamilyMember updateFamilyMember(Long id, FamilyMemberDTO memberDTO) {
        FamilyMember existingMember = getFamilyMemberById(id);
        updateMemberFromDTO(existingMember, memberDTO);
        return memberRepository.save(existingMember);
    }
    
    /**
     * Delete a family member
     */
    @Transactional
    public void deleteFamilyMember(Long id) {
        FamilyMember member = getFamilyMemberById(id);
        
        // Remove this member from all relationships
        for (FamilyMember parent : member.getParents()) {
            parent.getChildren().remove(member);
        }
        
        for (FamilyMember child : member.getChildren()) {
            child.getParents().remove(member);
        }
        
        for (FamilyMember spouse : member.getSpouses()) {
            spouse.getSpouses().remove(member);
        }
        
        memberRepository.delete(member);
    }
    
    /**
     * Get a graph of family members starting from a specific ancestor
     */
    public FamilyTreeResponse getFamilyTreeByAncestor(Long ancestorId) {
        FamilyMember ancestor = getFamilyMemberById(ancestorId);
        List<FamilyMember> allMembers = new ArrayList<>();
        List<FamilyMember> rootAncestors = new ArrayList<>();
        rootAncestors.add(ancestor);
        
        // Gather all descendants
        Set<FamilyMember> visited = new HashSet<>();
        gatherDescendants(ancestor, allMembers, visited);
        
        // Calculate generations
        calculateGenerations(allMembers, rootAncestors);
        
        return new FamilyTreeResponse(allMembers, rootAncestors);
    }
    
    /**
     * Calculate generations for all members
     */
    private void calculateGenerations(List<FamilyMember> allMembers, List<FamilyMember> rootMembers) {
        // Reset generations
        for (FamilyMember member : allMembers) {
            member.setGeneration(0);
        }
        
        // Calculate generations starting from root members
        for (FamilyMember root : rootMembers) {
            calculateGenerationRecursive(root, 1, new HashSet<>());
        }
    }
    
    /**
     * Helper method to calculate generation recursively
     */
    private void calculateGenerationRecursive(FamilyMember member, int generation, Set<Long> visited) {
        if (member == null || visited.contains(member.getId())) {
            return;
        }
        
        visited.add(member.getId());
        member.setGeneration(Math.max(member.getGeneration(), generation));
        
        // Calculate for children
        for (FamilyMember child : member.getChildren()) {
            calculateGenerationRecursive(child, generation + 1, visited);
        }
    }
    
    /**
     * Gather all descendants of a member
     */
    private void gatherDescendants(FamilyMember member, List<FamilyMember> allMembers, Set<FamilyMember> visited) {
        if (member == null || visited.contains(member)) {
            return;
        }
        
        visited.add(member);
        allMembers.add(member);
        
        // Gather all children and their descendants
        for (FamilyMember child : member.getChildren()) {
            gatherDescendants(child, allMembers, visited);
        }
        
        // Optionally include spouses
        for (FamilyMember spouse : member.getSpouses()) {
            if (!visited.contains(spouse)) {
                visited.add(spouse);
                allMembers.add(spouse);
            }
        }
    }
    
    /**
     * Update a member entity from DTO
     */
    private void updateMemberFromDTO(FamilyMember member, FamilyMemberDTO dto) {
        member.setName(dto.getName());
        member.setGender(dto.getGender());
        member.setBirthDate(dto.getBirthDate());
        member.setDeathDate(dto.getDeathDate());
        member.setVillageRole(dto.getVillageRole());
        member.setNotes(dto.getNotes());
        member.setPhotoUrl(dto.getPhotoUrl());
        
        // Handle parent relationships
        if (dto.getParentIds() != null) {
            List<FamilyMember> parents = new ArrayList<>();
            for (Long parentId : dto.getParentIds()) {
                memberRepository.findById(parentId).ifPresent(parents::add);
            }
            member.setParents(parents);
        }
        
        // Handle spouse relationships
        if (dto.getSpouseIds() != null) {
            List<FamilyMember> spouses = new ArrayList<>();
            for (Long spouseId : dto.getSpouseIds()) {
                memberRepository.findById(spouseId).ifPresent(spouse -> {
                    spouses.add(spouse);
                    if (!spouse.getSpouses().contains(member)) {
                        spouse.getSpouses().add(member);
                        memberRepository.save(spouse);
                    }
                });
            }
            member.setSpouses(spouses);
        }
    }
    
    /**
     * Generate a sample family tree data for testing
     */
    @Transactional
    public void generateSampleData() {
        // Check if data already exists
        if (memberRepository.count() > 0) {
            return; // Don't generate if data exists
        }
        
        // Create sample family members
        FamilyMember founder1 = new FamilyMember("Adebayo Olatunji", "male", java.time.LocalDate.of(1900, 1, 15));
        founder1.setDeathDate(java.time.LocalDate.of(1980, 7, 22));
        founder1.setVillageRole("Village Elder");
        founder1.setNotes("Founder of the eastern settlement");
        
        FamilyMember founder2 = new FamilyMember("Folami Olatunji", "female", java.time.LocalDate.of(1905, 3, 10));
        founder2.setDeathDate(java.time.LocalDate.of(1985, 11, 5));
        founder2.setVillageRole("Midwife");
        founder2.setNotes("Known for her herbal remedies");
        
        // Save the founders first
        founder1 = memberRepository.save(founder1);
        founder2 = memberRepository.save(founder2);
        
        // Add spouse relationship
        founder1.getSpouses().add(founder2);
        founder2.getSpouses().add(founder1);
        
        memberRepository.save(founder1);
        memberRepository.save(founder2);
        
        // Create second generation
        FamilyMember child1 = new FamilyMember("Oluwasegun Olatunji", "male", java.time.LocalDate.of(1925, 6, 20));
        child1.setDeathDate(java.time.LocalDate.of(2000, 9, 14));
        child1.setVillageRole("Farmer");
        child1.setNotes("Introduced new farming techniques");
        child1.getParents().add(founder1);
        child1.getParents().add(founder2);
        
        memberRepository.save(child1);
        
        FamilyMember spouse1 = new FamilyMember("Abimbola Olatunji", "female", java.time.LocalDate.of(1930, 11, 30));
        spouse1.setDeathDate(java.time.LocalDate.of(2005, 4, 17));
        spouse1.setVillageRole("Weaver");
        spouse1.setNotes("Created beautiful traditional textiles");
        
        memberRepository.save(spouse1);
        
        // Add spouse relationship
        child1.getSpouses().add(spouse1);
        spouse1.getSpouses().add(child1);
        
        memberRepository.save(child1);
        memberRepository.save(spouse1);
        
        // Add more generations as needed...
        FamilyMember grandchild = new FamilyMember("Tunde Olatunji", "male", java.time.LocalDate.of(1955, 8, 12));
        grandchild.setVillageRole("Teacher");
        grandchild.setNotes("Educated many generations of village children");
        grandchild.getParents().add(child1);
        grandchild.getParents().add(spouse1);
        
        memberRepository.save(grandchild);
        
        // And so on...
    }
}
