package com.ktds.hi.store.infra.gateway;

import com.ktds.hi.store.biz.domain.Menu;
import com.ktds.hi.store.biz.usecase.out.MenuRepositoryPort;
import com.ktds.hi.store.infra.gateway.entity.MenuEntity;
import com.ktds.hi.store.infra.gateway.repository.MenuJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 메뉴 리포지토리 어댑터 클래스
 * Menu Repository Port를 구현하여 데이터 영속성 기능을 제공
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
public class MenuRepositoryAdapter implements MenuRepositoryPort {

    private final MenuJpaRepository menuJpaRepository;

    @Override
    public List<Menu> findMenusByStoreId(Long storeId) {
        return menuJpaRepository.findByStoreId(storeId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Menu> findMenuById(Long menuId) {
        return menuJpaRepository.findById(menuId)
                .map(this::toDomain);
    }

    @Override
    public Menu saveMenu(Menu menu) {
        MenuEntity entity = toEntity(menu);
        MenuEntity saved = menuJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteMenu(Long menuId) {
        menuJpaRepository.deleteById(menuId);
    }

    @Override
    public List<Menu> findAvailableMenusByStoreId(Long storeId) {
        return menuJpaRepository.findByStoreIdAndIsAvailableTrue(storeId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findMenusByStoreIdAndCategory(Long storeId, String category) {
        return menuJpaRepository.findByStoreIdAndCategoryAndIsAvailableTrue(storeId, category)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> saveMenus(List<Menu> menus) {
        List<MenuEntity> entities = menus.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());

        List<MenuEntity> savedEntities = menuJpaRepository.saveAll(entities);

        return savedEntities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMenusByStoreId(Long storeId) {
        menuJpaRepository.deleteByStoreId(storeId);
    }

    /**
     * Entity를 Domain으로 변환
     */
    private Menu toDomain(MenuEntity entity) {
        return Menu.builder()
                .id(entity.getId())
                .storeId(entity.getStoreId())
                .menuName(entity.getMenuName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .category(entity.getCategory())
                .imageUrl(entity.getImageUrl())
                .available(entity.getIsAvailable())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Domain을 Entity로 변환
     */
    private MenuEntity toEntity(Menu domain) {
        return MenuEntity.builder()
                .id(domain.getId())
                .storeId(domain.getStoreId())
                .menuName(domain.getMenuName())
                .description(domain.getDescription())
                .price(domain.getPrice())
                .category(domain.getCategory())
                .imageUrl(domain.getImageUrl())
                .isAvailable(domain.getIsAvailable())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}