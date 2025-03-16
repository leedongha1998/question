package common.utils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import common.model.BaseEntity;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtils {

    public static Pageable pageable(int page, int size) {
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        return PageRequest.of(page, size);
    }

    public static OrderSpecifier<?> getCommonOrderSpecifier(
        EntityPathBase<? extends BaseEntity> qClass,
        Sort.Direction sortDirection,
        CommonSortBy sortBy) {
        PathBuilder<BaseEntity> entityPath = new PathBuilder<>(BaseEntity.class,
            qClass.getMetadata());

        switch (sortBy) {
            case CREATED_AT -> {
                return sortDirection == Sort.Direction.ASC
                    ? entityPath.getDateTime("createdAt", LocalDateTime.class).asc()
                    : entityPath.getDateTime("createdAt", LocalDateTime.class).desc();
            }
            case UPDATED_AT -> {
                return sortDirection == Sort.Direction.ASC
                    ? entityPath.getDateTime("updatedAt", LocalDateTime.class).asc()
                    : entityPath.getDateTime("updatedAt", LocalDateTime.class).desc();
            }
            default -> throw new IllegalArgumentException("지원되지 않는 정렬 조건: " + sortBy);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum CommonSortBy {
        CREATED_AT("createdAt"),
        UPDATED_AT("updatedAt");

        private final String value;

    }
}
