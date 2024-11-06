package com.limyusontudtud.souvseek

class DataClass() {
    var dataTitle: String? = null
        get() = field
        set(value) {
            field = value
        }

    var dataImage: String? = null
        get() = field
        set(value) {
            field = value
        }

    var key: String? = null
        get() = field
        set(value) {
            field = value
        }

    constructor(dataTitle: String, dataImage: String) : this() {
        this.dataTitle = dataTitle
        this.dataImage = dataImage
    }
}

