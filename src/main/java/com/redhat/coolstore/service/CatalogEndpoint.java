package com.redhat.coolstore.service;

import com.redhat.coolstore.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/services")
public class CatalogEndpoint {

    private final CatalogService catalogService;

    public CatalogEndpoint(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/products")
    public Page<Product> readAll(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit, @RequestParam(required = false) Boolean inventory) {
        boolean inv = inventory == null || inventory;
        if (limit == null) {
            List<Product> products = catalogService.readAll(inv);
            return new PageImpl<>(products, PageRequest.of(0, products.size()), products.size());
        } else {
            if (page == null) {
                page = 0;
            } else {
                page = page -1;
            }
            return catalogService.readAll(PageRequest.of(page, limit), inv);
        }
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> read(@PathVariable("id") String id, @RequestParam(required = false) Boolean inventory) {
        boolean inv = inventory == null || inventory;
        Product product = catalogService.read(id, inv);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/product/list/{ids}")
    public List<Product> readList(@PathVariable("ids") List<String> ids, @RequestParam(required = false) Boolean inventory) {
        boolean inv = inventory == null || inventory;
        return catalogService.readByIds(ids, inv);
    }

    @GetMapping("/product/category/{categories}")
    public List<Product> readCategories(@PathVariable("categories") List<String> categories, @RequestParam(required = false) Boolean inventory) {
        boolean inv = inventory == null || inventory;
        return catalogService.readByCategories(categories, inv);
    }

}