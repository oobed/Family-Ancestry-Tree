package com.village.familytree.model;

import java.util.List;
import lombok.Data;

@Data
public class FamilyTreeResponse {
    private List<FamilyMember> members;
    private List<FamilyMember> rootMembers;

    public FamilyTreeResponse(List<FamilyMember> members, List<FamilyMember> rootMembers) {
        this.members = members;
        this.rootMembers = rootMembers;
    }
}