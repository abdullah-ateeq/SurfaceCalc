package com.example.surfacecalc.data.model

sealed interface SurfaceCalcError {
    data object EmptyInput : SurfaceCalcError
    data object InvalidNumber : SurfaceCalcError
    data object NonPositiveValue : SurfaceCalcError
    data object InputTooLong : SurfaceCalcError
    data object MissingDimension : SurfaceCalcError
    data object InvalidTriangle : SurfaceCalcError
    data object NegativeSquareRoot : SurfaceCalcError

    fun userMessage(fieldName: String? = null): String {
        return when (this) {
            is EmptyInput -> if (fieldName != null) "Enter a value for $fieldName." else "Enter a valid dimension."
            is InvalidNumber -> "Enter a valid dimension."
            is NonPositiveValue -> if (fieldName != null) "$fieldName must be greater than zero." else "Value must be greater than zero."
            is InputTooLong -> "Number is too long. Maximum 500 digits."
            is MissingDimension -> if (fieldName != null) "Please enter $fieldName." else "Please enter all required dimensions."
            is InvalidTriangle -> "These side lengths cannot form a valid triangle."
            is NegativeSquareRoot -> "Cannot compute square root of a negative value."
        }
    }
}
