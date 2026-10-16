package com.village.familytree.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.village.familytree.model.FamilyMemberDTO;
import com.village.familytree.model.FamilyTreeResponse;
import com.village.familytree.service.FamilyTreeService;

@RestController
@RequestMapping("/api/family-tree")
public class FamilyTreeController {

    @Autowired
    private FamilyTreeService familyTreeService;
    
    /**
     * Get all family members with their relationships
     */
    @GetMapping("/members")
    public ResponseEntity<FamilyTreeResponse> getAllMembers() {
        FamilyTreeResponse response = familyTreeService.getAllFamilyMembers();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get a specific family member by ID
     */
    @GetMapping("/members/{id}")
    public ResponseEntity<FamilyMemberDTO> getMemberById(@PathVariable Long id) {
        FamilyMemberDTO member = familyTreeService.getFamilyMemberById(id);
        return ResponseEntity.ok(member);
    }

    /**
     * Search for family members
     */
    @GetMapping("/members/search")
    public ResponseEntity<List<FamilyMemberDTO>> searchMembers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String role) {
        List<FamilyMemberDTO> members = familyTreeService.searchFamilyMembers(name, gender, role);
        return ResponseEntity.ok(members);
    }
    
    /**
     * Get family tree starting from a specific ancestor
     */
    @GetMapping("/tree/{ancestorId}")
    public ResponseEntity<FamilyTreeResponse> getFamilyTreeByAncestor(@PathVariable Long ancestorId) {
        FamilyTreeResponse response = familyTreeService.getFamilyTreeByAncestor(ancestorId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Add a new family member
     */
    @PostMapping("/members")
    public ResponseEntity<FamilyMemberDTO> createMember(@RequestBody FamilyMemberDTO memberDTO) {
        FamilyMemberDTO newMember = familyTreeService.createFamilyMember(memberDTO);
        return new ResponseEntity<>(newMember, HttpStatus.CREATED);
    }

    /**
     * Update an existing family member
     */
    @PutMapping("/members/{id}")
    public ResponseEntity<FamilyMemberDTO> updateMember(
            @PathVariable Long id,
            @RequestBody FamilyMemberDTO memberDTO) {
        FamilyMemberDTO updatedMember = familyTreeService.updateFamilyMember(id, memberDTO);
        return ResponseEntity.ok(updatedMember);
    }
    
    /**
     * Delete a family member
     */
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        familyTreeService.deleteFamilyMember(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Generate sample data for testing
     */
    @PostMapping("/generate-sample-data")
    public ResponseEntity<String> generateSampleData() {
        familyTreeService.generateSampleData();
        return ResponseEntity.ok("Sample data generated successfully");
    }
}
