package com.village.familytree.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.village.familytree.model.FamilyMember;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    
    // Find members by name (case insensitive, partial match)
    List<FamilyMember> findByNameContainingIgnoreCase(String name);
    
    // Find members by gender
    List<FamilyMember> findByGender(String gender);
    
    // Find root members (members with no parents)
    @Query("SELECT m FROM FamilyMember m WHERE m.parents IS EMPTY")
    List<FamilyMember> findRootMembers();
    
    // Find members born after a specific date
    List<FamilyMember> findByBirthDateAfter(java.time.LocalDate date);
    
    // Find members born before a specific date
    List<FamilyMember> findByBirthDateBefore(java.time.LocalDate date);
    
    // Find members with a specific village role
    List<FamilyMember> findByVillageRoleContainingIgnoreCase(String role);
    
    // Find members with a specific parent
    @Query("SELECT m FROM FamilyMember m JOIN m.parents p WHERE p.id = :parentId")
    List<FamilyMember> findByParentId(Long parentId);
    
    // Find members with a specific spouse
    @Query("SELECT m FROM FamilyMember m JOIN m.spouses s WHERE s.id = :spouseId")
    List<FamilyMember> findBySpouseId(Long spouseId);
    
    // Custom query to search members by various criteria
    @Query("SELECT m FROM FamilyMember m WHERE " +
           "(:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:gender IS NULL OR m.gender = :gender) AND " +
           "(:role IS NULL OR LOWER(m.villageRole) LIKE LOWER(CONCAT('%', :role, '%')))")
    List<FamilyMember> searchMembers(String name, String gender, String role);
}
