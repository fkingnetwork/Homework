package com.yune.service.Impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yune.domain.Book;
import com.yune.domain.Inventory;
import com.yune.mapper.BookMapper;
import com.yune.service.BookService;
import com.yune.unit.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService{
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String key = "books";

    private Long total = 0L;

    @Override
    public IPage<Book> getBooksByPage(int currentPage, int pageSize) {


        // 构建分页对象
        Page<Book> page = new Page<>(currentPage, pageSize);

        // Redis 存储的键
        String redisKey = key + ":page:" + currentPage + ":" + pageSize;

        // 尝试从 Redis 中获取数据
        String cachedBooksJson = redisTemplate.opsForValue().get(redisKey);
        List<Book> books;

        if (cachedBooksJson != null) {
            // 从 Redis 获取数据并反序列化为 Book 列表
            books = JSONUtil.toList(new JSONArray(cachedBooksJson), Book.class);
            // 设置分页结果
            page.setRecords(books);
            // 此处不设置 total，因为从缓存中获取数据，无法知道总数
            page.setTotal(Integer.parseInt(redisTemplate.opsForValue().get("total")));
        } else {
            // 如果 Redis 中没有数据，则进行数据库查询
            page = this.page(page); // 使用 MyBatis-Plus 分页查询
//            total=page.getTotal();
            redisTemplate.opsForValue().set("total",StrUtil.toString(page.getTotal()));

            books = page.getRecords();


            // 将结果存入 Redis，并设置过期时间（例如 1 小时）
            redisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(books), 1, TimeUnit.HOURS);
        }

        return page;
    }


    @Override
    public Book queryById(int bookId) {
        return bookMapper.selectoneBook(bookId);
    }

    //使用乐观锁防止超扣
    @Override
    public void dropOneStock(Inventory inventory) {
        update().setSql("stock= stock-"+inventory.getNum())
                .eq("book_id",inventory.getBookId())
                .gt("stock",0)
                .update();
    }

    @Override
    public R selectByTittle(String title,String author,String genre) {

        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();


        if (title != null && !title.isEmpty() ) {
            queryWrapper.like("title", title);
        }

        if (author != null && !author.isEmpty()) {
            queryWrapper.like("author", author);
        }
        if (genre != null && !genre.isEmpty()) {
            queryWrapper.like("genre", genre);
        }
        List<Book> list = list(queryWrapper);
        return R.ok(list);
    }


}
