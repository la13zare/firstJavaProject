package com.example.crud.controllers;

import com.example.crud.models.ProductDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import com.example.crud.models.Product;
import com.example.crud.services.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.sound.midi.Patch;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping({"", "/"})
    public String list(Model model) {
        List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";

    }

    @GetMapping("create")
    public String create(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/create";
    }

    @PostMapping("/create")
    public String save(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {

        if (productDto.getImage().isEmpty()) {

            result.addError( new FieldError("productDto", "image", "Image is required"));

        }

        if (result.hasErrors()) {
            return "products/create";
        }

        // save image
        MultipartFile image = productDto.getImage();
        Date createdAt = new Date();
        String storeFileName = createdAt + "_" + image.getOriginalFilename();

        try {

            String uploadDir = "public/images/products/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {

                Files.createDirectories(uploadPath);

            }

            try (InputStream inputstream = image.getInputStream()) {

                Files.copy(inputstream, Paths.get(uploadDir + storeFileName));

            }


        } catch (Exception e) {

            System.out.println(e.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setCreatedAt(createdAt);
        product.setUpdatedAt(createdAt);
        product.setImage(storeFileName);

        productRepository.save(product);

        return "redirect:/products";

    }

    @GetMapping("edit")
    public String edit(Model model, @RequestParam int id) {

        try {

            Product product = productRepository.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setDescription(product.getDescription());
            productDto.setPrice(product.getPrice());

            model.addAttribute("productDto", productDto);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "redirect:/products";
        }

        return "products/edit";
    }

    @PostMapping("/edit")
    public String update(Model model, @RequestParam int id, @Valid @ModelAttribute ProductDto productDto, BindingResult result) {

        try {

            Product product = productRepository.findById(id).get();
            model.addAttribute("product", product);

            if (result.hasErrors()) {
                return "products/edit";
            }

            Date updatedAt = new Date();

            if (!productDto.getImage().isEmpty()) {

                String uploadDir = "public/images/products/";
                Path oldImagePath = Paths.get(uploadDir + product.getImage());

                try {

                    Files.delete(oldImagePath);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                // save new image
                MultipartFile image = productDto.getImage();
                String storeFileName = updatedAt + "_" + image.getOriginalFilename();

                try (InputStream inputstream = image.getInputStream()) {

                    Files.copy(inputstream, Paths.get(uploadDir + storeFileName), StandardCopyOption.REPLACE_EXISTING);

                }
                product.setImage(storeFileName);

            }

            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setPrice(productDto.getPrice());
            product.setUpdatedAt(updatedAt);

            productRepository.save(product);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/products";

    }

    @GetMapping("delete")
    public String delete(@RequestParam int id) {

        try {

            Product product = productRepository.findById(id).get();

            // delete image
            Path imagePath = Paths.get("public/images/products/" + product.getImage());

            try {

                Files.delete(imagePath);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            // delete product
            productRepository.delete(product);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/products";

    }


}
