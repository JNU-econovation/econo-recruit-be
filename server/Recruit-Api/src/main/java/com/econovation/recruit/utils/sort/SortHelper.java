package com.econovation.recruit.utils.sort;

import static com.econovation.recruitcommon.consts.RecruitStatic.PAGE_SIZE;

import com.econovation.recruit.utils.sort.strategy.SortStrategy;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Component
public class SortHelper<T> {
    private SortStrategy<T> sortStrategy;
    private final Map<Class, Map<String, SortStrategy<?>>> sortStrategies;

    public <T> void sort(List<T> data, String sortType) {

        Class<?> dataType = data.get(0).getClass();
        Map<String, SortStrategy<?>> strategies = sortStrategies.get(dataType);
        if (sortType == null || sortType.isEmpty() || strategies == null || strategies.isEmpty()) {
            // 데이터 타입에 대한 정렬 전략이 없을 경우 기본 정렬 전략을 사용
            sortType = "newest";
        }

        SortStrategy<T> sortStrategy = (SortStrategy<T>) strategies.get(sortType);
        if (sortStrategy == null) {
            sortStrategy = (SortStrategy<T>) strategies.get("newest");
        }

        data.sort(sortStrategy::compare);
    }

    @Autowired
    public SortHelper(List<SortStrategy<?>> sortStrategiesList) {
        this.sortStrategies = new HashMap<>();
        for (SortStrategy<?> strategy : sortStrategiesList) {
            Class<?> dataType = resolveGenericType(strategy.getClass());
            sortStrategies.computeIfAbsent(dataType, k -> new HashMap<>());
            Qualifier qualifier =
                    AnnotationUtils.findAnnotation(strategy.getClass(), Qualifier.class);
            if (qualifier != null) {
                String sortType = resolveSortType(qualifier.value());
                sortStrategies.get(dataType).put(sortType, strategy);
            } else {
                throw new IllegalArgumentException(
                        "@Qualifier Annotation(sorting strategy bean)을 찾을 수 없습니다.");
            }
        }
    }

    public static <T> List<T> paginateList(List<T> list, Integer page) {
        if (page == null || page < 1) {
            page = 1;
        }
        int startIndex = (page - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, list.size());

        return startIndex < endIndex ? list.subList(startIndex, endIndex) : Collections.emptyList();
    }

    private Class<?> resolveGenericType(Class<?> clazz) {
        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericInterfaces()[0];
        return (Class<?>) parameterizedType.getActualTypeArguments()[0];
    }

    private String resolveSortType(String qualifierValue) {
        // 대문자로 시작하는 첫 번째 단어를 추출하여 정렬 타입으로 사용
        Matcher matcher = Pattern.compile("[A-Z][a-z]*").matcher(qualifierValue);
        String s = matcher.find() ? matcher.group() : "";
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }
    // 지원서 정렬 데이터
    public int compare(T obj1, T obj2) {
        return sortStrategy.compare(obj1, obj2);
    }
}
