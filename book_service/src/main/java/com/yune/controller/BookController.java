package com.yune.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yune.domain.Book;

import com.yune.domain.Inventory;
import com.yune.service.BookService;
import com.yune.unit.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
@Api("书籍相关接口")
@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;
    @ApiOperation("查询书籍列表")
    @GetMapping("/books")
    public IPage<Book> listBooks(@RequestParam int currentpage, @RequestParam int pagesize){
        return bookService.getBooksByPage(currentpage,pagesize);
    }
    @ApiOperation("根据id查询书籍")
    @PostMapping("/queryById")
    public Book qbI(@RequestBody int bookId){
        return bookService.queryById(bookId);
    }
    @ApiOperation("删除库存")
    @PutMapping("/stock/delete")
    public void DeleteStock(@RequestBody Inventory inventory){
        bookService.dropOneStock(inventory);
    }
    @ApiOperation("条件查询（书名，作者，题材）")
    @GetMapping("/books/search")
    public R selectByTittle(@RequestParam(value = "title",required = false) String title,
                            @RequestParam(value = "author",required = false) String author,
                            @RequestParam(value = "genre",required = false) String genre){
        return bookService.selectByTittle(title,author,genre);
    }

    //管理员可使用的接口
    @ApiOperation("Admin查询所有书籍")
    @CrossOrigin
    @GetMapping("/listbooks")
    public R listbooks(){
        List<Book> list = bookService.list();
        if(list.isEmpty()){
            return R.error("查询失败");
        }
        return R.ok(list);
    }
    @ApiOperation("Admin添加书籍")
    @CrossOrigin
    @PutMapping("/addNewBook")
    public R addnewbook(@RequestBody Book book){
        if(bookService.save(book)){
            return R.ok();
        }else return R.error("添加失败");
    }
    @ApiOperation("Admin删除书籍")
    @CrossOrigin
    @PutMapping("/deleteBooks")
    public R deleteBooks(@RequestParam("ids") List<Integer> ids){
        AtomicBoolean flag = new AtomicBoolean(true);
        ids.forEach(bookId->{
           QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
           queryWrapper.eq("book_id",bookId);
           boolean remove = bookService.remove(queryWrapper);
            flag.set(flag.get() && remove); // 进行与运算
        });
        if(flag.get()){
            return R.ok();
        }
        else return R.error("删除失败");
    }
    @ApiOperation("Admin更新书籍")
    @CrossOrigin
    @PutMapping("/updateAbook")
    public R updateAbook(@RequestBody Book book){
        boolean b = bookService.updateById(book);
        if (b){
            return R.ok();
        }else return R.error("更新失败");
    }


}
