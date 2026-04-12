package com.dna.fooo_guard.domain.group.entity;

import java.util.ArrayList;
import java.util.List;

import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.userGroup.entity.UserGroup;
import com.dna.fooo_guard.global.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더가 내부적으로 쓸 생성자
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 객체 생성할 때 쓸 생성자
@Table(name = "groups", comment = "공유 냉장고 그룹 테이블")
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, comment = "그룹 고유 식별자(PK)")
    private Long id;

    @Column(name = "name", nullable = false, length = 20, comment = "그룹 이름")
    private String name;

    @Column(name = "description", length = 255, comment = "그룹 설명")
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "그룹 관리자 PK")
    private User manager;

    @Builder.Default
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<UserGroup> members = new ArrayList<>();

    public void addMember(User user) {
        UserGroup userGroup = UserGroup.builder()
                .user(user)
                .group(this)
                .build();
        this.members.add(userGroup);
        user.getGroups().add(userGroup);
        System.out.println("addMember: " + user.getId() + " added to group " + this.id);
    }
}
