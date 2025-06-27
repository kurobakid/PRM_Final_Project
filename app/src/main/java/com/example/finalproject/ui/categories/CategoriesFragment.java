package com.example.finalproject.ui.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.adapter.CategoriesAdapter;
import com.example.finalproject.model.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {
    private RecyclerView recyclerViewCategories;
    private CategoriesAdapter categoriesAdapter;
    private List<Category> categories = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);

        // Mock categories data
        categories.add(new Category("Phones", R.drawable.ic_launcher_foreground));
        categories.add(new Category("Laptops", R.drawable.ic_launcher_foreground));
        categories.add(new Category("Tablets", R.drawable.ic_launcher_foreground));
        categories.add(new Category("Accessories", R.drawable.ic_launcher_foreground));
        categories.add(new Category("Smartwatches", R.drawable.ic_launcher_foreground));
        categories.add(new Category("Headphones", R.drawable.ic_launcher_foreground));

        categoriesAdapter = new CategoriesAdapter(categories, category -> {
            // TODO: Navigate to category products
            Toast.makeText(getContext(), "Selected: " + category.getName(), Toast.LENGTH_SHORT).show();
        });
        recyclerViewCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewCategories.setAdapter(categoriesAdapter);
        return view;
    }
} 