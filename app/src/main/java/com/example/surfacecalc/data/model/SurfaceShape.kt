package com.example.surfacecalc.data.model

enum class SurfaceShape(
    val displayName: String,
    val dimensionCount: Int,
    val description: String
) {
    CUBE("Cube", 1, "1 dimension"),
    RECTANGULAR_PRISM("Rectangular Prism", 3, "3 dimensions"),
    CYLINDER("Cylinder", 2, "2 dimensions"),
    SPHERE("Sphere", 1, "1 dimension"),
    CONE("Cone", 2, "2 dimensions"),
    SQUARE_PYRAMID("Square Pyramid", 2, "2 dimensions"),
    TRIANGULAR_PRISM("Triangular Prism", 4, "4 dimensions"),
    HEMISPHERE("Hemisphere", 1, "1 dimension")
}
