package com.village.familytree.model;

import java.util.List;
import lombok.Data;

@Data
public class FamilyTreeResponse {
    private List<FamilyMemberDTO> members;
    private List<FamilyMemberDTO> rootMembers;

    public FamilyTreeResponse(List<FamilyMemberDTO> members, List<FamilyMemberDTO> rootMembers) {
        this.members = members;
        this.rootMembers = rootMembers;
    }
}