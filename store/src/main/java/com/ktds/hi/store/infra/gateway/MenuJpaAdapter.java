package com.ktds.hi.store.infra.gateway;

import com.ktds.hi.store.domain.Menu;
import com.ktds.hi.store.biz.usecase.out.MenuRepositoryPort;
import com.ktds.hi.store.infra.gateway.entity.MenuEntity;
import com.ktds.hi.store.infra.gateway.repository.MenuJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 메뉴 JPA 어댑터
 * 메뉴 리포지토리 포트의 JPA 구현체
 *
 * @author 하이오더 개발팀
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
public class MenuJpaAdapter implements MenuRepositoryPort {

    private final MenuJpaRepository menuJpaRepository;

    @Override
    public List<Menu> findMenusByStoreId(Long storeId) {
        List<MenuEntity> entities = menuJpaRepository.findByStoreId(storeId);
        return entities.stream()
                .map(MenuEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Menu> findMenuById(Long menuId) {
        return menuJpaRepository.findById(menuId)
                .map(MenuEntity::toDomain);
    }

    @Override
    public Menu saveMenu(Menu menu) {
        MenuEntity entity = MenuEntity.fromDomain(menu);
        MenuEntity savedEntity = menuJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public void deleteMenu(Long menuId) {
        menuJpaRepository.deleteById(menuId);
    }

    @Override
    public List<Menu> findAvailableMenusByStoreId(Long storeId) {
        List<MenuEntity> entities = menuJpaRepository.findByStoreIdAndIsAvailableTrue(storeId);
        return entities.stream()
                .map(MenuEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findMenusByStoreIdAndCategory(Long storeId, String category) {
        List<MenuEntity> entities = menuJpaRepository.findByStoreIdAndCategoryAndIsAvailableTrue(storeId, category);
        return entities.stream()
                .map(MenuEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> saveMenus(List<Menu> menus) {
        List<MenuEntity> entities = menus.stream()
                .map(MenuEntity::fromDomain)
                .collect(Collectors.toList());

        List<MenuEntity> savedEntities = menuJpaRepository.saveAll(entities);

        return savedEntities.stream()
                .map(MenuEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMenusByStoreId(Long storeId) {
        menuJpaRepository.deleteByStoreId(storeId);
    }
}
