package com.aldrin.foodfam.controller.api;

import com.aldrin.foodfam.dto.RecipeRequest;
import com.aldrin.foodfam.exception.ResourceNotFoundException;
import com.aldrin.foodfam.model.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import com.aldrin.foodfam.repository.RecipeRepository;
import com.aldrin.foodfam.service.impl.RecipeServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

@RestController()
@RequestMapping("/api/recipe/")
public class APIRecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeServiceImpl recipeServiceEmpl;

    @GetMapping("/list")
    public ResponseEntity<List<Recipe>> findAllRecipe() {
        return new ResponseEntity<List<Recipe>>(recipeRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Recipe> addRecipe(@Valid @RequestBody RecipeRequest request) {
        return new ResponseEntity<>(recipeRepository.save(request.getRecipe()), HttpStatus.CREATED);
    }


    @GetMapping("/findById/{id}")
    public ResponseEntity getRecipeById(@PathVariable(value = "id") Integer id) {
        return recipeRepository.findById(id).map(recipe -> ResponseEntity.ok(recipe)).orElseThrow(() -> new ResourceNotFoundException("Recipe with id : " + id + " not found")
        );
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<Recipe> getEmployeeInfo(@PathVariable Integer id) {
        return new ResponseEntity<Recipe>(recipeServiceEmpl.findById(id), HttpStatus.OK);
    }

    // http://localhost:8080/recipe/findByIngredients?ingredients=Tomato,+Potato,+Eggplant,+Beef
    @GetMapping("/findByIngredients")
    public ResponseEntity<List<Recipe>> findByIngredients(@RequestParam List<String> ingredients) {
        List<Recipe> result = new ArrayList<>();
        for (String keyword : ingredients) {
            for (Recipe recipe : recipeRepository.findByDescriptionContaining(keyword)) {
                if (!result.contains(recipe)) {
                    result.add(recipe);
                }
            }
        }
        return new ResponseEntity<List<Recipe>>(result, HttpStatus.OK);
    }

    //http://localhost:8080/recipe/search?keyword=Green
    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipeByKeyword(@RequestParam String keyword) {
        return new ResponseEntity<List<Recipe>>(recipeServiceEmpl.getTitleSortByKeyword(keyword), HttpStatus.OK);
    }

    @PutMapping("/recipe/update/{id}")
    public Recipe updateRecipe(@PathVariable(value = "id") Integer id, @RequestBody RecipeRequest request) {
        return recipeRepository.findById(id).map(author -> {
            author.setDescription(request.getRecipe().getDescription());
            author.setInformation(request.getRecipe().getInformation());
            author.setInstruction(request.getRecipe().getInstruction());
            return recipeRepository.save(author);
        }).orElseThrow(() -> new ResourceNotFoundException("Recipe with id : " + id + " not found"));
    }

    @DeleteMapping("/recipe/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable(value = "id") Integer id) {
        return recipeRepository.findById(id).map(author -> {
            recipeRepository.delete(author);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Recipe with id : " + id + " not found"));
    }
}
