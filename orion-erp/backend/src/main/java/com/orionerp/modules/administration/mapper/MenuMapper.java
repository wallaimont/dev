package com.orionerp.modules.administration.mapper;

import com.orionerp.modules.administration.domain.Menu;
import com.orionerp.modules.administration.dto.MenuDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    @Mapping(target = "parentId", expression = "java(menu.getParent() != null ? menu.getParent().getId() : null)")
    MenuDto toDto(Menu menu);
}
