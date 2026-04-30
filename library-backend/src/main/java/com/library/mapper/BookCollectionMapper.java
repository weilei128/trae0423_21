package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BookCollection;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookCollectionMapper extends BaseMapper<BookCollection> {
}
