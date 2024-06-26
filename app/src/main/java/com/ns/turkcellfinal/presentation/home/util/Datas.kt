package com.ns.turkcellfinal.presentation.home.util

import android.content.res.Resources
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.ns.turkcellfinal.R

fun setCategoriesChips(
    resources: Resources
): List<Category> {
    return listOf(
        Category(R.drawable.ic_beauty, "Beauty", Color.parseColor("#ECFDF5"), "beauty"),
        Category(
            R.drawable.ic_fragrances,
            "Fragrances",
            Color.parseColor("#FFEDED"),
            "fragrances"
        ),
        Category(
            R.drawable.ic_furniture,
            "Furniture",
            Color.parseColor("#ECFDF5"),
            "furniture"
        ),
        Category(
            R.drawable.ic_groceries,
            "Groceries",
            Color.parseColor("#FFF7ED"),
            "groceries"
        ),
        Category(
            R.drawable.ic_home_decoration,
            "Home\nDecoration",
            Color.parseColor("#FFF7ED"),
            "home-decoration"
        ),
        Category(
            R.drawable.ic_kitchen,
            "Kitchen\nAccessories",
            ResourcesCompat.getColor(resources, R.color.chip_gray_bg, null),
            "kitchen-accessories"
        ),
        Category(
            R.drawable.ic_laptop,
            "Laptops",
            ResourcesCompat.getColor(resources, R.color.chip_orange_bg, null),
            "laptops"
        ),
        Category(
            R.drawable.ic_mens_shirt,
            "Men's\nShirts",
            ResourcesCompat.getColor(resources, R.color.chip_blue_bg, null),
            "mens-shirts"
        ),
        Category(
            R.drawable.ic_men_watch,
            "Men's\nWatches",
            ResourcesCompat.getColor(resources, R.color.chip_blue_bg, null),
            "mens-watches"
        ),
        Category(
            R.drawable.ic_mobile_accessories,
            "Mobile\nAccessories",
            ResourcesCompat.getColor(resources, R.color.chip_gray_bg, null),
            "mobile-accessories"
        ),
        Category(
            R.drawable.ic_motorcycle,
            "Motorcycle",
            ResourcesCompat.getColor(resources, R.color.chip_green_bg, null),
            "motorcycle"
        ),
        Category(
            R.drawable.ic_skin_care,
            "Skin\nCare",
            ResourcesCompat.getColor(resources, R.color.chip_orange_bg, null),
            "skin-care"
        ),
        Category(
            R.drawable.ic_smartphone,
            "Smartphones",
            ResourcesCompat.getColor(resources, R.color.chip_gray_bg, null),
            "smartphones"
        ),
        Category(
            R.drawable.ic_sport,
            "Sports\nAccessories",
            ResourcesCompat.getColor(resources, R.color.chip_green_bg, null),
            "sports-accessories"
        ),
        Category(
            R.drawable.ic_sunglasses,
            "Sunglasses",
            ResourcesCompat.getColor(resources, R.color.chip_gray_bg, null),
            "sunglasses"
        ),
        Category(
            R.drawable.ic_tablet,
            "Tablets",
            ResourcesCompat.getColor(resources, R.color.chip_green_bg, null),
            "tablets"
        ),
        Category(
            R.drawable.ic_tops,
            "Tops",
            ResourcesCompat.getColor(resources, R.color.chip_blue_bg, null),
            "tops"
        ),
        Category(
            R.drawable.ic_vehicle,
            "Vehicle",
            ResourcesCompat.getColor(resources, R.color.chip_green_bg, null),
            "vehicle"
        ),
        Category(
            R.drawable.ic_women_bags,
            "Women's\nBags",
            ResourcesCompat.getColor(resources, R.color.chip_red_bg, null),
            "womens-bags"
        ),
        Category(
            R.drawable.ic_tops,
            "Women's\nDresses",
            ResourcesCompat.getColor(resources, R.color.chip_green_bg, null),
            "womens-dresses"
        ),
        Category(
            R.drawable.ic_jewellery,
            "Women's\nJewellery",
            ResourcesCompat.getColor(resources, R.color.chip_yellow_bg, null),
            "womens-jewellery"
        ),
        Category(
            R.drawable.ic_women_shoes,
            "Women's\nShoes",
            ResourcesCompat.getColor(resources, R.color.chip_red_bg, null),
            "womens-shoes"
        ),
        Category(
            R.drawable.ic_watch_women,
            "Women's\nWatches",
            ResourcesCompat.getColor(resources, R.color.chip_orange_bg, null),
            "womens-watches"
        )
    )
}


data class Category(val iconResId: Int, val text: String, val color: Int, val slug: String)

fun Int.dpToPx(resources: Resources): Int {
    return (this * resources.displayMetrics.density).toInt()
}
