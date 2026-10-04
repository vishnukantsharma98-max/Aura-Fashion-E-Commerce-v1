package com.example.data

import com.example.model.CartItem
import com.example.model.Category
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.Product
import com.example.model.ProductColor
import com.example.model.ShippingAddress
import com.example.model.UserProfile

object MockData {

    val categories = listOf(
        Category(
            id = "cat_all",
            name = "All",
            imageUrl = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=600&q=80",
            itemCount = 28,
            description = "Browse our full luxury catalogue",
            displayOrder = 0
        ),
        Category(
            id = "cat_women",
            name = "Women",
            imageUrl = "https://images.unsplash.com/photo-1483985988355-763728e1935b?w=600&q=80",
            itemCount = 12,
            description = "Elegance tailored for every modern silhouette",
            displayOrder = 1
        ),
        Category(
            id = "cat_men",
            name = "Men",
            imageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=600&q=80",
            itemCount = 10,
            description = "Sharp tailoring and effortless casual staples",
            displayOrder = 2
        ),
        Category(
            id = "cat_dresses",
            name = "Dresses",
            imageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=600&q=80",
            itemCount = 4,
            description = "From daytime slip dresses to evening silhouettes",
            displayOrder = 3
        ),
        Category(
            id = "cat_shirts",
            name = "Shirts",
            imageUrl = "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&q=80",
            itemCount = 4,
            description = "Crisp poplin, breathable linen and tailored shirts",
            displayOrder = 4
        ),
        Category(
            id = "cat_tshirts",
            name = "T-Shirts",
            imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600&q=80",
            itemCount = 4,
            description = "Heavyweight organic cotton and minimalist tees",
            displayOrder = 5
        ),
        Category(
            id = "cat_jeans",
            name = "Jeans",
            imageUrl = "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=600&q=80",
            itemCount = 4,
            description = "Authentic selvedge denim and relaxed cuts",
            displayOrder = 6
        ),
        Category(
            id = "cat_jackets",
            name = "Jackets",
            imageUrl = "https://images.unsplash.com/photo-1544441893-675973e31985?w=600&q=80",
            itemCount = 5,
            description = "Coats, trenches, leather and tailored blazers",
            displayOrder = 7
        ),
        Category(
            id = "cat_shoes",
            name = "Shoes",
            imageUrl = "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=600&q=80",
            itemCount = 4,
            description = "Handcrafted brogues, sneakers, boots and pumps",
            displayOrder = 8
        ),
        Category(
            id = "cat_accessories",
            name = "Accessories",
            imageUrl = "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&q=80",
            itemCount = 4,
            description = "Sculptural leather bags, sunglasses and jewelry",
            displayOrder = 9
        )
    )

    val colorBlack = ProductColor("Onyx Black", 0xFF1C1C1E)
    val colorCream = ProductColor("Alabaster Cream", 0xFFF7F5F0)
    val colorCamel = ProductColor("Warm Camel", 0xFFC19A6B)
    val colorSage = ProductColor("Muted Sage", 0xFF7D8C7C)
    val colorNavy = ProductColor("Midnight Navy", 0xFF1B263B)
    val colorTerracotta = ProductColor("Terracotta", 0xFFC86D51)
    val colorCharcoal = ProductColor("Charcoal", 0xFF3E424B)
    val colorOlive = ProductColor("Olive Drab", 0xFF556B2F)
    val colorWine = ProductColor("Bordeaux Wine", 0xFF58111A)
    val colorIndigo = ProductColor("Indigo Denim", 0xFF2B3A67)

    val products = listOf(
        // 1. Jackets
        Product(
            id = "prod_1",
            name = "Cashmere Blend Oversized Trench",
            description = "A quintessential timeless statement piece crafted from double-faced Italian wool-cashmere blend. Features horn buttons, detachable waist belt, and storm flap for effortless transitional dressing.",
            categoryId = "cat_jackets",
            categoryName = "Jackets",
            price = 12999.0,
            originalPrice = 15999.0,
            imageUrl = "https://images.unsplash.com/photo-1539533018447-63fcce2678e3?w=800&q=80",
            additionalImageUrls = listOf(
                "https://images.unsplash.com/photo-1544441893-675973e31985?w=800&q=80"
            ),
            rating = 4.9,
            reviewCount = 128,
            availableSizes = listOf("XS", "S", "M", "L", "XL"),
            availableColors = listOf(colorCamel, colorBlack, colorSage),
            stockQuantity = 14,
            isFeatured = true,
            isPopular = true
        ),

        // 2. Jackets / Women
        Product(
            id = "prod_2",
            name = "Structured Silk Crepe Blazer",
            description = "Impeccably tailored single-breasted blazer in fluid silk crepe. Sharp peak lapels with subtle shoulder pads create an empowering structured drape from boardroom to evening dinner.",
            categoryId = "cat_jackets",
            categoryName = "Jackets",
            price = 7999.0,
            originalPrice = 9999.0,
            imageUrl = "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=800&q=80",
            rating = 4.8,
            reviewCount = 94,
            availableSizes = listOf("S", "M", "L"),
            availableColors = listOf(colorBlack, colorCream, colorTerracotta),
            stockQuantity = 20,
            isFeatured = true,
            isPopular = true
        ),

        // 3. Women
        Product(
            id = "prod_3",
            name = "Merino Wool Ribbed Turtleneck",
            description = "Ultra-fine Australian merino wool knitted in a delicate tactile rib. Breathable, thermal-regulating, and exceptionally soft against the skin.",
            categoryId = "cat_women",
            categoryName = "Women",
            price = 3499.0,
            originalPrice = 4299.0,
            imageUrl = "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=800&q=80",
            rating = 4.7,
            reviewCount = 210,
            availableSizes = listOf("XS", "S", "M", "L"),
            availableColors = listOf(colorCream, colorSage, colorCharcoal),
            stockQuantity = 35,
            isFeatured = false,
            isPopular = true
        ),

        // 4. Men
        Product(
            id = "prod_4",
            name = "Tailored Wool Flannel Trousers",
            description = "Modern straight-leg trousers with crisp front pleats and adjustable side waist tabs. Cut from premium brushed wool flannel with subtle stretch.",
            categoryId = "cat_men",
            categoryName = "Men",
            price = 4599.0,
            originalPrice = 5499.0,
            imageUrl = "https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=800&q=80",
            rating = 4.8,
            reviewCount = 82,
            availableSizes = listOf("30", "32", "34", "36"),
            availableColors = listOf(colorCharcoal, colorNavy, colorBlack),
            stockQuantity = 18,
            isFeatured = false,
            isPopular = true
        ),

        // 5. Accessories
        Product(
            id = "prod_5",
            name = "Monochrome Minimalist Leather Tote",
            description = "Sculpted from full-grain calfskin with hand-painted raw edges. Spacious suede-lined interior with padded laptop sleeve and magnetic bridge closure.",
            categoryId = "cat_accessories",
            categoryName = "Accessories",
            price = 6499.0,
            originalPrice = 7999.0,
            imageUrl = "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=800&q=80",
            rating = 4.9,
            reviewCount = 315,
            availableSizes = listOf("Standard"),
            availableColors = listOf(colorBlack, colorCamel, colorTerracotta),
            stockQuantity = 12,
            isFeatured = true,
            isPopular = true
        ),

        // 6. Shoes
        Product(
            id = "prod_6",
            name = "Chunky Leather Chelsea Boots",
            description = "Bold contemporary silhouette set on a lightweight lugged Vibram sole. Elasticated side gussets and pull tabs for slip-on ease.",
            categoryId = "cat_shoes",
            categoryName = "Shoes",
            price = 5999.0,
            originalPrice = 7499.0,
            imageUrl = "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=800&q=80",
            rating = 4.6,
            reviewCount = 76,
            availableSizes = listOf("38", "39", "40", "41", "42", "43"),
            availableColors = listOf(colorBlack, colorCamel),
            stockQuantity = 9,
            isFeatured = false,
            isPopular = false
        ),

        // 7. Men / T-Shirts
        Product(
            id = "prod_7",
            name = "Heavyweight Organic Cotton Hoodie",
            description = "450 GSM organic French terry cotton with brushed interior. Relaxed drop-shoulder silhouette with kangaroo pocket and ribbed cuffs.",
            categoryId = "cat_men",
            categoryName = "Men",
            price = 3299.0,
            originalPrice = 3999.0,
            imageUrl = "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=800&q=80",
            rating = 4.9,
            reviewCount = 430,
            availableSizes = listOf("S", "M", "L", "XL", "XXL"),
            availableColors = listOf(colorNavy, colorCharcoal, colorSage),
            stockQuantity = 40,
            isFeatured = true,
            isPopular = true
        ),

        // 8. Women / Dresses
        Product(
            id = "prod_8",
            name = "Asymmetric Pleated Midi Dress",
            description = "Architectural sunray pleats catch light in motion. Cut from sustainable Japanese polyester georgette with high cowl neckline and self-tie sash.",
            categoryId = "cat_dresses",
            categoryName = "Dresses",
            price = 5499.0,
            originalPrice = 6999.0,
            imageUrl = "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=800&q=80",
            rating = 4.7,
            reviewCount = 59,
            availableSizes = listOf("XS", "S", "M", "L"),
            availableColors = listOf(colorTerracotta, colorCream, colorBlack),
            stockQuantity = 11,
            isFeatured = true,
            isPopular = false
        ),

        // 9. Dresses
        Product(
            id = "prod_9",
            name = "Silk Satin Bias-Cut Evening Dress",
            description = "Pure mulberry silk cut on the bias to gently hug the body. Features a sensual cowl neckline, delicate criss-cross back straps and a floor-skimming silhouette.",
            categoryId = "cat_dresses",
            categoryName = "Dresses",
            price = 6999.0,
            originalPrice = 8999.0,
            imageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=800&q=80",
            rating = 4.9,
            reviewCount = 88,
            availableSizes = listOf("XS", "S", "M", "L"),
            availableColors = listOf(colorWine, colorBlack, colorCream),
            stockQuantity = 15,
            isFeatured = true,
            isPopular = true
        ),

        // 10. Dresses
        Product(
            id = "prod_10",
            name = "Floral Tiered Bohemian Maxi Dress",
            description = "Breezy chiffon maxi dress featuring an ethereal floral print, delicate ruffle neckline, and tiered flounce skirt. Perfect for destination weddings and summer escapes.",
            categoryId = "cat_dresses",
            categoryName = "Dresses",
            price = 3799.0,
            originalPrice = 4999.0,
            imageUrl = "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=800&q=80",
            rating = 4.6,
            reviewCount = 64,
            availableSizes = listOf("S", "M", "L", "XL"),
            availableColors = listOf(colorSage, colorTerracotta),
            stockQuantity = 22,
            isFeatured = false,
            isPopular = true
        ),

        // 11. Dresses
        Product(
            id = "prod_11",
            name = "Sculptural Cut-Out Linen Midi Dress",
            description = "Pure Belgian linen crafted into a tailored fit-and-flare midi dress. Defined with modern waist cutouts and artisanal mother-of-pearl buttons.",
            categoryId = "cat_dresses",
            categoryName = "Dresses",
            price = 4599.0,
            originalPrice = 5999.0,
            imageUrl = "https://images.unsplash.com/photo-1496747611176-843222e1e57c?w=800&q=80",
            rating = 4.8,
            reviewCount = 42,
            availableSizes = listOf("XS", "S", "M"),
            availableColors = listOf(colorCream, colorOlive, colorBlack),
            stockQuantity = 17,
            isFeatured = false,
            isPopular = false
        ),

        // 12. Shirts / Men
        Product(
            id = "prod_12",
            name = "Italian Linen Mandarin Collar Shirt",
            description = "Crafted from 100% fine Italian linen. Features a band collar, French placket, and rounded barrel cuffs for polished warm-weather dressing.",
            categoryId = "cat_shirts",
            categoryName = "Shirts",
            price = 2799.0,
            originalPrice = 3499.0,
            imageUrl = "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=800&q=80",
            rating = 4.8,
            reviewCount = 145,
            availableSizes = listOf("38", "40", "42", "44"),
            availableColors = listOf(colorCream, colorNavy, colorSage),
            stockQuantity = 30,
            isFeatured = true,
            isPopular = true
        ),

        // 13. Shirts / Women
        Product(
            id = "prod_13",
            name = "Oversized Crisp Poplin Shirt",
            description = "Crisp 100% organic cotton poplin tailored with an architectural drop-shoulder cut, pointed collar, and elongated curved hemline.",
            categoryId = "cat_shirts",
            categoryName = "Shirts",
            price = 2499.0,
            originalPrice = 2999.0,
            imageUrl = "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=800&q=80",
            rating = 4.7,
            reviewCount = 98,
            availableSizes = listOf("XS", "S", "M", "L"),
            availableColors = listOf(colorCream, colorBlack, colorNavy),
            stockQuantity = 25,
            isFeatured = false,
            isPopular = true
        ),

        // 14. Shirts / Men
        Product(
            id = "prod_14",
            name = "Textured Oxford Formal Button-Down",
            description = "Heavy two-ply Oxford cloth woven for longevity. Classic button-down collar, box pleat with locker loop, and Mother-of-pearl buttons.",
            categoryId = "cat_shirts",
            categoryName = "Shirts",
            price = 2899.0,
            originalPrice = 3599.0,
            imageUrl = "https://images.unsplash.com/photo-1620012253295-c15c429f66bf?w=800&q=80",
            rating = 4.6,
            reviewCount = 112,
            availableSizes = listOf("38", "40", "42", "44"),
            availableColors = listOf(colorCream, colorNavy),
            stockQuantity = 28,
            isFeatured = false,
            isPopular = false
        ),

        // 15. Shirts / Women
        Product(
            id = "prod_15",
            name = "Fluid Silk Utility Pocket Shirt",
            description = "Soft mulberry silk with dual chest flap pockets and mother-of-pearl fasteners. A versatile luxury essential that bridges casual and smart.",
            categoryId = "cat_shirts",
            categoryName = "Shirts",
            price = 4299.0,
            originalPrice = 5499.0,
            imageUrl = "https://images.unsplash.com/photo-1598033129183-c4f50c736f10?w=800&q=80",
            rating = 4.9,
            reviewCount = 53,
            availableSizes = listOf("S", "M", "L"),
            availableColors = listOf(colorTerracotta, colorCream, colorOlive),
            stockQuantity = 14,
            isFeatured = true,
            isPopular = false
        ),

        // 16. T-Shirts / Men
        Product(
            id = "prod_16",
            name = "Heavyweight Classic Crewneck Tee",
            description = "280 GSM combed compact cotton built to withstand countless washes without losing its structured silhouette or neckline shape.",
            categoryId = "cat_tshirts",
            categoryName = "T-Shirts",
            price = 1299.0,
            originalPrice = 1699.0,
            imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=800&q=80",
            rating = 4.9,
            reviewCount = 520,
            availableSizes = listOf("S", "M", "L", "XL", "XXL"),
            availableColors = listOf(colorBlack, colorCream, colorCharcoal, colorNavy),
            stockQuantity = 60,
            isFeatured = true,
            isPopular = true
        ),

        // 17. T-Shirts / Women
        Product(
            id = "prod_17",
            name = "Supima Cotton Boxy Cropped Tee",
            description = "Crafted from long-staple California Supima cotton. Features a modern boxy cut, ribbed crewneck, and clean blind-stitched hem.",
            categoryId = "cat_tshirts",
            categoryName = "T-Shirts",
            price = 1499.0,
            originalPrice = 1899.0,
            imageUrl = "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=800&q=80",
            rating = 4.8,
            reviewCount = 184,
            availableSizes = listOf("XS", "S", "M", "L"),
            availableColors = listOf(colorCream, colorSage, colorTerracotta),
            stockQuantity = 45,
            isFeatured = false,
            isPopular = true
        ),

        // 18. T-Shirts / Men
        Product(
            id = "prod_18",
            name = "Minimalist Embroidered Mercerized Tee",
            description = "Double-mercerized Egyptian cotton gives this tee a subtle silk-like sheen and resistance to pilling. Discreet tone-on-tone logo embroidery at chest.",
            categoryId = "cat_tshirts",
            categoryName = "T-Shirts",
            price = 1799.0,
            originalPrice = 2299.0,
            imageUrl = "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=800&q=80",
            rating = 4.7,
            reviewCount = 92,
            availableSizes = listOf("M", "L", "XL"),
            availableColors = listOf(colorBlack, colorNavy, colorOlive),
            stockQuantity = 32,
            isFeatured = false,
            isPopular = false
        ),

        // 19. T-Shirts / Women
        Product(
            id = "prod_19",
            name = "Relaxed Slub Linen Blend Tee",
            description = "Natural linen blended with organic cotton provides breathable, textured comfort with subtle slub yarn character and effortless drape.",
            categoryId = "cat_tshirts",
            categoryName = "T-Shirts",
            price = 1599.0,
            originalPrice = 1999.0,
            imageUrl = "https://images.unsplash.com/photo-1529374255404-311a2a4f1fd9?w=800&q=80",
            rating = 4.6,
            reviewCount = 77,
            availableSizes = listOf("XS", "S", "M", "L"),
            availableColors = listOf(colorCream, colorCamel, colorSage),
            stockQuantity = 28,
            isFeatured = false,
            isPopular = false
        ),

        // 20. Jeans / Men
        Product(
            id = "prod_20",
            name = "Japanese Selvedge Slim Tapered Jeans",
            description = "13.5 oz raw Kurabo selvedge denim with classic red-line id. Button fly with custom copper hardware that develops personalized honeycombs over time.",
            categoryId = "cat_jeans",
            categoryName = "Jeans",
            price = 4999.0,
            originalPrice = 6499.0,
            imageUrl = "https://images.unsplash.com/photo-1542272604-780c96856592?w=800&q=80",
            rating = 4.9,
            reviewCount = 210,
            availableSizes = listOf("30", "32", "34", "36"),
            availableColors = listOf(colorIndigo, colorBlack),
            stockQuantity = 24,
            isFeatured = true,
            isPopular = true
        ),

        // 21. Jeans / Women
        Product(
            id = "prod_21",
            name = "Vintage Straight High-Rise Jeans",
            description = "High-rise fit engineered from 99% organic cotton with 1% elastane for authentic vintage denim texture without sacrificing all-day movement.",
            categoryId = "cat_jeans",
            categoryName = "Jeans",
            price = 3499.0,
            originalPrice = 4299.0,
            imageUrl = "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=800&q=80",
            rating = 4.8,
            reviewCount = 168,
            availableSizes = listOf("26", "28", "30", "32"),
            availableColors = listOf(colorIndigo, colorCharcoal, colorCream),
            stockQuantity = 38,
            isFeatured = false,
            isPopular = true
        ),

        // 22. Jeans / Men
        Product(
            id = "prod_22",
            name = "Relaxed Tapered Washed Indigo Denim",
            description = "Artisanal hand-sanded wash on 12 oz denim. Generous ease through the thigh tapering down to a clean ankle opening.",
            categoryId = "cat_jeans",
            categoryName = "Jeans",
            price = 3799.0,
            originalPrice = 4699.0,
            imageUrl = "https://images.unsplash.com/photo-1565084888279-aca607ecce0c?w=800&q=80",
            rating = 4.7,
            reviewCount = 115,
            availableSizes = listOf("30", "32", "34", "36"),
            availableColors = listOf(colorIndigo, colorCharcoal),
            stockQuantity = 29,
            isFeatured = false,
            isPopular = false
        ),

        // 23. Jeans / Women
        Product(
            id = "prod_23",
            name = "Wide-Leg Full Length Trouser Jeans",
            description = "Elongating wide-leg silhouette in premium Italian cotton denim. Deep trouser hem and sleek welt back pockets.",
            categoryId = "cat_jeans",
            categoryName = "Jeans",
            price = 3999.0,
            originalPrice = 4999.0,
            imageUrl = "https://images.unsplash.com/photo-1582418702059-97ebafb35d09?w=800&q=80",
            rating = 4.8,
            reviewCount = 89,
            availableSizes = listOf("26", "28", "30", "32"),
            availableColors = listOf(colorIndigo, colorCream),
            stockQuantity = 21,
            isFeatured = true,
            isPopular = false
        ),

        // 24. Jackets / Men
        Product(
            id = "prod_24",
            name = "Genuine Nappa Lambskin Biker Jacket",
            description = "Handcrafted from buttery-soft full-grain lambskin with asymmetrical silver-tone hardware, action back pleats, and quilted satin lining.",
            categoryId = "cat_jackets",
            categoryName = "Jackets",
            price = 14999.0,
            originalPrice = 18999.0,
            imageUrl = "https://images.unsplash.com/photo-1520975916090-3105956dac38?w=800&q=80",
            rating = 5.0,
            reviewCount = 98,
            availableSizes = listOf("S", "M", "L", "XL"),
            availableColors = listOf(colorBlack, colorCharcoal),
            stockQuantity = 8,
            isFeatured = true,
            isPopular = true
        ),

        // 25. Shoes / Men
        Product(
            id = "prod_25",
            name = "Handcrafted Goodyear Oxford Brogues",
            description = "Full-grain calfskin with subtle wingtip brogue perforations, Goodyear welted construction, and stacked leather sole.",
            categoryId = "cat_shoes",
            categoryName = "Shoes",
            price = 6999.0,
            originalPrice = 8499.0,
            imageUrl = "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=800&q=80",
            rating = 4.9,
            reviewCount = 140,
            availableSizes = listOf("40", "41", "42", "43", "44"),
            availableColors = listOf(colorBlack, colorCamel),
            stockQuantity = 15,
            isFeatured = true,
            isPopular = true
        ),

        // 26. Shoes
        Product(
            id = "prod_26",
            name = "Minimalist Leather Low-Top Court Sneakers",
            description = "Italian calf leather upper with padded collar, waxed tonal laces, and stitched Margom rubber cupsole.",
            categoryId = "cat_shoes",
            categoryName = "Shoes",
            price = 4499.0,
            originalPrice = 5499.0,
            imageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=800&q=80",
            rating = 4.8,
            reviewCount = 310,
            availableSizes = listOf("39", "40", "41", "42", "43", "44"),
            availableColors = listOf(colorCream, colorBlack),
            stockQuantity = 34,
            isFeatured = false,
            isPopular = true
        ),

        // 27. Accessories
        Product(
            id = "prod_27",
            name = "Polarized Acetate Cat-Eye Sunglasses",
            description = "Hand-cut Italian Mazzucchelli acetate frames fitted with scratch-resistant category 3 polarized lenses offering 100% UVA/UVB protection.",
            categoryId = "cat_accessories",
            categoryName = "Accessories",
            price = 2999.0,
            originalPrice = 3999.0,
            imageUrl = "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800&q=80",
            rating = 4.7,
            reviewCount = 145,
            availableSizes = listOf("Standard"),
            availableColors = listOf(colorBlack, colorCamel),
            stockQuantity = 22,
            isFeatured = false,
            isPopular = true
        ),

        // 28. Accessories
        Product(
            id = "prod_28",
            name = "Full-Grain Leather Minimalist Cardholder",
            description = "Hand-stitched vegetable-tanned leather featuring four external card slots and a central bill compartment. Ages beautifully with a rich patina.",
            categoryId = "cat_accessories",
            categoryName = "Accessories",
            price = 1899.0,
            originalPrice = 2499.0,
            imageUrl = "https://images.unsplash.com/photo-1627123424574-724758594e93?w=800&q=80",
            rating = 4.8,
            reviewCount = 230,
            availableSizes = listOf("Standard"),
            availableColors = listOf(colorCamel, colorBlack, colorTerracotta),
            stockQuantity = 40,
            isFeatured = false,
            isPopular = true
        )
    )

    val sampleAddress = ShippingAddress(
        fullName = "Ananya Sharma",
        street = "Flat 402, Signature Towers, Golf Course Road",
        city = "Gurugram",
        state = "Haryana",
        zipCode = "122002",
        phone = "+91 98765 43210"
    )

    val sampleUser = UserProfile(
        id = "user_guest",
        name = "Ananya Sharma",
        email = "ananya.sharma@aurafashion.in",
        phone = "+91 98765 43210",
        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&q=80",
        membershipTier = "Black Tier VIP",
        defaultAddress = sampleAddress
    )

    val sampleCartItems = listOf(
        CartItem(
            id = "cart_1",
            product = products[0],
            selectedSize = "M",
            selectedColor = products[0].availableColors[0],
            quantity = 1
        ),
        CartItem(
            id = "cart_2",
            product = products[4],
            selectedSize = "Standard",
            selectedColor = products[4].availableColors[1],
            quantity = 1
        )
    )

    val sampleCart = sampleCartItems

    val sampleOrders = listOf(
        Order(
            id = "ord_101",
            orderNumber = "AUR-92841",
            date = "Oct 2, 2026",
            items = listOf(
                CartItem(
                    id = "oi_1",
                    product = products[1],
                    selectedSize = "M",
                    selectedColor = products[1].availableColors[0],
                    quantity = 1
                ),
                CartItem(
                    id = "oi_2",
                    product = products[2],
                    selectedSize = "S",
                    selectedColor = products[2].availableColors[1],
                    quantity = 2
                )
            ),
            subtotal = 14997.0,
            shipping = 0.0,
            totalAmount = 14997.0,
            status = OrderStatus.PROCESSING,
            shippingAddress = sampleUser.defaultAddress
        ),
        Order(
            id = "ord_100",
            orderNumber = "AUR-87123",
            date = "Sep 18, 2026",
            items = listOf(
                CartItem(
                    id = "oi_3",
                    product = products[6],
                    selectedSize = "L",
                    selectedColor = products[6].availableColors[0],
                    quantity = 1
                )
            ),
            subtotal = 3299.0,
            shipping = 149.0,
            totalAmount = 3448.0,
            status = OrderStatus.DELIVERED,
            shippingAddress = sampleUser.defaultAddress
        )
    )
}
