package com.village.familytree.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "family_members")
@Data
@NoArgsConstructor
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @Column(name = "village_role")
    private String villageRole;

    @Column(length = 1000)
    private String notes;

    @Column(name = "photo_url")
    private String photoUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "family_relationships",
            joinColumns = @JoinColumn(name = "child_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id")
    )
    private List<FamilyMember> parents = new ArrayList<>();

    @ManyToMany(mappedBy = "parents", fetch = FetchType.LAZY)
    private List<FamilyMember> children = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "marriages",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "spouse_id")
    )
    private List<FamilyMember> spouses = new ArrayList<>();

    @Transient
    private int generation;

    // Constructor
    public FamilyMember(String name, String gender, LocalDate birthDate) {
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
    }

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

    public List<FamilyMember> getParents() {
        return parents;
    }

    public void setParents(List<FamilyMember> parents) {
        this.parents = parents;
    }

    public List<FamilyMember> getChildren() {
        return children;
    }

    public void setChildren(List<FamilyMember> children) {
        this.children = children;
    }

    public List<FamilyMember> getSpouses() {
        return spouses;
    }

    public void setSpouses(List<FamilyMember> spouses) {
        this.spouses = spouses;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }
}