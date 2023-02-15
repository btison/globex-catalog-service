package com.redhat.coolstore.service;

import com.redhat.coolstore.client.InventoryClient;
import com.redhat.coolstore.model.Product;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CatalogService {

    @Autowired
    private ProductRepository repository;

    //TODO: Autowire Inventory Client
    @Autowired
    private InventoryClient inventoryClient;

    public Product read(String id, boolean inventory) {
        Product product = repository.findById(id);
        if (product == null) {
            return null;
        }
        if (inventory) {
            JSONArray jsonArray = new JSONArray(inventoryClient.getInventoryStatus(product.getItemId()));
            List<String> quantity = IntStream.range(0, jsonArray.length())
                    .mapToObj(index -> ((JSONObject) jsonArray.get(index))
                            .optString("quantity")).collect(Collectors.toList());
            product.setQuantity(Integer.parseInt(quantity.get(0)));
        }
        return product;
    }

    public List<Product> readAll(boolean inventory) {
        List<Product> productList = repository.readAll();
        if (inventory) {
            addInventory(productList);
        }
        return productList;
    }

    public Page<Product> readAll(Pageable page, boolean inventory) {
        Page<Product> productPage = repository.readAll(page);
        if (inventory) {
            addInventory(productPage);
        }
        return productPage;
    }

    public List<Product> readByIds(List<String> ids, boolean inventory) {
        List<Product> productList = repository.findByIdList(ids);
        if (inventory) {
            addInventory(productList);
        }
        return productList;
    }

    public List<Product> readByCategories(List<String> categories, boolean inventory) {
        List<Product> productList = repository.findByCategoryList(categories);
        if (inventory) {
            addInventory(productList);
        }
        return productList;
    }

    private void addInventory(Iterable<Product> productList) {
        productList.forEach(p -> {
            JSONArray jsonArray = new JSONArray(this.inventoryClient.getInventoryStatus(p.getItemId()));
            List<String> quantity = IntStream.range(0, jsonArray.length())
                    .mapToObj(index -> ((JSONObject) jsonArray.get(index))
                            .optString("quantity")).collect(Collectors.toList());
            p.setQuantity(Integer.parseInt(quantity.get(0)));
        });
    }
}