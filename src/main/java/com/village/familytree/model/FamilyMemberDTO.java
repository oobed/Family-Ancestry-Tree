package com.village.familytree.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class FamilyMemberDTO {
    private Long id;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private String villageRole;
    private String notes;
    private String photoUrl;
    private List<Long> parentIds;
    private List<Long> spouseIds;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(LocalDate deathDate) {
        this.deathDate = deathDate;
    }

    public String getVillageRole() {
        return villageRole;
    }

    public void setVillageRole(String villageRole) {
        this.villageRole = villageRole;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<Long> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<Long> parentIds) {
        this.parentIds = parentIds;
    }

    public List<Long> getSpouseIds() {
        return spouseIds;
    }

    public void setSpouseIds(List<Long> spouseIds) {
        this.spouseIds = spouseIds;
    }
}