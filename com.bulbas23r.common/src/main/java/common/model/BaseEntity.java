package common.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@RequiredArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted;

    private LocalDateTime deletedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    private String deletedBy;

    @PreUpdate
    public void setDeleted() {
        if (isDeleted) {
            deletedAt = LocalDateTime.now();

            // TODO SecurityContextHolder 해결하기
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            if (authentication == null || !authentication.isAuthenticated()) {
//                deletedBy = "SYSTEM";
//                return;
//            }
//
//            Object principal = authentication.getPrincipal();
//
//            if (principal instanceof UserDetails) {
//                deletedBy = ((UserDetails) principal).getUsername();
//                return;
//            }
//
//            deletedBy = "UNKNOWN";
        }
    }

}
