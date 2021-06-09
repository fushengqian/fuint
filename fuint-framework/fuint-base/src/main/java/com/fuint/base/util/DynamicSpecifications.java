package com.fuint.base.util;

import com.fuint.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 动态SQL拼接
 *
 * @author fsq
 * @version $Id: DynamicSpecifications.java, v 0.1 2015年10月26日 上午11:24:35 fsq Exp $
 */
public class DynamicSpecifications {

    private static final Logger logger = LoggerFactory.getLogger(DynamicSpecifications.class);

    public static <T> Specification<T> groupBySearchFilter(Root<T> root, final Collection<SearchFilter> filters,
                                                           final Class<T> entityClazz, final String groupField) {
        return new Specification<T>() {
            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
                                         CriteriaBuilder builder) {
                if (filters != null && filters.size() > 0) {
                    List<Predicate> predicates = new ArrayList<Predicate>();
                    for (SearchFilter filter : filters) {
                        Path expression = null;
                        if (StringUtil.split(filter.fieldName, ".").length <= 1) {//如果是单属性查询
                            expression = root.get(filter.fieldName);
                        } else {
                            try {
                                String[] fieldName = StringUtil.split(filter.fieldName, ".");
                                if (fieldName.length > 3) {
                                    logger.error("关联对象属性查询,filter设置关联不能超过三层");
                                    continue;
                                }
                                Join join = null;
                                for (int i = 0; i < fieldName.length - 1; i++) {
                                    if (null != join) {
                                        join = join.join(fieldName[i]);
                                    } else {
                                        join = root.join(fieldName[i]);
                                    }
                                }
                                if (null != join) {
                                    expression = join.get(fieldName[fieldName.length - 1]);
                                }
                            } catch (Exception e) {
                                logger.error("关联对象属性查询,filter设置错误:{}", e);
                                continue;
                            }
                        }
                        // logic operator
                        switch (filter.operator) {
                            case EQ:
                                predicates.add(builder.equal(expression, filter.value));
                                break;
                            case LIKE:
                                predicates.add(builder.like(expression, "%" + filter.value + "%"));
                                break;
                            case GT:
                                predicates.add(builder.greaterThan(expression,
                                        (Comparable) filter.value));
                                break;
                            case LT:
                                predicates.add(builder.lessThan(expression,
                                        (Comparable) filter.value));
                                break;
                            case GTE:
                                predicates.add(builder.greaterThanOrEqualTo(expression,
                                        (Comparable) filter.value));
                                break;
                            case LTE:
                                predicates.add(builder.lessThanOrEqualTo(expression,
                                        (Comparable) filter.value));
                                break;
                            case IN:
                                CriteriaBuilder.In in = builder.in(expression);
                                for (Object obj : filter.array) {
                                    in.value(obj);
                                }
                                predicates.add(in);
                                break;
                            case NQ:
                                predicates.add(builder.notEqual(expression, (Comparable) filter.value));
                                break;
                        }
                    }

                    // 将所有条件用 and 联合起来
                    if (!predicates.isEmpty()) {
                        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                    }
                }
                query.multiselect(root.get(groupField));
                query.groupBy(root.get(groupField));
                return query.getRestriction();
            }
        };
    }


    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters,
                                                      final Class<T> entityClazz) {
        return new Specification<T>() {
            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
                                         CriteriaBuilder builder) {
                if (filters != null && filters.size() > 0) {
                    List<Predicate> predicates = new ArrayList<Predicate>();
                    for (SearchFilter filter : filters) {
                        Path expression = null;
                        if (StringUtil.split(filter.fieldName, ".").length <= 1) {//如果是单属性查询
                            expression = root.get(filter.fieldName);
                        } else {
                            try {
                                String[] fieldName = StringUtil.split(filter.fieldName, ".");
                                if (fieldName.length > 3) {
                                    logger.error("关联对象属性查询,filter设置关联不能超过三层");
                                    continue;
                                }
                                Join join = null;
                                for (int i = 0; i < fieldName.length - 1; i++) {
                                    if (null != join) {
                                        join = join.join(fieldName[i]);
                                    } else {
                                        join = root.join(fieldName[i]);
                                    }
                                }
                                if (null != join) {
                                    expression = join.get(fieldName[fieldName.length - 1]);
                                }
                            } catch (Exception e) {
                                logger.error("关联对象属性查询,filter设置错误:{}", e);
                                continue;
                            }
                        }
                        // logic operator
                        switch (filter.operator) {
                            case EQ:
                                predicates.add(builder.equal(expression, filter.value));
                                break;
                            case LIKE:
                                predicates.add(builder.like(expression, "%" + filter.value + "%"));
                                break;
                            case GT:
                                predicates.add(builder.greaterThan(expression,
                                        (Comparable) filter.value));
                                break;
                            case LT:
                                predicates.add(builder.lessThan(expression,
                                        (Comparable) filter.value));
                                break;
                            case GTE:
                                predicates.add(builder.greaterThanOrEqualTo(expression,
                                        (Comparable) filter.value));
                                break;
                            case LTE:
                                predicates.add(builder.lessThanOrEqualTo(expression,
                                        (Comparable) filter.value));
                                break;
                            case IN:
                                CriteriaBuilder.In in = builder.in(expression);
                                for (Object obj : filter.array) {
                                    in.value(obj);
                                }
                                predicates.add(in);
                                break;
                            case NQ:
                                predicates.add(builder.notEqual(expression, (Comparable) filter.value));
                                break;
                        }
                    }

                    // 将所有条件用 and 联合起来
                    if (!predicates.isEmpty()) {
                        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                    }
                }
                return builder.conjunction();
            }
        };
    }
}
